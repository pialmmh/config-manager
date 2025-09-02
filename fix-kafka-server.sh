#!/bin/bash

# Script to fix Kafka advertised.listeners on the server
# Run this on the Kafka server (103.95.96.76)

KAFKA_IP="103.95.96.76"
KAFKA_CONFIG="/opt/kafka/config/server.properties"  # Adjust path if different

echo "==================================="
echo "Kafka Server Configuration Fix"
echo "==================================="

# Backup current configuration
echo "1. Backing up current configuration..."
sudo cp $KAFKA_CONFIG ${KAFKA_CONFIG}.backup.$(date +%Y%m%d_%H%M%S)

# Check current settings
echo -e "\n2. Current listener configuration:"
grep -E "^listeners|^advertised.listeners" $KAFKA_CONFIG || echo "No listeners configured"

# Update configuration
echo -e "\n3. Updating configuration..."

# Comment out old advertised.listeners if exists
sudo sed -i 's/^advertised.listeners/#advertised.listeners/g' $KAFKA_CONFIG

# Add correct advertised.listeners
echo -e "\n# Fixed advertised.listeners for external access" | sudo tee -a $KAFKA_CONFIG
echo "advertised.listeners=PLAINTEXT://${KAFKA_IP}:9092" | sudo tee -a $KAFKA_CONFIG

# Also ensure listeners is set correctly
if ! grep -q "^listeners=" $KAFKA_CONFIG; then
    echo "listeners=PLAINTEXT://0.0.0.0:9092" | sudo tee -a $KAFKA_CONFIG
fi

echo -e "\n4. New configuration:"
grep -E "^listeners|^advertised.listeners" $KAFKA_CONFIG

echo -e "\n5. Configuration updated! Now restart Kafka..."
echo ""
echo "To restart Kafka, run one of these commands:"
echo ""
echo "  # If using systemd:"
echo "  sudo systemctl restart kafka"
echo ""
echo "  # If using init.d:"
echo "  sudo /etc/init.d/kafka restart"
echo ""
echo "  # If running manually:"
echo "  $KAFKA_HOME/bin/kafka-server-stop.sh"
echo "  $KAFKA_HOME/bin/kafka-server-start.sh -daemon $KAFKA_CONFIG"
echo ""
echo "==================================="