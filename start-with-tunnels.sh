#!/bin/bash

# Complete startup script with SSH tunnels for all services

REMOTE_HOST="103.95.96.76"
REMOTE_USER="root"

echo "======================================"
echo "Starting SSH Tunnels for All Services"
echo "======================================"

# Function to create tunnel
create_tunnel() {
    local LOCAL_PORT=$1
    local REMOTE_HOST=$2
    local REMOTE_PORT=$3
    local SERVICE_NAME=$4
    
    # Kill existing tunnel
    existing=$(ps aux | grep "ssh.*${LOCAL_PORT}:.*:${REMOTE_PORT}" | grep -v grep | awk '{print $2}')
    if [ ! -z "$existing" ]; then
        echo "Killing existing ${SERVICE_NAME} tunnel..."
        kill $existing 2>/dev/null
        sleep 1
    fi
    
    # Create new tunnel
    echo "Creating ${SERVICE_NAME} tunnel (localhost:${LOCAL_PORT} -> ${REMOTE_HOST}:${REMOTE_PORT})..."
    ssh -N -L ${LOCAL_PORT}:${REMOTE_HOST}:${REMOTE_PORT} ${REMOTE_USER}@${REMOTE_HOST} &
    echo "  PID: $!"
}

# Create tunnels for all services
create_tunnel 9092 localhost 9092 "Kafka"
create_tunnel 2181 localhost 2181 "Zookeeper"
create_tunnel 3306 127.0.0.1 3306 "MySQL"
create_tunnel 6379 localhost 6379 "Redis"

echo ""
echo "All tunnels created successfully!"
echo ""
echo "Services now accessible at:"
echo "  - Kafka:     localhost:9092"
echo "  - Zookeeper: localhost:2181"
echo "  - MySQL:     localhost:3306"
echo "  - Redis:     localhost:6379"
echo ""
echo "To stop all tunnels:"
echo "  pkill -f 'ssh.*-L'"
echo ""
echo "Starting ConfigManager..."
cd RTC-Manager/ConfigManager
mvn spring-boot:run