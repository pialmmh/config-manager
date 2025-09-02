#!/bin/bash

# Quick fix for Kafka advertised.listeners issue
# This maps localhost to the actual Kafka server IP

KAFKA_IP="103.95.96.76"

echo "Checking current /etc/hosts for localhost mapping..."
grep "^127.0.0.1" /etc/hosts

echo ""
echo "To fix the Kafka connection issue, you need to either:"
echo ""
echo "1. (Recommended) Fix Kafka server configuration:"
echo "   On server $KAFKA_IP, edit kafka/config/server.properties:"
echo "   advertised.listeners=PLAINTEXT://$KAFKA_IP:9092"
echo ""
echo "2. (Temporary) Add this line to /etc/hosts:"
echo "   127.0.0.1 localhost $KAFKA_IP"
echo "   NOTE: This will break other localhost services!"
echo ""
echo "3. (Better Workaround) Use SSH tunnel:"
echo "   ssh -L 9092:localhost:9092 user@$KAFKA_IP"
echo ""
echo "The issue is that Kafka at $KAFKA_IP is advertising itself as 'localhost'"
echo "which causes clients to try connecting to 127.0.0.1 instead of $KAFKA_IP"