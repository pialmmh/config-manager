#!/bin/bash
# Build libraries and all applications

set -e  # Exit on error

echo "🏗️  RouteSphere Complete Build"
echo "=============================="

# Build libraries first
echo -e "\n📚 Phase 1: Building Libraries"
./build-libraries.sh

# Build Quarkus application
if [ -d "routesphere-quarkus" ]; then
    echo -e "\n⚡ Phase 2: Building Quarkus Application"
    (cd routesphere-quarkus && mvn clean compile -DskipTests)
else
    echo "⚠️  Quarkus application not found"
fi

# Build Spring Boot application  
if [ -d "routesphere-springboot" ]; then
    echo -e "\n🍃 Phase 3: Building Spring Boot Application"
    (cd routesphere-springboot && mvn clean compile -DskipTests)
else
    echo "⚠️  Spring Boot application not found"
fi

echo -e "\n✅ Build Complete!"
echo ""
echo "To run applications:"
echo "  Quarkus:     cd routesphere-quarkus && mvn quarkus:dev"
echo "  Spring Boot: cd routesphere-springboot && mvn spring-boot:run"