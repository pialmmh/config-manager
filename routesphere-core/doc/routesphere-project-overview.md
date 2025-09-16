# RouteSphere Project Overview

## Project Location
- **Main Project**: `/home/mustafa/telcobright-projects/routesphere/routesphere-core`
- **ConfigManager Source**: `/home/mustafa/telcobright-projects/routesphere/RTC-Manager/ConfigManager`

## Current State (As of 2025-09-14)

### What We Have
1. **Quarkus-based microservice** (routesphere-core) that needs to integrate with a Spring Boot application (ConfigManager)
2. **Domain models synchronized** from ConfigManager to routesphere-core in `com.telcobright.rtc.domainmodel` package
3. **API integration setup** to receive Tenant hierarchy from ConfigManager

### Key Components

#### 1. Domain Model Classes
- **Location**: `routesphere-core/src/main/java/com/telcobright/rtc/domainmodel/`
- **Contents**:
  - `nonentity/Tenant.java` - Main tenant hierarchy class
  - `nonentity/TenantProfile.java` - Tenant profile with configuration
  - `nonentity/DynamicContext.java` - Runtime context for tenant
  - `nonentity/AllCache.java` - Cache management
  - `mysqlentity/*` - All JPA entities (Partner, Dialplan, Route, etc.)

#### 2. ConfigManager Integration
- **API Endpoint**: `/get-tenant-root` (POST)
- **Integration Class**: `TenantHierarchyInitializer.java`
- **Purpose**: Fetches Tenant object from ConfigManager on startup
- **ConfigManager API**: Exposed by `ConfigManager/src/main/java/freeswitch/controller/FsController.java`

#### 3. Configuration Structure
```
routesphere-core/src/main/resources/config/tenants/
└── ccl/
    ├── dev/
    ├── prod/
    ├── staging/
    └── mock/
        └── protocol-instances/
            ├── esl/
            ├── http/
            └── sip/
```

## Current Goals

### Primary Goal
**Successfully receive and deserialize the Tenant object from ConfigManager's API**
- The Tenant object contains the complete hierarchy of tenants with parent-child relationships
- Each Tenant has a TenantProfile with database configuration, cache, and context

### Immediate Tasks
1. **Test the API integration**
   - Start ConfigManager on port 8080 (or configured port)
   - Ensure `/get-tenant-root` endpoint is accessible
   - Verify Tenant object deserialization works

2. **Use the Tenant hierarchy**
   - Once received, use the Tenant object to:
     - Identify tenant relationships
     - Access tenant-specific configurations
     - Route requests based on tenant context

3. **Fix any remaining integration issues**
   - Handle serialization/deserialization mismatches
   - Ensure all required fields are properly mapped
   - Add proper error handling

### Technical Context

#### Dependencies Added
```xml
<!-- In routesphere-core/pom.xml -->
- quarkus-hibernate-orm
- jakarta.persistence-api
- lombok
- Jackson for JSON processing
- Apache HttpClient for API calls
```

#### Stub Classes Created
Located in `routesphere-core/src/main/java/com/telcobright/rtc/`:
- `freeswitch/DataLoader.java`
- `freeswitch/AllCacheLoader.java`
- `service/DynamicDatabaseService.java`
- `GlobalTenantRegistry.java`

These are placeholder implementations to allow compilation.

## Architecture Notes

### Multi-Tenant Forest Architecture
- **Root Tenant**: Top-level organization
- **Reseller Levels**: L1-L5 hierarchical resellers
- **End Users**: Leaf nodes in the hierarchy
- Each tenant has its own database (multi-tenant with database isolation)

### Communication Flow
1. RouteSphere starts up
2. `TenantHierarchyInitializer` calls ConfigManager API
3. Receives Tenant object with full hierarchy
4. Stores for routing decisions

## Known Issues & Constraints

1. **Removed Components** (to fix compilation):
   - TenantHierarchy wrapper class (not needed)
   - Various pipeline and routing classes that had incorrect dependencies
   - These may need to be recreated with correct implementations

2. **Mock vs Real Data**:
   - In "mock" profile, API call is skipped
   - In other profiles, attempts to fetch from ConfigManager

3. **MySQL Configuration**:
   - Database: `127.0.0.1:3306` (not localhost)
   - Credentials: `root/123456`
   - Running in LXD container

## Next Steps for Development

1. **Verify API Integration**:
   ```bash
   # Start ConfigManager
   cd /path/to/ConfigManager
   mvn spring-boot:run

   # Start RouteSphere
   cd /home/mustafa/telcobright-projects/routesphere/routesphere-core
   mvn quarkus:dev
   ```

2. **Check logs for successful Tenant reception**

3. **Implement business logic** using the received Tenant hierarchy

4. **Add proper routing logic** based on tenant identification

## Files to Focus On
- `TenantHierarchyInitializer.java` - Main integration point
- `ConfigManagerService.java` - Service that also receives Tenant
- `Tenant.java` in domainmodel/nonentity - The core domain object

## Testing the Integration
```bash
# Test the ConfigManager endpoint directly
curl -X POST http://localhost:8080/get-tenant-root \
  -H "Content-Type: application/json" \
  -H "Accept: application/json"
```

## Contact & Context
This project is part of the TelcoBright RTC (Real-Time Communication) system, implementing a multi-tenant routing sphere for telecom operations.