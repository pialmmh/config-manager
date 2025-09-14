# Multitenant System Architecture Documentation

## Overview
This is a multi-database tenant architecture where each tenant maintains its own database with identical schemas. The system uses a forest-tree structure to organize tenants and their resellers in a hierarchical manner.

## Core Concepts

### 1. Tenant Forest Structure
- **Forest**: Collection of root tenants (e.g., ccl, brilliant, telcobright)
- **Root Tenants**: Top-level administrative databases (ccl, brilliant, telcobright, etc.)
- **Tree Structure**: Each root tenant can have multiple levels of resellers forming a tree

### 2. Database Naming Convention
- **Root databases**: Use tenant name directly (e.g., `ccl`, `brilliant`, `telcobright`)
- **Reseller databases**: Follow pattern `res_grandGrandParentId_grandParentId_parentId_partnerId`
- **Example**: `res_2_6` means reseller with partnerId=6 under parent with partnerId=2

### 3. Partner vs Reseller Terminology
- **Partner**: General term for clients (stored in `partner` table in each database)
- **Reseller**: Special type of partner where `Partner.type = 4`
- **Reseller Significance**: When `Partner.type = 4`, that partner gets their own dedicated database

## Data Structures

### Tenant Class
```java
public class Tenant {
    private final String dbName;           // Database name for this tenant
    private String parent;                 // Parent tenant reference
    private final Map<String, Tenant> children = new HashMap<>(); // Child tenants
    private TenantProfile profile;         // Cached data for fast access
    
    public Tenant(String dbName) {
        this.dbName = dbName;
    }
    
    public void addChild(String childDbName, Tenant child) {
        this.children.put(childDbName, child);
    }
}
```

### TenantProfile Class
- Contains all cached data needed for fast system access
- Specific fields not detailed in source but critical for performance

## Caching Strategy

### 1. Partner-to-Database Lookup Map
- **Type**: `HashMap<partnerId, lookupDbName>`
- **Purpose**: Quick lookup to find which database contains a specific partner
- **Example**: `(60, "res_2_6")` means partner with ID 60 is found in database `res_2_6`
- **Uniqueness**: Partner IDs are unique across all databases within the same tenant tree

### 2. Forest Cache
- **Content**: Complete tenant forest structure loaded at startup
- **Access**: Available via API endpoint
- **Structure**: Tree of Tenant objects with parent-child relationships

## Operational Workflow

### ESL Event Processing
When an ESL (Event Socket Library) event occurs during a call:

1. **Input Data Extraction**:
   - `partnerId`: Identifies the partner making the call (e.g., 60)
   - `ipAddr`: Determines root tenant (e.g., 103.87.98.04 → ccl)

2. **Root Tenant Identification**:
   - Map IP address to root tenant name
   - Example: `103.87.98.04` maps to `ccl` tenant

3. **Database Lookup**:
   - Use partnerId in the lookup map to find target database
   - Example: partnerId `60` maps to database `res_2_6`

4. **Path Resolution**:
   - From database name `res_2_6`, traverse the tenant tree
   - Find path from root (`ccl`) to target tenant (`res_2_6`)
   - Access the TenantProfile for that tenant

5. **Processing**:
   - Use TenantProfile data for further call processing
   - All necessary cached data is now available

## Example Tenant Trees

### CCL Tenant Tree
```
ccl (root)
├── res_1
│   ├── res_1_3
│   └── res_1_4
└── res_2
    ├── res_2_5
    └── res_2_6
```

### Brilliant Tenant Tree
```
brilliant (root)
├── res_1
│   ├── res_1_3
│   └── res_1_4
└── res_2
    ├── res_2_5
    └── res_2_6
```


