#!/bin/bash

# Production Environment Startup Script
# No tunnels needed - direct connections

echo "======================================"
echo "  PRODUCTION ENVIRONMENT STARTUP"
echo "======================================"
echo ""
echo "Starting ConfigManager in PRODUCTION mode..."
echo "Direct connections to remote services:"
echo "  • Kafka:     103.95.96.76:9092"
echo "  • MySQL:     103.95.96.77:3306"
echo "  • Redis:     103.95.96.76:6379"
echo ""
echo "IMPORTANT: Ensure Kafka server has correct configuration:"
echo "  advertised.listeners=PLAINTEXT://103.95.96.76:9092"
echo ""
echo "Starting application..."
echo "======================================"

cd RTC-Manager/ConfigManager
mvn spring-boot:run -Dspring.profiles.active=prod