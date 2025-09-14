# SMS-Softswitch Billing System Plan

This document provides step-by-step plans for implementing the **prepaid billing system** in the SMS-Softswitch project.  
The AI coding agent can access the mentioned **entities** and **fields** to understand the required services, flows, and persistence logic.  

---

## 🔹 Summary of Flows (High-Level)

The prepaid billing system manages **packages, top-ups, balance reservation, and call/SMS charging** for partners.  

- **Package Creation** → Admins define reusable packages consisting of multiple `PackageItem`s (minutes, SMS, etc.) with restrictions such as `prefix` and `validity`.  
- **Package Purchase** → When a partner purchases a package, system creates purchase records (`PackagePurchase`) and initializes balances in `PackageAccount`.  
- **Top-Up** → Works as a special one-item package, always having lowest priority in usage order.  
- **Call/SMS Initiation** → On each request, system checks available `PackageAccount`s (from cache), reserves the required amount (per minute or per SMS), and records it in `PackageAccountReserve`.  
- **Call/SMS Termination** → On session completion, system finalizes billing: refunds unused reserved units, updates balances, and creates a `CDR` entry.  
- **Caching Strategy** → Active balances (`PackageAccount`s) are always cached per partner for fast access. Cache refreshes on purchase, top-up, or expiry events.  

This ensures **real-time charging**, **fair resource usage**, and **scalable partner-level accounting** for SMS and VoIP services.

---

## 1. Package Creation

### Entities:
- `Package` (fields: `id`, `name`, `price`, `VAT`, `validity`, etc.)
- `PackageItem` (fields: `id`, `id_package`, `quantity`, `id_uom`, `prefix`)

### Logic:
1. A **Package** can contain multiple `PackageItem`s.  
   - Example:  
     - Package A → 100 minutes, 200 SMS, valid 30 days.
2. Each `PackageItem` defines:  
   - `quantity` → total units (minutes/SMS).  
   - `id_uom` → unit of measurement (minute, SMS, taka).  
   - `prefix` → allowed region/prefix restriction.

---

## 2. Package Purchase

### Entities:
- `PackagePurchase` (fields: `id`, `id_package`, `id_partner`, `purchaseDate`, `expiryDate`, `priority`)
- `PackageAccount` (fields: `id`, `id_packagePurchase`, `id_packageItem`, `balanceBefore`, `balanceAfter`, `lastAmount`)

### Logic:
1. When a partner buys a package:  
   - Create a record in `PackagePurchase` (linking `id_partner` + `id_package`).  
   - Assign `priority`:  
     - Lower priority → top-ups.  
     - Higher priority → regular packages (earliest expiry first).
2. For each `PackageItem` in the package:  
   - Create a corresponding `PackageAccount`.  
   - Initialize balances:  
     - `balanceBefore = 0`  
     - `lastAmount = item.quantity`  
     - `balanceAfter = item.quantity`

---

## 3. Top-Up

### Entities:
- Same as `PackagePurchase` + `PackageAccount`.

### Logic:
1. A **Top-Up** is a special package with a single `PackageItem`.  
2. Purchase flow is identical to package purchase.  
3. Ensure top-ups always get **lowest priority** in `PackagePurchase`.

---

## 4. Call/SMS Initiation

### Entities:
- `PackageAccountReserve` (fields: `id`, `id_packageAccount`, `reservedAmount`, `sessionId`, `timestamp`)

### Logic:
1. When a call/SMS starts:  
   - Fetch all active `PackageAccount`s for the partner from **cache** (`dbCache: Map<Long, PackageAccount>`).  
   - Sort accounts by `PackagePurchase.priority` (expiry date → ascending, top-ups last).
2. Deduct the **minimum required balance**:  
   - Calls → cost of 1 minute.  
   - SMS → cost of 1 SMS.  
3. Create a `PackageAccountReserve` record:  
   - `reservedAmount = deductedAmount`.  
   - `sessionId = callId / smsId`.  
   - Link to `PackageAccount`.  
4. For ongoing calls:  
   - Update the existing `PackageAccountReserve` entry (increment `reservedAmount` per additional minute).

---

## 5. Call/SMS Termination

### Entities:
- `PackageAccountReserve`
- `CDR` (fields: `id`, `id_partner`, `id_packageAccount`, `sessionId`, `duration`, `amountDeducted`, `timestamp`)

### Logic:
1. When call/SMS ends (or fails):  
   - Refund unused reserved balance back to the corresponding `PackageAccount`.  
   - Delete the entry from `PackageAccountReserve`.  
   - Create a `CDR` with final usage details:
     - `duration`  
     - `amountDeducted`  
     - `packageAccountId`  
     - `partnerId`

---

## 6. Caching Strategy

- Maintain `dbCache: Map<Long, List<PackageAccount>>` keyed by `id_partner`.  
- On package purchase/top-up/expiry:  
  - Reload the cache for that partner.  
- Always use cache during call/SMS flow for performance.  

