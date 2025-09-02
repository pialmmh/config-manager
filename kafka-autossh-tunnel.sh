#!/bin/bash

# Persistent SSH Tunnel for Kafka using autossh
# This automatically reconnects if connection drops

REMOTE_HOST="103.95.96.76"
REMOTE_USER="root"  # Change to your SSH username
LOCAL_PORT="9092"
REMOTE_PORT="9092"
MONITOR_PORT="20000"  # Port for autossh monitoring

echo "======================================"
echo "Starting Persistent SSH Tunnel with AutoSSH"
echo "======================================"

# Install autossh if not present
if ! command -v autossh &> /dev/null; then
    echo "AutoSSH not found. Installing..."
    sudo apt-get update && sudo apt-get install -y autossh
fi

# Kill existing autossh tunnels
echo "Checking for existing autossh tunnels..."
pkill -f "autossh.*${LOCAL_PORT}:localhost:${REMOTE_PORT}"
sleep 2

# Start autossh
echo "Starting autossh tunnel..."
AUTOSSH_GATETIME=0 autossh -M ${MONITOR_PORT} \
    -o "ServerAliveInterval=60" \
    -o "ServerAliveCountMax=3" \
    -o "StrictHostKeyChecking=no" \
    -o "UserKnownHostsFile=/dev/null" \
    -N -f \
    -L ${LOCAL_PORT}:localhost:${REMOTE_PORT} \
    ${REMOTE_USER}@${REMOTE_HOST}

if [ $? -eq 0 ]; then
    echo "✓ AutoSSH tunnel started successfully!"
    echo ""
    echo "Kafka is now accessible at: localhost:9092"
    echo ""
    echo "The tunnel will automatically reconnect if disconnected."
    echo ""
    echo "To stop the tunnel:"
    echo "  pkill -f 'autossh.*${LOCAL_PORT}:localhost:${REMOTE_PORT}'"
    echo ""
    echo "To check tunnel status:"
    echo "  ps aux | grep autossh"
else
    echo "✗ Failed to start autossh tunnel"
    exit 1
fi