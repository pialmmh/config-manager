# RouteSphere Global Configuration

## Overview
The global configuration (`routesphere-global.yml`) is a simplified control file that contains ONLY the active tenant and profile selection. This is loaded first at startup with highest priority.

## Configuration File Location
```
src/main/resources/routesphere-global.yml
```

## Global Configuration Structure

```yaml
routesphere:
  global:
    # Only two parameters in global config
    tenant: ccl        # Which tenant is active
    profile: dev       # Which profile to use
```

## Configuration Parameters

The global configuration contains exactly two parameters:

### 1. Tenant
```yaml
tenant: ccl      # Active tenant name
```
Specifies which tenant configuration to load (e.g., telcobright_root, ccl, reseller_premium)

### 2. Profile
```yaml
profile: dev     # Active profile name
```
Specifies which environment profile to use (e.g., dev, staging, prod, mock)

## Configuration Precedence

The configuration follows this precedence (highest to lowest):

1. **Environment Variables** (highest priority)
2. **Command Line Arguments**
3. **Tenant Profile Config**
4. **Tenant Base Config**
5. **Global Config** (lowest priority)

## Setting Active Tenant and Profile

### Method 1: Edit Global Config File
```yaml
routesphere:
  global:
    tenant: ccl
    profile: prod
```

### Method 2: Environment Variables
```bash
export ROUTESPHERE_ACTIVE_TENANT=ccl
export ROUTESPHERE_ACTIVE_PROFILE=prod
java -jar routesphere.jar
```

### Method 3: System Properties
```bash
java -Droutesphere.active.tenant=ccl \
     -Droutesphere.active.profile=prod \
     -jar routesphere.jar
```

## Examples

### Switch to CCL Development
```yaml
tenant: ccl
profile: dev
```
Result: Uses CCL tenant with development database, local services

### Switch to CCL Production
```yaml
tenant: ccl
profile: prod
```
Result: Uses CCL tenant with production database, clusters, TLS

### Switch to Root Tenant
```yaml
tenant: telcobright_root
profile: dev
```
Result: Uses root organization configuration

## Additional Settings

All other configuration settings (features, tenant registry, switching rules, etc.) are managed in:
- Tenant-specific configuration files (`config/tenants/<tenant>/tenant-config.yml`)
- Profile-specific configuration files (`config/tenants/<tenant>/profile-*.yml`)
- Application properties (`application.properties`)

## How It Works

### Startup Sequence:
1. **GlobalConfigService** loads first (Priority 1)
   - Reads `routesphere-global.yml`
   - Determines active tenant and profile
   - Validates configuration

2. **DeploymentConfigService** loads second (Priority 2)
   - Uses tenant/profile from GlobalConfigService
   - Loads database, Kafka, Redis configs

3. **TenantConfigLoader** loads third (Priority 3)
   - Loads tenant-specific configurations
   - Loads profile-specific settings

4. **TenantHierarchyInitializer** loads fourth (Priority 4)
   - Initializes tenant hierarchy
   - Connects to ConfigManager

## GlobalConfigService API

```java
@Inject
GlobalConfigService globalConfig;

// Get active configuration
String tenant = globalConfig.getActiveTenant();     // "ccl"
String profile = globalConfig.getActiveProfile();   // "dev"

// Check environment modes
boolean isProd = globalConfig.isProduction();       // true if profile="prod"
boolean isDev = globalConfig.isDevelopment();       // true if profile="dev"
boolean isStaging = globalConfig.isStaging();      // true if profile="staging"
boolean isMock = globalConfig.isMock();            // true if profile="mock"

// Get configuration summary
String summary = globalConfig.getConfigurationSummary();
```

## Validation

The global config validates:
- Tenant name is not empty
- Profile name is not empty
- Profile name is standard (dev, staging, prod, mock)

Defaults are applied:
- If no tenant specified: `telcobright_root`
- If no profile specified: `dev`

Warnings are logged for:
- Missing tenant (defaults to telcobright_root)
- Missing profile (defaults to dev)
- Non-standard profile names

## Benefits

1. **Single Point of Control**: One file to switch entire deployment
2. **Environment Flexibility**: Easy dev/staging/prod switching
3. **Multi-Tenant Support**: Quick tenant switching for testing
4. **Override Capability**: Environment variables for CI/CD
5. **Validation**: Ensures valid tenant/profile combination
6. **Registry**: Central list of all available tenants

## Use Cases

### Development
```yaml
active-tenant: ccl
active-profile: dev
```

### Testing Different Tenants
```yaml
# Test CCL
tenant: ccl
profile: mock

# Test Premium Reseller
tenant: reseller_premium
profile: dev
```

### Production Deployment
```yaml
tenant: ccl
profile: prod
```

### Multi-Tenant SaaS
```yaml
# Each deployment uses different tenant
# Instance 1: tenant: ccl
# Instance 2: tenant: abc_corp
# Instance 3: tenant: xyz_telecom
```

## Environment Variable Reference

| Variable | Description | Example |
|----------|-------------|---------|
| `ROUTESPHERE_ACTIVE_TENANT` | Override active tenant | `ccl` |
| `ROUTESPHERE_ACTIVE_PROFILE` | Override active profile | `prod` |
| `DEPLOYMENT_ENV` | Deployment environment | `production` |
| `DEPLOYMENT_REGION` | Deployment region | `us-east-1` |
| `DEPLOYMENT_ZONE` | Availability zone | `zone-a` |

## Troubleshooting

### No Tenant Specified
```
⚠️  No tenant specified, using default: telcobright_root
```
Solution: Add `tenant: <name>` to global config

### No Profile Specified
```
⚠️  No profile specified, using default: dev
```
Solution: Add `profile: <name>` to global config

### Non-Standard Profile
```
⚠️  Warning: Profile 'custom' may not be standard (expected: dev, staging, prod, mock)
```
Solution: Use standard profile names for consistency

### Configuration Not Loading
Check:
1. File exists at `src/main/resources/routesphere-global.yml`
2. YAML syntax is valid
3. Both `tenant` and `profile` fields are present under `routesphere.global`