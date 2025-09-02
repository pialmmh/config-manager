#!/bin/bash

# SSH tunnel script to forward local Kafka port to remote server
# This works around the advertised.listeners issue

REMOTE_HOST="103.95.96.76"
REMOTE_PORT="9092"
LOCAL_PORT="9092"
SSH_USER="your-username"  # Replace with your SSH username

echo "Creating SSH tunnel for Kafka..."
echo "Local port $LOCAL_PORT -> Remote $REMOTE_HOST:$REMOTE_PORT"

# Create SSH tunnel
# -L creates local port forwarding
# -N means don't execute remote command
# -f runs in background
ssh -L $LOCAL_PORT:localhost:$REMOTE_PORT -N -f $SSH_USER@$REMOTE_HOST

if [ $? -eq 0 ]; then
    echo "SSH tunnel created successfully!"
    echo "Kafka is now accessible at localhost:9092"
    echo ""
    echo "To kill the tunnel later, run:"
    echo "  ps aux | grep 'ssh -L $LOCAL_PORT' | grep -v grep | awk '{print \$2}' | xargs kill"
else
    echo "Failed to create SSH tunnel"
    exit 1
fi