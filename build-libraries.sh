#!/bin/bash
# Build only the shared libraries

echo "🔨 Building Shared Libraries..."

LIBRARIES=(
    "statemachine"
    "infinite-scheduler"
    "partitioned-repo"
    "chronicle-db-Cache"
)

for lib in "${LIBRARIES[@]}"; do
    if [ -d "$lib" ]; then
        echo "📦 Building $lib..."
        (cd "$lib" && mvn clean install -DskipTests) || {
            echo "❌ Failed to build $lib"
            exit 1
        }
    else
        echo "⚠️  Skipping $lib (directory not found)"
    fi
done

echo "✅ All libraries built successfully!"
echo ""
echo "You can now build your applications:"
echo "  - For Quarkus: cd routesphere-quarkus && mvn compile"
echo "  - For Spring Boot: cd routesphere-springboot && mvn compile"