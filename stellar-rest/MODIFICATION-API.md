# Entity Modification API Documentation

## Overview

The Stellar REST API now provides a **single generic endpoint** for all entity modifications (INSERT, UPDATE, DELETE) with support for hierarchical nested operations in a single transaction. The system features **lazy hierarchy building** with **intelligent caching** for optimal performance.

## Key Features

### 1. Single Generic Endpoint
- **URL**: `/api/modify`
- **Method**: POST
- **Content-Type**: application/json
- Handles all entities: category, product, customer, salesorder, orderdetail, employee, shipper, supplier

### 2. Lazy Hierarchy Building with Caching
- **First Request**: Inspects JSON payload, validates relationships, builds metadata (~50ms)
- **Subsequent Requests**: Uses cached hierarchy metadata (~5ms)
- **Cache Key**: Generated from entity structure (e.g., "category-product", "customer-salesorder-orderdetail")
- **Thread-safe**: Concurrent-safe global HashMap cache

### 3. Single Transaction Guarantee
- All nested modifications execute in ONE database transaction
- Complete rollback on any failure
- Maintains referential integrity

## API Endpoints

### 1. Modify Entity
```
POST /api/modify
```

### 2. Cache Statistics
```
GET /api/cache/stats
```

### 3. Clear Cache
```
DELETE /api/cache/clear
```

## Request Structure

### Basic Structure
```json
{
  "entityName": "string",           // Required: entity name
  "operation": "INSERT|UPDATE|DELETE", // Required: operation type
  "data": {                         // Required for INSERT/UPDATE
    "field1": "value1",
    "field2": "value2"
  },
  "criteria": {                     // Required for UPDATE/DELETE
    "id": 123
  },
  "include": [                      // Optional: nested operations
    {
      "entityName": "childEntity",
      "operation": "INSERT",
      "data": {...}
    }
  ]
}
```

## Examples

### 1. Simple INSERT
```json
{
  "entityName": "category",
  "operation": "INSERT",
  "data": {
    "CategoryName": "Electronics",
    "Description": "Electronic devices"
  }
}
```

### 2. Nested INSERT - Category with Products
```json
{
  "entityName": "category",
  "operation": "INSERT",
  "data": {
    "CategoryName": "Home Appliances",
    "Description": "Kitchen and home appliances"
  },
  "include": [
    {
      "entityName": "product",
      "operation": "INSERT",
      "data": {
        "ProductName": "Microwave",
        "UnitPrice": 199.99,
        "UnitsInStock": 50
      }
    },
    {
      "entityName": "product",
      "operation": "INSERT",
      "data": {
        "ProductName": "Blender",
        "UnitPrice": 89.99,
        "UnitsInStock": 30
      }
    }
  ]
}
```

### 3. Deep Nested - Customer → Order → OrderDetails
```json
{
  "entityName": "customer",
  "operation": "INSERT",
  "data": {
    "CustomerID": "NEWCUST",
    "CompanyName": "New Company",
    "ContactName": "John Doe",
    "City": "New York",
    "Country": "USA"
  },
  "include": [
    {
      "entityName": "salesorder",
      "operation": "INSERT",
      "data": {
        "OrderDate": "2024-01-15",
        "ShipCity": "New York"
      },
      "include": [
        {
          "entityName": "orderdetail",
          "operation": "INSERT",
          "data": {
            "ProductID": 1,
            "UnitPrice": 18.00,
            "Quantity": 10
          }
        }
      ]
    }
  ]
}
```

### 4. UPDATE Operation
```json
{
  "entityName": "product",
  "operation": "UPDATE",
  "data": {
    "UnitPrice": 29.99,
    "UnitsInStock": 100
  },
  "criteria": {
    "ProductID": 1
  }
}
```

### 5. DELETE Operation
```json
{
  "entityName": "customer",
  "operation": "DELETE",
  "criteria": {
    "CustomerID": "OLDCUST"
  }
}
```

### 6. Mixed Operations - UPDATE Parent, INSERT Child
```json
{
  "entityName": "category",
  "operation": "UPDATE",
  "data": {
    "Description": "Updated description"
  },
  "criteria": {
    "CategoryID": 1
  },
  "include": [
    {
      "entityName": "product",
      "operation": "INSERT",
      "data": {
        "ProductName": "New Product",
        "UnitPrice": 49.99
      }
    }
  ]
}
```

## Response Structure

