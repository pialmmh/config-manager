# DB-Util Minimal Requirements for Split-Verse

## Overview
Split-Verse needs a simple JPA repository that extends JpaRepository with ONE additional method for MySQL extended insert. Keep it minimalistic.

## Core Requirement

```java
public interface MySqlOptimizedRepository<T, ID> extends JpaRepository<T, ID> {

    // The ONLY custom method needed
    int insertExtendedToMysql(List<T> entities);
}
```

## Must Support

### 1. String ID Type
```java
// Must work with String IDs (not just Long)
public interface UserRepository extends MySqlOptimizedRepository<User, String> {
    // Standard JPA methods work
    Optional<User> findById(String id);

    // Custom JPQL must work
    @Query("SELECT u FROM User u WHERE u.createdAt BETWEEN :start AND :end")
    List<User> findByDateRange(@Param("start") LocalDateTime start,
                               @Param("end") LocalDateTime end);
}
```

### 2. Standard Spring Data JPA Features
- Derived query methods: `findByCustomerIdAndStatus()`
- @Query with JPQL: `@Query("SELECT o FROM Order o WHERE...")`
- Native queries: `@Query(value = "SELECT * FROM...", nativeQuery = true)`
- All standard JpaRepository methods

### 3. MySQL Extended Insert Implementation
```java
// The insertExtendedToMysql should generate:
INSERT INTO users (id, name, email) VALUES
    (?, ?, ?),
    (?, ?, ?),
    (?, ?, ?)  -- Single INSERT, multiple rows

// Instead of standard JPA batch which does:
INSERT INTO users (id, name, email) VALUES (?, ?, ?);
INSERT INTO users (id, name, email) VALUES (?, ?, ?);
INSERT INTO users (id, name, email) VALUES (?, ?, ?);
```

## That's It!

No need for:
- Complex transaction propagation
- EntityManager exposure
- Metadata access methods
- Configurable batch sizes (use reasonable default like 1000)

## Testing

Test with:
```java
@Entity
@Table(name = "users")
public class User {
    @Id
    private String id;  // String ID, not auto-generated

    private String name;
    private String email;
    private LocalDateTime createdAt;
}

// Repository
public interface UserRepository extends MySqlOptimizedRepository<User, String> {
    List<User> findByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.createdAt > :date")
    List<User> findRecentUsers(@Param("date") LocalDateTime date);
}

// Usage
List<User> users = createMillionUsers();
userRepository.insertExtendedToMysql(users);  // 5-10x faster than saveAll()
```

## MySQL Connection
```bash
# Test with MySQL in LXC
mysql -h 127.0.0.1 -u root -p123456
```

## Summary
Just make JpaRepository + one MySQL batch insert method. Keep it simple.