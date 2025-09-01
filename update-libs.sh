#!/bin/bash
# Simple script to update libraries after changes

echo "========================================="
echo "Building and installing RouteSphere libraries to local Maven repository..."
echo "========================================="

# Track success/failure
SUCCESS_COUNT=0
FAIL_COUNT=0
FAILED_LIBS=""

# Build each library
for lib in statemachine infinite-scheduler partitioned-repo chronicle-db-Cache; do
    if [ -d "$lib" ]; then
        echo ""
        echo "📦 Building $lib..."
        echo "-----------------------------------------"
        if (cd "$lib" && mvn clean install -DskipTests); then
            echo "✅ $lib built successfully"
            ((SUCCESS_COUNT++))
        else
            echo "❌ Failed to build $lib"
            ((FAIL_COUNT++))
            FAILED_LIBS="$FAILED_LIBS $lib"
        fi
    else
        echo "⚠️  Directory $lib not found, skipping..."
    fi
done

echo ""
echo "========================================="
echo "Build Summary:"
echo "✅ Successfully built: $SUCCESS_COUNT libraries"

if [ $FAIL_COUNT -gt 0 ]; then
    echo "❌ Failed to build: $FAIL_COUNT libraries"
    echo "   Failed libraries:$FAILED_LIBS"
    echo ""
    echo "Please check the errors above and try again."
    exit 1
else
    echo ""
    echo "🎉 All libraries updated in local Maven repository (~/.m2/repository)"
    echo ""
    echo "Next steps:"
    echo "1. Restart your Spring Boot/Quarkus applications to use the latest versions"
    echo "2. Or run 'mvn clean compile' in your application directories"
fi

echo "========================================="