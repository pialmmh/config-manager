#!/bin/bash

# Development Environment Startup Script
# Creates SSH tunnels for all remote services

REMOTE_HOST="103.95.96.76"
REMOTE_MYSQL_HOST="103.95.96.77"
REMOTE_USER="root"

echo "======================================"
echo "  DEVELOPMENT ENVIRONMENT SETUP"
echo "======================================"
echo ""
echo "Creating SSH tunnels for remote services..."
echo ""

# Function to create tunnel
create_tunnel() {
    local LOCAL_PORT=$1
    local REMOTE_IP=$2
    local REMOTE_PORT=$3
    local SERVICE_NAME=$4
    local SSH_HOST=$5
    
    # Kill existing tunnel
    existing=$(ps aux | grep "ssh.*${LOCAL_PORT}:" | grep -v grep | awk '{print $2}')
    if [ ! -z "$existing" ]; then
        echo "  ✓ Killing existing ${SERVICE_NAME} tunnel (PID: $existing)"
        kill $existing 2>/dev/null
        sleep 1
    fi
    
    # Create new tunnel
    echo "  → Creating ${SERVICE_NAME} tunnel..."
    ssh -N -L ${LOCAL_PORT}:${REMOTE_IP}:${REMOTE_PORT} ${REMOTE_USER}@${SSH_HOST} &
    local PID=$!
    echo "    localhost:${LOCAL_PORT} -> ${SSH_HOST} -> ${REMOTE_IP}:${REMOTE_PORT} (PID: $PID)"
    echo $PID >> /tmp/dev-tunnels.pids
}

# Clear previous PIDs file
> /tmp/dev-tunnels.pids

# Create tunnels
echo "Setting up tunnels:"
create_tunnel 9092 localhost 9092 "Kafka" $REMOTE_HOST
create_tunnel 2181 localhost 2181 "Zookeeper" $REMOTE_HOST
create_tunnel 3306 127.0.0.1 3306 "MySQL" $REMOTE_MYSQL_HOST
create_tunnel 6379 localhost 6379 "Redis" $REMOTE_HOST

echo ""
echo "======================================"
echo "  TUNNELS READY!"
echo "======================================"
echo ""
echo "Services accessible at:"
echo "  • Kafka:     localhost:9092"
echo "  • Zookeeper: localhost:2181"
echo "  • MySQL:     localhost:3306"
echo "  • Redis:     localhost:6379"
echo ""
echo "PIDs saved to: /tmp/dev-tunnels.pids"
echo ""
echo "To start ConfigManager in DEV mode:"
echo "  cd RTC-Manager/ConfigManager"
echo "  mvn spring-boot:run -Dspring.profiles.active=dev"
echo ""
echo "To stop all tunnels:"
echo "  cat /tmp/dev-tunnels.pids | xargs kill"
echo "======================================"