# Call Admission and Partner Identification in ESL

This document explains how the **Call Admission** process works in our ESL (Event Socket Library) system and how **partners** are identified during a call.

---

## 1. Overview

When ESL receives a **CHANNEL_PARK** event, it triggers the `performCallAdmission()` method in the `CallAdmissionController` class.

The purpose of this method is to identify the **partner** associated with the call and manage call balance/reservation.

---

## 2. Call Admission Workflow

1. ESL receives a `CHANNEL_PARK` event.
2. `performCallAdmission()` is called with the event details.
3. The system tries to identify the partner using the following rules (in order):
    - **Partner by SIP Account**
    - **Partner by IP**
    - other rules may applied later
4. If a partner is found:
    - The system checks if **balance needs to be reserved**.
    - Updates call state with partner information if required.
    - Then do the **UUID_transfer** By FreeSwitch
5. If no partner is found, the call is **dropped**.

---

## 3. Partner Identification Rules

### 3.1 Partner by SIP Account

- Get `callerNumber` from the `ChargingEvent` object.
- Look up the database name (`dbName`) of the current reseller using `didNumberVsDbName`.
- Using `dbName` and `callerNumber`, fetch the partner from `sipAccountWisePartners`.

> **Note:** If the `callerNumber` is not found in the database, the call is dropped.



### 3.2 Partner by IP

- Get `callerNumber` from the `ChargingEvent`.
- Extract the caller’s IP from `VariableVertoUser` or `VariableSipFromUri`.
- Check the call type:
    - **Incoming calls:** Identify partner by IP directly.
    - **Outgoing calls:** Validate number prefixes and length before identifying partner.


---

## 4. Error Handling

- If a partner cannot be identified, the system logs an error and **drops the call**.
- Specific cases like missing DID or invalid caller number are logged for troubleshooting.

---

### 5. Summary

- ESL handles call admission by identifying partners using **SIP accounts** and **IP addresses**.
- Calls without a valid partner are automatically dropped.
- Balance reservation and prepaid checks are performed for identified partners.
- The system ensures only authorized partners can initiate or receive calls.




