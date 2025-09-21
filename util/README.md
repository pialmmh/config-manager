# Telcobright Util Library

MySQL-optimized JPA Repository extension for Spring Data JPA.

## Installation

```xml
<dependency>
    <groupId>com.telcobright</groupId>
    <artifactId>util</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

## Quick Start

### 1. Enable in your Spring Boot application

```java
import com.telcobright.util.db.repository.MySqlOptimizedRepositoryFactory;

@EnableJpaRepositories(
    repositoryFactoryBeanClass = MySqlOptimizedRepositoryFactory.class
)
```

### 2. Create repository

```java
import com.telcobright.util.db.repository.MySqlOptimizedRepository;

public interface ProductRepository extends MySqlOptimizedRepository<Product, Long> {
    // Inherits all JpaRepository methods + MySQL optimized batch insert
}
```

### 3. Use it

```java
// Standard JPA
productRepository.save(product);
productRepository.findById(1L);

// MySQL optimized batch insert (5-10x faster)
productRepository.insertExtendedToMysql(productList);
```

## Features

- Extends Spring Data JPA's `JpaRepository`
- MySQL optimized batch insert using extended INSERT syntax
- 5-10x faster for bulk inserts
- Automatic fallback to standard JPA if optimization fails

## Requirements

- Spring Data JPA 2.x+
- MySQL 5.6+
- Java 8+