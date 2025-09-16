#!/bin/bash

echo "Testing ConfigManager Tenant API endpoint..."
echo "============================================"
echo ""

# Test if ConfigManager is running
echo "Checking if ConfigManager is running on port 7070..."
if curl -s -o /dev/null -w "%{http_code}" http://localhost:7070/actuator/health 2>/dev/null | grep -q "200"; then
    echo "✅ ConfigManager is running"
else
    echo "❌ ConfigManager is not running or not reachable on port 7070"
    echo "   Please start ConfigManager first with: mvn spring-boot:run"
    exit 1
fi

echo ""
echo "Testing /get-tenant-root endpoint..."
echo "-----------------------------------"

# Make the API call
response=$(curl -X POST http://localhost:7070/get-tenant-root \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -s -w "\nHTTP_STATUS_CODE:%{http_code}")

# Extract status code
http_code=$(echo "$response" | grep "HTTP_STATUS_CODE:" | cut -d: -f2)
body=$(echo "$response" | sed '/HTTP_STATUS_CODE:/d')

echo "Response Status Code: $http_code"
echo ""

if [ "$http_code" = "200" ]; then
    echo "✅ API call successful!"
    echo "Response body (first 500 chars):"
    echo "$body" | head -c 500
    echo ""

    # Try to extract dbName using jq if available
    if command -v jq &> /dev/null; then
        echo ""
        echo "Parsed Tenant info:"
        echo "  DB Name: $(echo "$body" | jq -r '.dbName // "N/A"')"
        echo "  Children count: $(echo "$body" | jq '.children | length // 0')"
    fi
else
    echo "❌ API call failed with status: $http_code"
    echo "Response body:"
    echo "$body"
fi