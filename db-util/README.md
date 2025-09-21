# DB Util - MySQL Optimized JPA Repository

Spring Data JPA extension that adds MySQL batch insert optimization.

## Installation

```xml
<dependency>
    <groupId>com.telcobright</groupId>
    <artifactId>dbutil</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

## Setup

Enable in your Spring Boot application:

```java
import com.telcobright.util.db.repository.MySqlOptimizedRepositoryFactory;

@SpringBootApplication
@EnableJpaRepositories(
    repositoryFactoryBeanClass = MySqlOptimizedRepositoryFactory.class
)
public class Application {
}
```

## Usage

### 1. Create Repository

Change `extends JpaRepository` to `extends MySqlOptimizedRepository`:

```java
import com.telcobright.util.db.repository.MySqlOptimizedRepository;

public interface UserRepository extends MySqlOptimizedRepository<User, String> {
    // All JpaRepository methods available
    // Plus: insertExtendedToMysql() for batch insert
}
```

### 2. Use Batch Insert

```java
@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public void importUsers(List<User> users) {
        // MySQL optimized batch insert (5-10x faster)
        int count = userRepository.insertExtendedToMysql(users);
    }
}
```

## Features

- **All JpaRepository methods** work normally (save, findById, delete, etc.)
- **Custom queries** supported (@Query, derived methods)
- **String ID support** (works with String, Long, Integer)
- **Auto-chunking** for large batches (default 1000 per chunk)
- **5-10x faster** than standard saveAll() for bulk inserts

## Example Entity

```java
@Entity
@Table(name = "users")
public class User {
    @Id
    private String id;  // String ID supported

    @Column(name = "email")
    private String email;

    // getters, setters
}
```

## API

### MySqlOptimizedRepository<T, ID>

Extends `JpaRepository<T, ID>` with:

```java
int insertExtendedToMysql(List<T> entities)
```

Returns number of rows inserted. Handles 100,000+ entities efficiently.

## Requirements

- Spring Data JPA 2.x+
- MySQL 5.6+
- Java 8+