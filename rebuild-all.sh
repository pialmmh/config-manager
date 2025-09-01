#!/bin/bash
# Rebuild all libraries and main project

echo "🔨 Building RouteSphere Libraries..."

# Build libraries
echo "📦 Building statemachine..."
(cd statemachine && mvn clean install -DskipTests)

echo "📦 Building infinite-scheduler..."  
(cd infinite-scheduler && mvn clean install -DskipTests)

echo "📦 Building partitioned-repo..."
(cd partitioned-repo && mvn clean install -DskipTests)

echo "📦 Building chronicle-db-Cache..."
(cd chronicle-db-Cache && mvn clean install -DskipTests)

# Build main project
echo "🚀 Building main RouteSphere application..."
mvn clean compile

echo "✅ Build complete!"