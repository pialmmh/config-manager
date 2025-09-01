# ConfigManager Integration for RouteSphere

## Overview
This document describes how RouteSphere integrates with the RTC-Manager/ConfigManager microservice to load the tenant hierarchy.

## Components Created

### 1. DTO Classes (`com.telcobright.routesphere.tenant.dto`)
- **ConfigManagerTenant**: Matches the structure of `freeswitch.config.dynamic.Tenant`
- **TenantProfileDTO**: Simplified version of TenantProfile
- **DynamicContextDTO**: Contains partner and rate plan information
- **AllCacheDTO**: Contains package account cache

### 2. TenantMapper (`com.telcobright.routesphere.tenant.TenantMapper`)
- Converts ConfigManager's tenant structure to RouteSphere's TenantHierarchy
- Maps hierarchy levels based on depth (ROOT → RESELLER_L1-L5 → END_USER)
- Extracts properties from profile data

### 3. Updated TenantHierarchyInitializer
The startup class now:
1. Attempts to call ConfigManager API at startup
2. Falls back to mock data if API is unavailable
3. Converts received data to RouteSphere format
4. Populates the TenantHierarchyService

## Configuration

### Environment Variables
```bash
# ConfigManager API URL (default: http://localhost:8080)
CONFIG_MANAGER_URL=http://localhost:8080

# Use mock data when ConfigManager is unavailable (default: false)
USE_MOCK_TENANT_DATA=true
```

### Application Properties
```properties
# ConfigManager API Settings
config.manager.url=http://localhost:8080
config.manager.tenant.endpoint=/get-tenant-root
config.manager.timeout=30000

# Use mock data when ConfigManager is not available
tenant.use.mock.data=true
```

## API Integration

### ConfigManager API Endpoint
- **URL**: `POST http://localhost:8080/get-tenant-root`
- **Response**: JSON structure of tenant hierarchy starting from root

### Data Flow
1. TenantHierarchyInitializer calls ConfigManager API on startup
2. Receives ConfigManagerTenant object (root with nested children)
3. TenantMapper converts to RouteSphere TenantHierarchy
4. Each tenant is mapped with appropriate level based on depth
5. Properties and metadata are preserved

## Testing

### Run ConfigManager Integration Demo
```bash
mvn exec:java -Dexec.mainClass="com.telcobright.routesphere.startup.ConfigManagerIntegrationDemo"
```

### Test with Mock Data
```bash
USE_MOCK_TENANT_DATA=true mvn exec:java -Dexec.mainClass="com.telcobright.routesphere.startup.ConfigManagerIntegrationDemo"
```

## Tenant Level Mapping

| Depth | RouteSphere Level | Description |
|-------|------------------|-------------|
| 0 | ROOT | Root organization |
| 1 | RESELLER_L1 | Primary reseller |
| 2 | RESELLER_L2 | Regional reseller |
| 3 | RESELLER_L3 | Local reseller |
| 4 | RESELLER_L4 | Sub-reseller |
| 5 | RESELLER_L5 | Micro-reseller |
| 6+ | END_USER | End customer |

## Error Handling

The integration handles several error scenarios:
1. **ConfigManager unavailable**: Falls back to mock data if configured
2. **Invalid response**: Logs error and uses empty hierarchy
3. **Parsing errors**: Caught and logged with fallback options

## Dependencies Added

```xml
<!-- Jackson for JSON parsing -->
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
    <version>2.15.2</version>
</dependency>

<!-- Apache HttpClient for REST calls -->
<dependency>
    <groupId>org.apache.httpcomponents.client5</groupId>
    <artifactId>httpclient5</artifactId>
    <version>5.2.1</version>
</dependency>

<!-- Quarkus REST Client -->
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-rest-client-reactive-jackson</artifactId>
    <version>${quarkus.platform.version}</version>
</dependency>
```

## Usage in Application

Once the tenant hierarchy is loaded, it can be accessed via dependency injection:

```java
@Inject
TenantHierarchyService tenantHierarchyService;

// Get tenant by ID
Tenant tenant = tenantHierarchyService.getTenant("customer1");

// Get tenant path from root
List<Tenant> path = tenantHierarchyService.getPathFromRoot("customer1");

// Get all descendants
List<Tenant> descendants = tenantHierarchyService.getDescendants("reseller1");
```

## Next Steps

1. Ensure ConfigManager microservice is running on port 8080
2. Update CONFIG_MANAGER_URL if using different host/port
3. Test integration with actual ConfigManager data
4. Monitor startup logs for successful tenant loading
5. Use TenantHierarchyResource REST endpoints to verify loaded data