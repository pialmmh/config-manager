#!/bin/bash
echo "Quick test of tenant API..."
curl -X POST http://localhost:7070/get-tenant-root \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -s | jq '.' 2>/dev/null || echo "ConfigManager not running or jq not installed"
