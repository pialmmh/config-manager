# RouteSphere Library Integration Guide

## Available Libraries

The following libraries are available for use in RTC-Manager projects:

```xml
<!-- State Machine Library -->
<dependency>
    <groupId>com.telcobright</groupId>
    <artifactId>statemachine</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>

<!-- Partitioned Repository -->
<dependency>
    <groupId>com.telcobright.db</groupId>
    <artifactId>partitioned-repo</artifactId>
    <version>1.0.0</version>
</dependency>

<!-- Infinite Scheduler -->
<dependency>
    <groupId>com.telcobright</groupId>
    <artifactId>infinite-scheduler</artifactId>
    <version>1.0.0</version>
</dependency>

<!-- Chronicle DB Cache -->
<dependency>
    <groupId>com.telcobright</groupId>
    <artifactId>chronicle-db-cache</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

## How to Add Libraries to Your Project

### Step 1: Add Dependencies to Your POM

Edit your project's `pom.xml` and add the required dependencies in the `<dependencies>` section.

Example for ConfigManager (`RTC-Manager/ConfigManager/pom.xml`):

```xml
<dependencies>
    <!-- Existing dependencies... -->
    
    <!-- RouteSphere Libraries -->
    <dependency>
        <groupId>com.telcobright</groupId>
        <artifactId>statemachine</artifactId>
        <version>1.0-SNAPSHOT</version>
    </dependency>
    
    <dependency>
        <groupId>com.telcobright.db</groupId>
        <artifactId>partitioned-repo</artifactId>
        <version>1.0.0</version>
    </dependency>
    
    <!-- Add other libraries as needed -->
</dependencies>
```

### Step 2: Build and Install Libraries

Before using the libraries, they must be installed to your local Maven repository:

```bash
cd /home/mustafa/telcobright-projects/routesphere
./update-libs.sh
```

### Step 3: Rebuild Your Project

After adding dependencies and installing libraries:

```bash
cd RTC-Manager/ConfigManager
mvn clean compile
```

## When Libraries Change

### Quick Update (Single Library)

If you've modified a specific library:

```bash
# Example: Updated statemachine
cd /home/mustafa/telcobright-projects/routesphere/statemachine
mvn clean install -DskipTests
```

### Full Update (All Libraries)

To update all libraries:

```bash
cd /home/mustafa/telcobright-projects/routesphere
./update-libs.sh
```

### Apply Changes to Applications

After updating libraries, restart your Spring Boot/Quarkus applications:

```bash
# For Spring Boot
cd RTC-Manager/ConfigManager
mvn spring-boot:run

# Or if running as JAR
java -jar target/your-app.jar
```

## Example Usage in Code

### Using StateMachine

```java
import com.telcobright.statemachine.GenericStateMachine;
import com.telcobright.statemachine.StateMachineRegistry;

@Service
public class CallService {
    @Autowired
    private StateMachineRegistry registry;
    
    public void processCall() {
        // Use the state machine
    }
}
```

### Using Partitioned Repository

```java
import com.telcobright.db.partitioned.PartitionedRepository;

@Repository
public class DataRepository {
    // Use partitioned repo features
}
```

## Version Management

- **During Development**: Use `-SNAPSHOT` versions
- **For Production**: Use fixed versions (e.g., `1.0.0`, `1.1.0`)
- **Version Updates**: Update version in library POM, rebuild, then update consumer POMs

## Troubleshooting

### Library Not Found

If Maven can't find a library:

1. Ensure the library is built: `cd [library] && mvn clean install`
2. Check version matches exactly
3. Verify groupId and artifactId are correct

### Class Not Found at Runtime

1. Rebuild the library: `mvn clean install`
2. Rebuild your application: `mvn clean package`
3. Restart the application

### Conflicts

If you see dependency conflicts:

1. Use `mvn dependency:tree` to analyze
2. Add exclusions if needed
3. Ensure all libraries use compatible versions of shared dependencies