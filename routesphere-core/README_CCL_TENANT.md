# CCL Tenant Configuration

## Overview
CCL (CCL Communications Ltd) is configured as a tenant/client in RouteSphere. Each tenant represents a partner, party, company, or person using the RouteSphere platform. CCL has multiple deployment profiles (dev, staging, prod, mock) for different environments.

## Tenant Hierarchy

```
telcobright_root (ROOT)
└── ccl (END_USER) - CCL Communications Ltd
    ├── profile: ccl-dev
    ├── profile: ccl-staging
    ├── profile: ccl-prod
    └── profile: ccl-mock
```

## Directory Structure

```
config/tenants/ccl/
├── tenant-config.yml      # Base tenant configuration
├── profile-dev.yml        # Development environment
├── profile-staging.yml    # Staging environment  
├── profile-prod.yml       # Production environment
└── profile-mock.yml       # Mock/testing environment
```

## Tenant Configuration (tenant-config.yml)

Base configuration for CCL tenant that applies to all profiles:

```yaml
routesphere:
  tenant:
    id: "ccl"
    name: "CCL Communications Ltd"
    type: "END_USER"
    parent-id: "telcobright_root"
    
    metadata:
      company-type: "Telecommunications Provider"
      contract-id: "CTR-2024-CCL-001"
      sla-level: "PLATINUM"
      billing-type: "POSTPAID"
      credit-limit: "50000"
      
    settings:
      max-concurrent-calls: "5000"
      max-daily-calls: "100000"
      allowed-destinations: "BD,IN,US,UK,CA"
      blocked-destinations: "CU,IR,KP"
```

## Profile Configurations

### Development Profile (ccl-dev)
- **Database**: Local MySQL on `ccl_dev` database
- **Kafka**: Local instance on port 9092
- **Redis**: Local instance, database 2
- **SIP Port**: 15060 (local testing)
- **HTTP API**: Port 18080
- **Security**: Relaxed (no auth required)

### Staging Profile (ccl-staging)
- **Database**: Staging MySQL cluster
- **Kafka**: Staging Kafka cluster
- **Redis**: Staging Redis, database 1
- **SIP Port**: 5060 on staging network
- **Security**: Production-like settings

### Production Profile (ccl-prod)
- **Database**: HA MySQL cluster with SSL
- **Kafka**: 3-node Kafka cluster with SASL/SSL
- **Redis**: Redis cluster with password
- **SIP Ports**: 
  - Primary: 10.50.1.10:5060 (Public: 203.95.220.100)
  - Secondary: 10.50.2.10:5060 (Public: 203.95.220.101)
- **HTTPS API**: Port 8443 with TLS 1.2/1.3
- **SMS Gateway**: Twilio integration
- **Security**: Full TLS, digest auth, IP whitelisting
- **Limits**: 
  - Max connections: 5000
  - Rate limit: 500 calls/second
  - API rate limit: 1000 requests/second

### Mock Profile (ccl-mock)
- **Database**: In-memory H2
- **Kafka**: Mock broker
- **SIP Port**: 25060 (mock testing)
- **Features**: Auto-answer, test numbers

## Socket Profiles per Environment

### Production Sockets
1. **ccl-sip-prod-primary**: Main SIP profile with TLS
2. **ccl-sip-prod-secondary**: Backup SIP profile
3. **ccl-https-prod**: REST API with bearer auth
4. **ccl-sms-prod**: SMS gateway via Twilio

### Development Sockets
1. **ccl-sip-dev**: Local SIP testing
2. **ccl-http-dev**: Local HTTP API with Swagger
3. **ccl-sms-dev**: Disabled/mock SMS

## Environment Variables (Production)

```bash
# Database
CCL_DB_HOST=ccl-db.production.local
CCL_DB_NAME=ccl_prod
CCL_DB_USER=ccl_prod
CCL_DB_PASSWORD=<secure_password>

# Kafka
CCL_KAFKA_SERVERS=kafka1.ccl.prod:9092,kafka2.ccl.prod:9092
CCL_KAFKA_SASL_CONFIG=<sasl_config>

# Redis
CCL_REDIS_HOST=redis.ccl.prod
CCL_REDIS_PASSWORD=<redis_password>

# Network
CCL_PUBLIC_IP=203.95.220.100
CCL_ALLOWED_NETWORKS=203.95.220.0/24,10.50.0.0/16

# SMS (Twilio)
CCL_TWILIO_ACCOUNT_SID=<account_sid>
CCL_TWILIO_AUTH_TOKEN=<auth_token>
```

## Activation

### Activate CCL tenant with dev profile:
```properties
routesphere.active.tenant=ccl
routesphere.active.profile=dev
```

### Or via environment:
```bash
export ROUTESPHERE_ACTIVE_TENANT=ccl
export ROUTESPHERE_ACTIVE_PROFILE=prod
```

### Or via command line:
```bash
java -Droutesphere.active.tenant=ccl \
     -Droutesphere.active.profile=prod \
     -jar routesphere.jar
```

## CCL-Specific Features

### 1. Multi-Level Configuration
- **Tenant Level**: Company metadata, SLA, billing
- **Profile Level**: Environment-specific settings
- **Socket Level**: Protocol-specific configurations

### 2. High Availability (Production)
- Primary and secondary SIP profiles
- Database failover configuration
- Kafka cluster with 3 nodes
- Redis cluster support

### 3. Security Layers
- **Network**: IP whitelisting, bind addresses
- **Transport**: TLS 1.2/1.3 for SIP and HTTPS
- **Authentication**: Digest for SIP, Bearer for API
- **Rate Limiting**: Per-socket limits

### 4. Resource Limits
- Max concurrent calls: 5000
- Max daily calls: 100000
- Connection limits per socket
- Rate limits per protocol

### 5. Monitoring
- Prometheus metrics endpoint
- Health checks (liveness/readiness)
- JSON structured logging
- Per-environment log levels

## Usage in Code

```java
@Inject
TenantConfigLoader configLoader;

// Get CCL configuration
TenantConfiguration cclConfig = configLoader.getTenantConfig("ccl");

// Get active profile for CCL
ProfileConfiguration activeProfile = cclConfig.getProfile("prod");

// Access database settings
String dbUrl = activeProfile.getDatabaseUrl();

// Get socket configurations
Map<String, SocketConfiguration> sockets = activeProfile.getSockets();
SocketConfiguration sipSocket = sockets.get("ccl-sip-prod-primary");
```

## Benefits of This Structure

1. **Tenant Isolation**: Each client has separate configuration
2. **Environment Separation**: Clear dev/staging/prod boundaries
3. **Protocol Flexibility**: Different sockets for different protocols
4. **Security Gradation**: Progressive security from dev to prod
5. **Resource Management**: Per-tenant limits and quotas
6. **Easy Onboarding**: Copy template, customize for new client
7. **Configuration as Code**: Version controlled, reviewable

## Adding New Tenants

To add a new tenant (e.g., "ABC Corp"):

1. Create directory: `config/tenants/abc/`
2. Copy CCL templates and customize
3. Update tenant ID, name, and settings
4. Configure profiles for their environments
5. Set resource limits based on contract
6. Deploy with `routesphere.active.tenant=abc`