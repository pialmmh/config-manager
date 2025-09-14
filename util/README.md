# Util Module - SpringToQuarkusEntityConverter

## Overview
The `util` module provides utility classes for the RouteSphere project. The main utility is `SpringToQuarkusEntityConverter`, which converts Spring Boot entity classes to Quarkus-compatible classes by removing Spring-specific annotations while preserving specified annotations.

## SpringToQuarkusEntityConverter

### Purpose
Converts Spring Boot domain model classes to Quarkus-compatible classes by:
- Removing Spring-specific imports
- Removing Spring annotations (@Service, @Repository, @Autowired, etc.)
- Preserving JPA/Jakarta Persistence annotations
- Preserving Lombok annotations
- Preserving Jackson annotations

### Usage

#### Command Line
```bash
java -cp util-1.0-SNAPSHOT.jar com.telcobright.util.db.SpringToQuarkusEntityConverter \
  <source_dir> <dest_dir> [annotations_to_preserve]
```

#### Maven Integration (from ConfigManager)
```bash
# Using the new converter
mvn clean validate -Psync-models-v2

# Using the old shell script method
mvn clean validate -Psync-models
```

### Configuration in ConfigManager

The `sync-models-v2` profile in ConfigManager's `pom.xml` is configured to:
1. Read source files from: `ConfigManager/src/main/java/com/telcobright/rtc/domainmodel`
2. Write converted files to: `routesphere-core/src/main/java/com/telcobright/rtc/domainmodel`
3. Preserve these annotations:
   - **JPA/Jakarta**: Entity, Table, Id, GeneratedValue, Column, ManyToOne, OneToMany, etc.
   - **Lombok**: Data, Getter, Setter, ToString, Builder, etc.
   - **Jackson**: JsonIgnore, JsonProperty, JsonFormat, etc.

### Features

1. **Recursive Processing**: Processes all Java files in subdirectories
2. **Clean Sync**: Deletes destination directory before copying
3. **Smart Filtering**: Removes only Spring-specific code
4. **Preserves Structure**: Maintains package structure and file organization
5. **Logging**: Provides detailed logging of the conversion process

### Annotations Removed

The converter removes these Spring-specific annotations:
- @Repository, @Service, @Component, @Controller, @RestController
- @Configuration, @SpringBootApplication, @Bean
- @Autowired, @Value, @Qualifier, @Primary
- @RequestMapping, @GetMapping, @PostMapping, @PutMapping, @DeleteMapping
- @PathVariable, @RequestParam, @RequestBody, @ResponseBody
- @Transactional, @EnableAutoConfiguration, @ComponentScan
- And many more Spring-specific annotations

### Annotations Preserved

By default, the converter preserves:
- JPA/Jakarta Persistence annotations
- Lombok annotations
- Jackson annotations
- Custom business annotations

### Example

**Before Conversion (Spring Boot):**
```java
package com.telcobright.rtc.domainmodel;

import org.springframework.stereotype.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.persistence.*;
import lombok.Data;

@Repository
@Entity
@Data
@Table(name = "partner")
public class Partner {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Autowired
    private SomeService service;
}
```

**After Conversion (Quarkus):**
```java
package com.telcobright.rtc.domainmodel;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "partner")
public class Partner {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
}
```

## Building the Module

```bash
cd /home/mustafa/telcobright-projects/routesphere/util
mvn clean install
```

## Dependencies

- Apache Commons IO: File operations
- Apache Commons Lang3: String utilities
- SLF4J/Logback: Logging
- JUnit: Testing (test scope)