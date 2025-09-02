# RouteSphere ConfigManager Integration

## Overview
The ConfigManager package provides a Quarkus-based configuration management system that:
1. Listens to Kafka topics for configuration change notifications (via Debezium CDC)
2. Reloads tenant configuration hierarchy when changes are detected
3. Exposes REST APIs for other clients to fetch configuration
4. Maintains a global registry of all tenants

## Architecture

### Components

#### 1. **Data Models** (`configmanager.model`)
- `ConfigTenant`: Represents a tenant in the hierarchy
- `ConfigTenantProfile`: Contains tenant-specific configuration (database, Kafka, Redis)
- `GlobalTenantRegistry`: Registry for quick tenant lookup

#### 2. **Kafka Consumer** (`configmanager.consumer`)
- `ConfigChangeConsumer`: Listens to Kafka topics for configuration updates
  - Topic: `config_event_loader` - Configuration change notifications
  - Topic: `all-mysql-changes` - Direct Debezium CDC events (optional)

#### 3. **Service Layer** (`configmanager.service`)
- `ConfigManagerService`: Core service managing configuration loading and caching
  - Loads configuration from ConfigManager API or uses mock data
  - Maintains tenant hierarchy
  - Handles configuration reloading

#### 4. **REST Endpoints** (`configmanager.resource`)
- `ConfigManagerResource`: RESTful API for configuration access

## REST API Endpoints

### Get Root Tenant Hierarchy
```http
POST /api/config/get-tenant-root
Content-Type: application/json

Response: Complete tenant hierarchy starting from root
```

### Get Global Tenant Registry
```http
POST /api/config/get-global-tenant-registry
Content-Type: application/json

Response: Global registry with all registered tenants
```

### Get Specific Tenant
```http
GET /api/config/tenant/{dbName}

Response: Specific tenant configuration
```

### Reload Configuration
```http
POST /api/config/reload
Content-Type: application/json

Response: {"status": "Configuration reloaded successfully"}
```

### Health Check
```http
GET /api/config/health

Response: 
{
  "status": "UP",
  "rootTenant": "ccl",
  "totalTenants": 5
}
```

### Statistics
```http
GET /api/config/stats

Response:
{
  "totalTenants": 5,
  "rootTenants": 1,
  "resellers": 3,
  "endUsers": 1,
  "activeTenant": "ccl"
}
```

## Configuration Flow

### 1. Database Change Detection
```
Database Change → Debezium → Kafka (all-mysql-changes)
```

### 2. Configuration Reload
```
ConfigReloader (Original) → Kafka (config_event_loader) → ConfigChangeConsumer
```

### 3. Configuration Update
```
ConfigChangeConsumer → ConfigManagerService.reloadConfiguration()
```

### 4. Client Access
- **Push Model**: Clients consume from `config_event_loader` topic
- **Pull Model**: Clients call REST API endpoints

## Kafka Configuration

### Application Properties
```properties
# ConfigManager API
configmanager.api.url=http://localhost:7070
configmanager.api.enabled=true

# Kafka Bootstrap Servers
kafka.bootstrap.servers=localhost:9092

# Config Updates Channel
mp.messaging.incoming.config-updates.connector=smallrye-kafka
mp.messaging.incoming.config-updates.topic=config_event_loader
mp.messaging.incoming.config-updates.group.id=routesphere-config-group
mp.messaging.incoming.config-updates.auto.offset.reset=latest

# Database Changes Channel (Optional)
mp.messaging.incoming.db-changes.connector=smallrye-kafka
mp.messaging.incoming.db-changes.topic=all-mysql-changes
mp.messaging.incoming.db-changes.group.id=routesphere-db-group
mp.messaging.incoming.db-changes.auto.offset.reset=latest
```

## Tenant Hierarchy

