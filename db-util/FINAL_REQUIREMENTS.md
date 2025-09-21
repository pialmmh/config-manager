# Final Requirements for DB-Util Library

## Core Requirement
Create a simple Spring Data JPA repository extension that adds MySQL extended insert capability.

## What to Build

### 1. Interface Definition
```java
package com.telcobright.util.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MySqlOptimizedRepository<T, ID> extends JpaRepository<T, ID> {

    /**
     * Performs batch insert using MySQL extended INSERT syntax.
     *
     * Generates: INSERT INTO table (col1, col2) VALUES (?, ?), (?, ?), (?, ?)
     * Instead of: Multiple individual INSERT statements
     *
     * @param entities List of entities to insert
     * @return Number of rows inserted
     */
    int insertExtendedToMysql(List<T> entities);
}
```

### 2. Implementation Requirements

The `insertExtendedToMysql` method must:

1. **Extract table and column names** from JPA annotations (@Table, @Column)
2. **Build MySQL extended INSERT SQL**:
   ```sql
   INSERT INTO users (id, name, email) VALUES
   (?, ?, ?),
   (?, ?, ?),
   (?, ?, ?)
   ```
3. **Use raw JDBC** via EntityManager.unwrap(Connection.class)
4. **Handle up to 100,000+ entities** efficiently (batch in chunks if needed)
5. **Return the count** of inserted rows

### 3. Must Support

#### String ID Type
```java
@Entity
@Table(name = "users")
public class User {
    @Id
    private String id;  // Must support String IDs (not auto-generated)

    @Column(name = "name")
    private String name;
}

public interface UserRepository extends MySqlOptimizedRepository<User, String> {
    // Standard JPA methods still work
}
```

#### Standard Spring Data JPA Features
All existing JpaRepository features must continue working:
- `findById()`, `save()`, `deleteById()`, etc.
- Derived query methods: `findByNameAndEmail()`
- @Query with JPQL: `@Query("SELECT u FROM User u WHERE...")`
- Native queries: `@Query(value = "SELECT...", nativeQuery = true)`

### 4. Repository Factory

```java
package com.telcobright.util.db.repository;

@Configuration
@EnableJpaRepositories(
    repositoryFactoryBeanClass = MySqlOptimizedRepositoryFactory.class
)
public class MySqlOptimizedRepositoryFactory extends JpaRepositoryFactoryBean {
    // Factory to create repository instances with MySQL optimization
}
```

### 5. Usage Example

```java
// Entity
@Entity
@Table(name = "products")
public class Product {
    @Id
    private String productId;

    @Column(name = "name")
    private String name;

    @Column(name = "price")
    private BigDecimal price;
}

// Repository
public interface ProductRepository extends MySqlOptimizedRepository<Product, String> {
    List<Product> findByPriceGreaterThan(BigDecimal price);

    @Query("SELECT p FROM Product p WHERE p.name LIKE %:keyword%")
    List<Product> searchByName(@Param("keyword") String keyword);
}

// Service
@Service
public class ProductService {
    @Autowired
    private ProductRepository repository;

    public void importProducts(List<Product> products) {
        // Standard JPA for small batches
        if (products.size() < 100) {
            repository.saveAll(products);
        } else {
            // MySQL optimization for large batches (5-10x faster)
            repository.insertExtendedToMysql(products);
        }
    }
}
```

## What NOT to Include

Do NOT add:
- Transaction management complexity (Spring handles it)
- Sharding logic (Split-Verse handles it)
- Custom annotations (only use standard JPA annotations)
- EntityManager exposure methods
- Configurable batch sizes (use reasonable default like 1000)
- Database detection logic (assume MySQL)

## Testing Requirements

### Test Setup
```sql
-- MySQL in LXC container
-- Connect: mysql -h 127.0.0.1 -u root -p123456

CREATE DATABASE testdb;
USE testdb;

CREATE TABLE users (
    id VARCHAR(100) PRIMARY KEY,
    name VARCHAR(255),
    email VARCHAR(255),
    created_at TIMESTAMP
);
```

### Test Cases
1. **Small batch**: Insert 100 entities
2. **Medium batch**: Insert 10,000 entities
3. **Large batch**: Insert 100,000 entities
4. **Performance comparison**: insertExtendedToMysql vs saveAll
5. **String IDs**: Verify String ID support
6. **Mixed operations**: Combine with standard JPA operations

### Expected Performance
- Standard JPA saveAll: ~15-20 seconds for 10,000 records
- insertExtendedToMysql: ~1-2 seconds for 10,000 records (5-10x faster)

## Project Structure
```
db-util/
├── src/main/java/com/telcobright/util/db/
│   └── repository/
│       ├── MySqlOptimizedRepository.java         (interface)
│       ├── MySqlOptimizedRepositoryImpl.java     (implementation)
│       └── MySqlOptimizedRepositoryFactory.java  (factory)
├── src/test/java/
│   └── MySqlOptimizedRepositoryTest.java
├── pom.xml
└── README.md
```

## Dependencies Required
```xml
<dependencies>
    <!-- Spring Data JPA -->
    <dependency>
        <groupId>org.springframework.data</groupId>
        <artifactId>spring-data-jpa</artifactId>
    </dependency>

    <!-- JPA API -->
    <dependency>
        <groupId>javax.persistence</groupId>
        <artifactId>javax.persistence-api</artifactId>
    </dependency>

    <!-- MySQL Connector -->
    <dependency>
        <groupId>mysql</groupId>
        <artifactId>mysql-connector-java</artifactId>
        <scope>runtime</scope>
    </dependency>
</dependencies>
```

## Success Criteria

✅ The library is successful if:

1. **It extends JpaRepository** with one additional method
2. **Standard JPA operations** continue working normally
3. **MySQL batch insert** is 5-10x faster than standard saveAll
4. **Works with String IDs** (not just Long/Integer)
5. **Supports custom queries** via @Query and derived methods
6. **Simple to use** - just change extends JpaRepository to extends MySqlOptimizedRepository

## Important Notes

1. **Keep it simple**: This is just JpaRepository + one optimized method
2. **No sharding logic**: Split-Verse handles all sharding/routing
3. **No custom annotations**: Only standard JPA annotations
4. **MySQL only**: The optimization is MySQL-specific
5. **Raw SQL is fine**: Use JDBC for the batch insert, not JPA

## Example Implementation Approach

```java
@Repository
public class MySqlOptimizedRepositoryImpl<T, ID>
    extends SimpleJpaRepository<T, ID>
    implements MySqlOptimizedRepository<T, ID> {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public int insertExtendedToMysql(List<T> entities) {
        if (entities == null || entities.isEmpty()) {
            return 0;
        }

        // Get table name from @Table annotation
        String tableName = extractTableName();

        // Get column names from @Column annotations
        List<String> columns = extractColumns();

        // Build SQL: INSERT INTO table (col1, col2) VALUES (?, ?), (?, ?)
        String sql = buildExtendedInsertSql(tableName, columns, entities.size());

        // Execute using raw JDBC
        Connection conn = entityManager.unwrap(Connection.class);
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // Set parameters for all entities
            setParameters(pstmt, entities);
            return pstmt.executeUpdate();
        }
    }
}
```

## Contact
- MySQL LXC: `mysql -h 127.0.0.1 -u root -p123456`
- Related: `/home/mustafa/telcobright-projects/routesphere/util/src/main/java/com/telcobright/util/db/sqlgen/`