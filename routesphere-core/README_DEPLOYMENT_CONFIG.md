# RouteSphere Deployment Configuration

## Overview
RouteSphere uses Quarkus configuration management to handle multi-tenant, multi-profile deployment configurations. Each tenant (party/partner) can have multiple deployment profiles (mock, dev, staging, prod) with specific socket instances and general configurations.

## Configuration Structure

```
routesphere:
  tenant:
    id: "tenant_id"
    name: "Tenant Name"
    type: "ROOT|RESELLER_L1|RESELLER_L2|END_USER"
    active-profile: "dev|staging|prod|mock"
    profiles:
      dev:
        general:
          database: {...}
          kafka: {...}
          redis: {...}
          config-manager: {...}
        sockets:
          sip-external: {...}
          sip-internal: {...}
          http-rest: {...}
      prod:
        general: {...}
        sockets: {...}
```

## Profiles

### 1. **Development (dev)**
- Local development environment
- Uses localhost services
- Relaxed security settings
- Higher logging levels

### 2. **Production (prod)**
- Production environment
- Full security enabled
- Optimized connection pools
- Environment variable configuration

### 3. **Staging**
- Pre-production testing
- Production-like configuration
- Separate databases

### 4. **Mock**
- Unit testing
- In-memory databases (H2)
- Mock external services

## General Configuration

### Database Configuration
```yaml
database:
  type: "mysql"
  host: "127.0.0.1"
  port: 3306
  name: "routesphere_dev"
  username: "root"
  password: "123456"
  pool:
    min-size: 5
    max-size: 20
```

### Kafka Configuration
```yaml
kafka:
  bootstrap-servers: "localhost:9092"
  group-id: "routesphere-dev"
  topics:
    cdr: "cdr-dev"
    events: "events-dev"
    billing: "billing-dev"
```

### Redis Configuration
```yaml
redis:
  host: "localhost"
  port: 6379
  database: 0
  pool:
    max-total: 50
```

### ConfigManager Configuration
```yaml
config-manager:
  base-url: "http://localhost:7070"
  tenant-endpoint: "/get-tenant-root"
  timeout: "30s"
```

## Socket Profiles

Socket profiles are similar to FreeSWITCH profiles (external/internal) and define protocol-specific network bindings.

### SIP Socket Profile
```yaml
sip-external:
  name: "sip-external"
  protocol: "SIP"
  enabled: true
  network:
    bind-address: "0.0.0.0"
    bind-port: 5060
    external-ip: "203.0.113.1"
    transport: "UDP,TCP,TLS"
    limits:
      max-connections: 10000
      rate-limit: 100
  security:
    auth:
      type: "digest"
      realm: "routesphere.dev"
      require-auth: true
    tls:
      enabled: true
      cert-file: "/etc/certs/sip.crt"
      key-file: "/etc/certs/sip.key"
```

### HTTP/HTTPS Socket Profile
```yaml
http-rest:
  name: "http-rest"
  protocol: "HTTP"
  enabled: true
  network:
    bind-address: "0.0.0.0"
    bind-port: 8080
    limits:
      max-connections: 1000
      rate-limit: 1000
  settings:
    cors-enabled: "true"
    max-request-size: "10MB"
```

### SMS Socket Profile
```yaml
sms-gateway:
  name: "sms-gateway"
  protocol: "SMS"
  enabled: true
  network:
    bind-address: "0.0.0.0"
    bind-port: 8090
  settings:
    provider: "twilio"
    max-message-length: "1600"
```

### ESL (FreeSWITCH) Socket Profile
```yaml
esl-freeswitch:
  name: "esl-freeswitch"
  protocol: "ESL"
  enabled: true
  network:
    bind-address: "127.0.0.1"
    bind-port: 8021
  settings:
    password: "ClueCon"
    event-filter: "CHANNEL_CREATE,CHANNEL_ANSWER"
```

## Multi-Tenant Configuration

Different tenants can have their own configuration files:

### Example: Premium Reseller
```yaml
# config/tenants/reseller-premium.yml
routesphere:
  tenant:
    id: "reseller_premium_001"
    name: "Premium Communications Partner"
    type: "RESELLER_L1"
    parent-id: "telcobright_root"
    profiles:
      prod:
        general:
          database:
            name: "reseller_premium_prod"
        sockets:
          sip-premium:
            network:
              bind-address: "10.100.1.10"  # Dedicated IP
              limits:
                max-connections: 20000      # Higher limits
```

## Environment Variables

Production configurations support environment variables:

```yaml
database:
  host: "${DB_HOST:db.production.local}"
  password: "${DB_PASSWORD}"
  
kafka:
  bootstrap-servers: "${KAFKA_SERVERS:kafka1:9092,kafka2:9092}"
  
config-manager:
  base-url: "${CONFIG_MANAGER_URL:https://config.local}"
```

## Profile Activation

### Via application.properties
```properties
quarkus.profile=dev
```

### Via environment variable
```bash
export QUARKUS_PROFILE=prod
```

### Via command line
```bash
java -Dquarkus.profile=prod -jar routesphere.jar
```

## Configuration Loading

The `DeploymentConfigService` loads configuration on startup:

```java
@Inject
DeploymentConfigService deploymentConfig;

// Get database configuration
DatabaseConfig dbConfig = deploymentConfig.getDatabaseConfig();

// Get ConfigManager URL
String configManagerUrl = deploymentConfig.getConfigManagerUrl();

// Check active profile
String profile = deploymentConfig.getActiveProfile();
```

## File Locations

```
src/main/resources/
├── application.properties           # Default configuration
├── application-dev.yml             # Development profile
├── application-prod.yml            # Production profile
├── application-staging.yml         # Staging profile
└── config/
    └── tenants/
        ├── reseller-premium.yml    # Premium reseller config
        ├── reseller-standard.yml   # Standard reseller config
        └── customer-abc.yml        # End customer config
```

## Key Features

1. **Profile-based Configuration**: Different settings for dev, staging, prod
2. **Socket Profiles**: Multiple protocol instances with different settings
3. **Security Configuration**: TLS, authentication, access control per socket
4. **Connection Limits**: Per-socket connection and rate limiting
5. **Dynamic Properties**: Environment variable substitution
6. **Tenant Isolation**: Separate databases and Kafka topics per tenant
7. **Monitoring Config**: Metrics, health checks, logging per profile

## Migration from Hardcoded Config

The database configuration has been moved from:
- **Before**: Hardcoded in `TenantHierarchyInitializer`
- **After**: Configuration files with profile support

ConfigManager URL now comes from:
- **Before**: `System.getenv("CONFIG_MANAGER_URL")`
- **After**: `deploymentConfig.getConfigManagerUrl()`

## Best Practices

1. **Use environment variables** for sensitive data in production
2. **Profile inheritance**: Common settings in base, specific in profiles
3. **Socket naming**: Use descriptive names (sip-external, sip-internal)
4. **Tenant isolation**: Separate databases, Redis DBs, Kafka topics
5. **Connection pools**: Size based on expected load per profile
6. **Security layers**: Enable progressively (dev → staging → prod)