The system maintains a hierarchical structure:
```
ROOT (ccl)
├── RESELLER_L1 (ccl_premium)
│   └── RESELLER_L2 (ccl_premium_east)
│       └── END_USER (ccl_premium_east_customer1)
└── RESELLER_L1 (ccl_standard)
```

### Tenant Types
- `ROOT`: Top-level organization
- `RESELLER_L1` to `RESELLER_L5`: Multi-level reseller hierarchy
- `END_USER`: Final customer/tenant

## Tenant Profile Configuration

Each tenant has a profile containing:

### Database Configuration
```java
- url: JDBC connection URL
- username: Database username
- password: Database password
- driver: JDBC driver class
- maxPoolSize: Connection pool size
```

### Kafka Configuration
```java
- bootstrapServers: Kafka broker addresses
- groupId: Consumer group ID
- securityProtocol: Security protocol (optional)
- properties: Additional Kafka properties
```

### Redis Configuration
```java
- host: Redis server host
- port: Redis server port
- password: Redis password (optional)
- database: Redis database number
- maxConnections: Maximum connections
```

## Usage Examples

### Java Client
```java
@Inject
ConfigManagerService configManagerService;

// Get root tenant
ConfigTenant root = configManagerService.getRootTenant();

// Get specific tenant
ConfigTenant tenant = configManagerService.getTenantByDbName("ccl_premium");

// Access tenant profile
ConfigTenantProfile profile = tenant.getProfile();
DatabaseConfig dbConfig = profile.getDatabaseConfig();
```

### REST Client
```bash
# Get root tenant hierarchy
curl -X POST http://localhost:8081/api/config/get-tenant-root \
  -H "Content-Type: application/json"

# Get specific tenant
curl -X GET http://localhost:8081/api/config/tenant/ccl_premium

# Reload configuration
curl -X POST http://localhost:8081/api/config/reload \
  -H "Content-Type: application/json"

# Check health
curl -X GET http://localhost:8081/api/config/health
```

## Mock Mode

When `activeProfile=mock` or ConfigManager API is unavailable:
- System generates mock tenant hierarchy
- Mock profiles include sample database, Kafka, and Redis configurations
- Useful for development and testing

## Integration with RouteSphere

The ConfigManager integrates with existing RouteSphere components:

1. **GlobalConfigService**: Provides active tenant and profile
2. **DeploymentConfigService**: Uses configuration for database connections
3. **TenantHierarchyInitializer**: Can fetch hierarchy from ConfigManager API

## Startup Sequence

1. **GlobalConfigService** (Priority 1): Loads tenant/profile from global config
2. **DeploymentConfigService** (Priority 2): Loads deployment configuration
3. **ConfigManagerService** (Priority 5): Initializes configuration management
   - Fetches from ConfigManager API or builds mock data
   - Registers all tenants in global registry
   - Starts Kafka consumer for updates

## Error Handling

- Failed API calls fall back to mock data
- Kafka consumer uses negative acknowledgment for retry
- REST endpoints return appropriate HTTP status codes
- All errors are logged with context

## Monitoring

### Logging
```
LOG.info: Configuration loaded, reloaded
LOG.debug: Kafka message processing
LOG.error: API failures, processing errors
```

### Metrics (Future)
- Configuration reload count
- API call success/failure rates
- Kafka consumer lag
- Tenant registry size

## Dependencies

### Maven Dependencies
```xml
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-kafka-client</artifactId>
</dependency>
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-smallrye-reactive-messaging-kafka</artifactId>
</dependency>
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-resteasy-reactive-jackson</artifactId>
</dependency>
```

## Future Enhancements

1. **Caching**: Add local caching with TTL for configuration data
2. **WebSocket**: Real-time configuration updates via WebSocket
3. **Metrics**: Micrometer metrics for monitoring
4. **Security**: Add authentication/authorization for REST endpoints
5. **Validation**: Schema validation for configuration changes
6. **Audit**: Configuration change audit trail
7. **Multi-Region**: Support for region-specific configurations