#!/bin/bash

# Intelligent ConfigManager Startup Script
# Detects environment and sets up accordingly

echo "======================================"
echo "   ConfigManager Startup"
echo "======================================"
echo ""

# Detect if we can reach the remote servers directly
echo "Detecting network environment..."

# Check if Kafka is directly accessible
nc -zv -w 2 103.95.96.76 9092 &>/dev/null
KAFKA_DIRECT=$?

# Check if MySQL is directly accessible  
nc -zv -w 2 103.95.96.77 3306 &>/dev/null
MYSQL_DIRECT=$?

if [ $KAFKA_DIRECT -eq 0 ] && [ $MYSQL_DIRECT -eq 0 ]; then
    echo "✓ Remote services are directly accessible"
    echo "→ Starting in PRODUCTION mode"
    echo ""
    PROFILE="prod"
else
    echo "✗ Remote services are not directly accessible"
    echo "→ Starting in DEVELOPMENT mode with SSH tunnels"
    echo ""
    PROFILE="dev"
    
    # Start tunnels for dev mode
    ./start-dev-tunnels.sh
    echo ""
    echo "Waiting for tunnels to establish..."
    sleep 3
fi

echo "Starting ConfigManager with profile: $PROFILE"
echo "======================================"

cd RTC-Manager/ConfigManager

# Start with detected profile
mvn spring-boot:run -Dspring.profiles.active=$PROFILE