#!/bin/bash

# SSH Tunnel for Kafka
# This forwards local port 9092 to remote Kafka server

REMOTE_HOST="103.95.96.76"
REMOTE_USER="root"  # Change to your SSH username
LOCAL_PORT="9092"
REMOTE_PORT="9092"

echo "======================================"
echo "Starting SSH Tunnel for Kafka"
echo "======================================"
echo ""
echo "Local port: localhost:${LOCAL_PORT}"
echo "Remote: ${REMOTE_HOST}:${REMOTE_PORT}"
echo ""

# Kill any existing tunnel on the same port
echo "Checking for existing tunnels..."
existing_pid=$(ps aux | grep "ssh.*${LOCAL_PORT}:localhost:${REMOTE_PORT}" | grep -v grep | awk '{print $2}')
if [ ! -z "$existing_pid" ]; then
    echo "Killing existing tunnel (PID: $existing_pid)"
    kill $existing_pid
    sleep 2
fi

# Create SSH tunnel
echo "Creating SSH tunnel..."
ssh -N -L ${LOCAL_PORT}:localhost:${REMOTE_PORT} ${REMOTE_USER}@${REMOTE_HOST} &
SSH_PID=$!

echo "SSH tunnel started with PID: $SSH_PID"
echo ""
echo "Tunnel is running in background."
echo "Your application can now connect to: localhost:9092"
echo ""
echo "To stop the tunnel:"
echo "  kill $SSH_PID"
echo ""
echo "To keep tunnel alive in foreground (Ctrl+C to stop):"
echo "  ssh -N -L ${LOCAL_PORT}:localhost:${REMOTE_PORT} ${REMOTE_USER}@${REMOTE_HOST}"
echo "======================================"

# Save PID to file for later use
echo $SSH_PID > /tmp/kafka-tunnel.pid
echo "PID saved to /tmp/kafka-tunnel.pid"