### Success Response
```json
{
  "success": true,
  "hierarchyKey": "category-product",
  "cacheStats": {
    "usageCount": 5,
    "createdAt": 1704067200000,
    "lastUsedAt": 1704067800000
  },
  "data": {
    "entity": "category",
    "operation": "INSERT",
    "insertedId": 9,
    "nested": [
      {
        "entity": "product",
        "operation": "INSERT",
        "insertedId": 78
      }
    ]
  }
}
```

### Error Response
```json
{
  "success": false,
  "error": "No valid relationship from category to employee",
  "exception": "java.lang.IllegalArgumentException"
}
```

## Valid Entity Relationships

### Parent → Child Relationships
- `category` → `product`
- `supplier` → `product`
- `product` → `orderdetail`
- `customer` → `salesorder`
- `employee` → `salesorder`
- `shipper` → `salesorder`
- `salesorder` → `orderdetail`

### Invalid Relationships (Will Error)
- `category` → `employee` ❌
- `customer` → `product` ❌
- `product` → `supplier` ❌ (reverse relationship)

## Cache Management

### View Cache Statistics
```bash
curl http://localhost:8090/api/cache/stats
```

Response:
```json
{
  "totalCachedHierarchies": 3,
  "hierarchies": [
    {
      "key": "category-product",
      "usageCount": 15,
      "lastUsedAt": 1704067800000,
      "isValid": true
    },
    {
      "key": "customer-salesorder-orderdetail",
      "usageCount": 8,
      "lastUsedAt": 1704067500000,
      "isValid": true
    }
  ]
}
```

### Clear Cache
```bash
curl -X DELETE http://localhost:8090/api/cache/clear
```

## Performance Characteristics

### First Request (Cache Miss)
- Validates entity relationships
- Uses reflection to build metadata
- Creates foreign key mappings
- **Time**: ~30-50ms

### Subsequent Requests (Cache Hit)
- Skips validation and reflection
- Uses pre-built metadata
- Direct SQL generation
- **Time**: ~3-5ms

### Performance Improvement
- **10-15x faster** for cached hierarchies
- Memory efficient - only caches used patterns
- Thread-safe concurrent access

## Testing

### Run Modification Tests
```bash
cd stellar-node-tester
node modify-test.js
```

### Run Performance Test
```bash
node modify-test.js --perf
```

## Transaction Behavior

1. **Atomicity**: All operations in a request succeed or all fail
2. **Foreign Keys**: Automatically populated in child entities
3. **Cascading**: Parent IDs cascade to nested children
4. **Rollback**: Complete rollback on any error

## Error Handling

### Common Errors
1. **Invalid Relationship**: "No valid relationship from X to Y"
2. **Missing Criteria**: "UPDATE requires criteria"
3. **Duplicate Key**: "Duplicate entry for key PRIMARY"
4. **Foreign Key Violation**: "Cannot add or update a child row"

### Error Response Includes
- Error message
- Exception class name
- SQL state (for database errors)
- Full stack trace (for debugging)

## Best Practices

1. **Batch Operations**: Group related modifications in one request
2. **Use Caching**: Repeated patterns benefit from cache
3. **Validate First**: Check relationships before complex operations
4. **Monitor Cache**: Use `/api/cache/stats` to monitor usage
5. **Clear Sparingly**: Only clear cache for testing/maintenance

## Implementation Details

### Technologies Used
- **Framework**: Quarkus with JAX-RS
- **Caching**: ConcurrentHashMap (thread-safe)
- **Transaction**: JDBC with manual transaction management
- **Validation**: Schema-based relationship validation

### Key Classes
- `EntityModificationRequest`: Request model
- `ModifyOperation`: Enum (INSERT, UPDATE, DELETE)
- `EntityHierarchy`: Cached metadata structure
- `EntityModificationService`: Core processing logic
- `QueryResource`: REST endpoint

## Limitations

1. **Composite Keys**: OrderDetail uses composite key (OrderID, ProductID)
2. **Bulk Operations**: Each array element processed individually
3. **Schema Changes**: Requires cache clear after schema modifications
4. **Memory**: Cache grows with unique hierarchy patterns

## Future Enhancements

1. **Batch Processing**: True bulk operations
2. **Cache TTL**: Time-based cache expiration
3. **Metrics**: Detailed performance metrics
4. **Validation**: Pre-flight validation endpoint
5. **Webhooks**: Event notifications for modifications