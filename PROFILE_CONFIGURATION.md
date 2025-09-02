# ConfigManager Profile Configuration

## Overview
ConfigManager supports multiple profiles for different environments:
- **dev**: Development environment using SSH tunnels
- **prod**: Production environment with direct connections

## Profile Configurations

### Development Profile (dev)
- **File**: `application-dev.properties`
- **Kafka**: `localhost:9092` (via SSH tunnel)
- **MySQL**: `localhost:3306` (via SSH tunnel)
- **Redis**: `localhost:6379` (via SSH tunnel)
- **Requires**: SSH tunnels to be running

### Production Profile (prod)
- **File**: `application-prod.properties`
- **Kafka**: `103.95.96.76:9092` (direct)
- **MySQL**: `103.95.96.77:3306` (direct)
- **Redis**: `103.95.96.76:6379` (direct)
- **Requires**: Network access to remote servers

## Usage

### Development Mode
```bash
# Start SSH tunnels and run in dev mode
./start-dev-tunnels.sh
cd RTC-Manager/ConfigManager
mvn spring-boot:run -Dspring.profiles.active=dev
```

### Production Mode
```bash
# Run directly in production mode
./start-prod.sh
# OR
cd RTC-Manager/ConfigManager
mvn spring-boot:run -Dspring.profiles.active=prod
```

### Auto-Detection Mode
```bash
# Automatically detects environment and starts appropriately
./start-configmanager.sh
```

## Setting Active Profile

### Method 1: Command Line
```bash
mvn spring-boot:run -Dspring.profiles.active=dev
```

### Method 2: Environment Variable
```bash
export SPRING_PROFILES_ACTIVE=dev
mvn spring-boot:run
```

### Method 3: application.properties
```properties
spring.profiles.active=dev
```

### Method 4: IDE Configuration
In IntelliJ IDEA:
- Run Configuration → Environment Variables
- Add: `SPRING_PROFILES_ACTIVE=dev`

## SSH Tunnels (Dev Mode Only)

### Required Tunnels
```bash
# Kafka
ssh -L 9092:localhost:9092 root@103.95.96.76

# MySQL
ssh -L 3306:127.0.0.1:3306 root@103.95.96.77

# Zookeeper
ssh -L 2181:localhost:2181 root@103.95.96.76

# Redis
ssh -L 6379:localhost:6379 root@103.95.96.76
```

### Using the Script
```bash
# Start all tunnels
./start-dev-tunnels.sh

# Stop all tunnels
cat /tmp/dev-tunnels.pids | xargs kill
```

## Troubleshooting

### Issue: Kafka Connection Error in Dev Mode
**Solution**: Ensure SSH tunnels are running
```bash
ps aux | grep "ssh.*9092"
```

### Issue: Kafka Connection Error in Prod Mode
**Solution**: Check Kafka server configuration
```bash
# On Kafka server (103.95.96.76)
grep advertised.listeners /opt/kafka/config/server.properties
# Should be: advertised.listeners=PLAINTEXT://103.95.96.76:9092
```

### Issue: Profile Not Loading
**Check active profile**:
```bash
# In application logs, look for:
# "The following profiles are active: dev"
```

## Profile-Specific Properties

### Common Properties (application.properties)
- Basic Spring Boot configuration
- Kafka serializers
- Consumer settings

### Dev-Specific (application-dev.properties)
- localhost endpoints
- Debug logging
- Development database

### Prod-Specific (application-prod.properties)
- Remote server endpoints
- Optimized settings
- Production database
- Reduced logging

## Best Practices

1. **Never commit production passwords** - Use environment variables
2. **Always use dev profile locally** - Prevents accidental production access
3. **Test profile switching** - Ensure both profiles work correctly
4. **Monitor tunnel stability** - Use autossh for persistent tunnels
5. **Document server IPs** - Keep track of which server hosts which service

## Service Endpoints

| Service    | Dev (via tunnel)  | Prod (direct)      |
|------------|------------------|-------------------|
| Kafka      | localhost:9092   | 103.95.96.76:9092 |
| MySQL      | localhost:3306   | 103.95.96.77:3306 |
| Zookeeper  | localhost:2181   | 103.95.96.76:2181 |
| Redis      | localhost:6379   | 103.95.96.76:6379 |