#!/bin/bash

# Script to start Kafka and Zookeeper using Docker for testing
# This is optional - only needed when you want to test Kafka integration

echo "Starting Kafka and Zookeeper with Docker..."

# Create network if it doesn't exist
docker network create kafka-network 2>/dev/null || true

# Start Zookeeper
echo "Starting Zookeeper..."
docker run -d \
  --name zookeeper \
  --network kafka-network \
  -p 2181:2181 \
  -e ZOOKEEPER_CLIENT_PORT=2181 \
  -e ZOOKEEPER_TICK_TIME=2000 \
  confluentinc/cp-zookeeper:latest

# Wait for Zookeeper to start
sleep 5

# Start Kafka
echo "Starting Kafka..."
docker run -d \
  --name kafka \
  --network kafka-network \
  -p 9092:9092 \
  -e KAFKA_BROKER_ID=1 \
  -e KAFKA_ZOOKEEPER_CONNECT=zookeeper:2181 \
  -e KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://localhost:9092 \
  -e KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR=1 \
  confluentinc/cp-kafka:latest

echo "Waiting for Kafka to be ready..."
sleep 10

# Create topics
echo "Creating Kafka topics..."
docker exec kafka kafka-topics --create \
  --bootstrap-server localhost:9092 \
  --topic config_event_loader \
  --partitions 3 \
  --replication-factor 1 \
  --if-not-exists

docker exec kafka kafka-topics --create \
  --bootstrap-server localhost:9092 \
  --topic all-mysql-changes \
  --partitions 3 \
  --replication-factor 1 \
  --if-not-exists

echo "Kafka is ready!"
echo ""
echo "To stop Kafka and Zookeeper, run:"
echo "  docker stop kafka zookeeper && docker rm kafka zookeeper"
echo ""
echo "To enable Kafka in RouteSphere:"
echo "  1. Edit src/main/resources/application.properties"
echo "  2. Set configmanager.kafka.enabled=true"
echo "  3. Uncomment the Kafka connector configuration lines"