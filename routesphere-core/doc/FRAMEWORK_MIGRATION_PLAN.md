# RouteSphere Framework Migration Plan

## Executive Summary

RouteSphere needs to evolve from a monolithic application containing business logic to a reusable framework/library that Line of Business (LoB) applications can build upon. This document outlines the migration strategy, architectural options, and implementation roadmap.

## Current State

### Problems
- Business logic (CRM package) is embedded within RouteSphere core
- Tight coupling between framework and application code
- No clear separation between protocol handling and business logic
- Difficult to scale for multiple LoB apps (ERP, Accounting, etc.)
- Cannot independently version or deploy LoB apps

### Current Structure
```
routesphere/
├── routesphere-core/
│   ├── protocols/          # Protocol implementations
│   ├── pipeline/           # Pipeline framework
│   │   └── crm/           # ❌ Business logic mixed with framework
│   └── channels/          # Channel abstractions
```

## Target Architecture

### Goals
1. **Separation of Concerns**: Clear boundary between framework and business logic
2. **Reusability**: RouteSphere as a library that multiple apps can use
3. **Independent Deployment**: Each LoB app can be deployed separately
4. **Protocol Abstraction**: Unified interface for HTTP, ESL, SIP, etc.
5. **Configuration Flexibility**: Support both YAML and Quarkus native config

## Architecture Options

### Option 1: Library/Framework Approach

**Structure:**
```
routesphere-framework/
├── routesphere-api/           # Interfaces and annotations
├── routesphere-core/          # Core implementations
├── routesphere-http/          # HTTP protocol module
├── routesphere-esl/           # ESL protocol module
├── routesphere-sip/           # SIP protocol module
└── routesphere-starter/       # Quick-start templates

Separate repositories:
├── crm-app/                   # Uses routesphere-framework
├── erp-app/                   # Uses routesphere-framework
└── accounting-app/            # Uses routesphere-framework
```

**Pros:**
- Clean separation
- Independent versioning
- Can publish to Maven Central
- Each LoB app has full control

**Cons:**
- More complex dependency management
- Need to maintain backward compatibility
- Requires Maven repository (local or remote)

### Option 2: Quarkus Extension

**Implementation:**
```java
// routesphere-extension/
├── deployment/
│   └── RouteSpherProcessor.java    # Build-time processing
├── runtime/
│   └── RouteSpherRecorder.java     # Runtime configuration
└── pom.xml
```

**Usage in LoB Apps:**
```xml
<dependency>
    <groupId>com.telcobright</groupId>
    <artifactId>quarkus-routesphere</artifactId>
    <version>1.0.0</version>
</dependency>
```

**Pros:**
- Native Quarkus integration
- Build-time optimization
- Automatic configuration
- Seamless developer experience

**Cons:**
- Tied to Quarkus ecosystem
- More complex to develop
- Requires understanding of Quarkus internals

### Option 3: Plugin Architecture

**Core Framework:**
```java
public interface RouteSpherePlugin {
    void register(RouteSphereRegistry registry);
    List<Route> getRoutes();
    List<Pipeline> getPipelines();
}
```

**LoB Implementation:**
```java
@Plugin(name = "crm")
public class CrmPlugin implements RouteSpherePlugin {
    @Override
    public void register(RouteSphereRegistry registry) {
        registry.addRoute("/api/crm", CrmHandler.class);
        registry.addPipeline("crm-pipeline", CrmPipeline.class);
    }
}
```

**Pros:**
- Dynamic loading of functionality
- Can add/remove features at runtime
- Good for multi-tenant scenarios

**Cons:**
- Complex classloader management
- Potential security issues
- Harder to debug

### Option 4: Microservices with Shared Library

**Architecture:**
```yaml
services:
  routesphere-gateway:
    role: API Gateway & Protocol Handler

  crm-service:
    depends_on: routesphere-sdk

  erp-service:
    depends_on: routesphere-sdk
```

**Pros:**
- True microservices architecture
- Independent scaling
- Technology diversity possible

**Cons:**
- Network overhead
- Complex deployment
- Service discovery needed

## Recommended Approach: Hybrid Solution

### Phase 1: Module Separation (Week 1-2)

1. **Extract API Module**
```xml
<module>routesphere-api</module>
<!-- Contains interfaces, annotations, DTOs -->
```

2. **Extract Protocol Modules**
```xml
<module>routesphere-protocol-http</module>
<module>routesphere-protocol-esl</module>
<module>routesphere-protocol-sip</module>
```

3. **Create Core Module**
```xml
<module>routesphere-core</module>
<!-- Pipeline, Channel abstractions -->
```

### Phase 2: Framework Creation (Week 3-4)

1. **Create Parent POM**
```xml
<project>
    <groupId>com.telcobright.routesphere</groupId>
    <artifactId>routesphere-framework</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <packaging>pom</packaging>

    <modules>
        <module>routesphere-api</module>
        <module>routesphere-core</module>
        <module>routesphere-protocol-http</module>
        <module>routesphere-protocol-esl</module>
        <module>routesphere-protocol-sip</module>
    </modules>
</project>
```

2. **Define Framework APIs**
```java
// routesphere-api/src/main/java/com/telcobright/routesphere/api/
public interface Channel {
    void initialize(ChannelConfig config);
    void start();
    void stop();
}

public interface Pipeline {
    PipelineResult process(PipelineContext context);
}

@Qualifier
@Retention(RUNTIME)
public @interface RouteSphereChannel {
    String value();
}
```

### Phase 3: Extract Business Logic (Week 5-6)

1. **Create CRM Application**
```bash
crm-app/
├── src/main/java/
│   └── com/company/crm/
│       ├── api/
│       │   └── CustomerResource.java
│       ├── pipeline/
│       │   └── CrmPipeline.java
│       └── CrmApplication.java
├── src/main/resources/
│   └── application.yml
└── pom.xml
```

2. **CRM POM Configuration**
```xml
<dependencies>
    <dependency>
        <groupId>com.telcobright.routesphere</groupId>
        <artifactId>routesphere-core</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </dependency>
    <dependency>
        <groupId>com.telcobright.routesphere</groupId>
        <artifactId>routesphere-protocol-http</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </dependency>
</dependencies>
```

### Phase 4: Configuration Migration (Week 7)

1. **Framework Configuration**
```java
@ConfigMapping(prefix = "routesphere")
public interface RouteSphereConfig {
    boolean enabled();
    List<ChannelConfig> channels();

    interface ChannelConfig {
        String name();
        String protocol();
        boolean enabled();
        Map<String, String> properties();
    }
}
```

2. **LoB App Configuration**
```yaml
# crm-app/application.yml
routesphere:
  enabled: true
  channels:
    - name: crm-http
      protocol: http
      enabled: true
      properties:
        port: "8081"
        base-path: "/api/crm"

    - name: crm-telephony
      protocol: esl
      enabled: true
      properties:
        host: "103.95.96.98"
        port: "8021"
        password: "${ESL_PASSWORD}"
```

## Implementation Roadmap

### Milestone 1: Framework Extraction (Weeks 1-4)
- [ ] Create module structure
- [ ] Extract interfaces and APIs
- [ ] Separate protocol implementations
- [ ] Create build configuration
- [ ] Unit tests for framework

### Milestone 2: CRM Extraction (Weeks 5-6)
- [ ] Create CRM application project
- [ ] Move business logic from framework
- [ ] Configure dependency on framework
- [ ] Integration tests

### Milestone 3: Documentation & Examples (Week 7)
- [ ] API documentation
- [ ] Developer guide
- [ ] Example applications
- [ ] Migration guide for existing code

### Milestone 4: Advanced Features (Week 8+)
- [ ] Quarkus extension development
- [ ] Plugin system
- [ ] Service mesh integration
- [ ] Observability (metrics, tracing)

## Technical Considerations

### Dependency Management
```xml
<!-- Framework BOM -->
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>com.telcobright.routesphere</groupId>
            <artifactId>routesphere-bom</artifactId>
            <version>${routesphere.version}</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

### Backward Compatibility
- Semantic versioning (MAJOR.MINOR.PATCH)
- Deprecation policy (minimum 2 minor versions)
- Migration guides for breaking changes

### Testing Strategy
1. **Unit Tests**: Each module independently
2. **Integration Tests**: Framework modules together
3. **E2E Tests**: Complete LoB app with framework
4. **Performance Tests**: Benchmark protocol handlers

## Configuration Examples

### HTTP Channel with JAX-RS
```java
@ApplicationScoped
public class HttpChannelAdapter implements ChannelAdapter {

    @Inject
    @ConfigProperty(name = "routesphere.channels.http.port")
    int port;

    @Override
    public void initialize() {
        // Programmatically create JAX-RS endpoints
        Router router = Router.router(vertx);
        router.route("/api/*").handler(this::handleRequest);

        vertx.createHttpServer()
            .requestHandler(router)
            .listen(port);
    }
}
```

### ESL Channel
```java
@ApplicationScoped
public class EslChannelAdapter implements ChannelAdapter {

    @Inject
    RouteSphereConfig config;

    @Override
    public void initialize() {
        ChannelConfig eslConfig = findChannel("esl");
        String host = eslConfig.properties().get("host");
        int port = Integer.parseInt(eslConfig.properties().get("port"));

        EslClient client = new EslClient(host, port);
        client.connect();
    }
}
```

## Risks and Mitigation

| Risk | Impact | Mitigation |
|------|--------|------------|
| Breaking existing code | High | Maintain compatibility layer |
| Complex migration | Medium | Phased approach with clear milestones |
| Performance degradation | Medium | Benchmark before/after |
| Dependency conflicts | Low | Use dependency management |
| Learning curve | Medium | Comprehensive documentation |

## Success Criteria

1. **Technical**
   - Framework published as separate artifacts
   - CRM app runs independently
   - All tests passing
   - Performance within 5% of current

2. **Developer Experience**
   - Clear documentation
   - Example applications
   - IDE support (auto-completion)
   - Hot reload working

3. **Business**
   - Multiple LoB apps can use framework
   - Independent deployment possible
   - Reduced time to create new apps

## Next Steps

1. **Immediate Actions**
   - Review and approve this plan
   - Set up multi-module Maven structure
   - Create framework API interfaces

2. **Short Term** (2 weeks)
   - Extract protocol implementations
   - Create first LoB app (CRM)
   - Write integration tests

3. **Long Term** (1-2 months)
   - Develop Quarkus extension
   - Create additional LoB apps
   - Performance optimization

## References

- [Quarkus Extension Guide](https://quarkus.io/guides/writing-extensions)
- [Maven Multi-Module Projects](https://maven.apache.org/guides/mini/guide-multiple-modules.html)
- [Semantic Versioning](https://semver.org/)
- [JAX-RS Dynamic Registration](https://eclipse-ee4j.github.io/jersey.github.io/documentation/latest/resource-builder.html)

## Appendix: Sample Code Structure

### Framework Module Structure
```
routesphere-framework/
├── routesphere-api/
│   ├── src/main/java/
│   │   └── com/telcobright/routesphere/api/
│   │       ├── Channel.java
│   │       ├── Pipeline.java
│   │       ├── ChannelConfig.java
│   │       └── annotations/
│   └── pom.xml
├── routesphere-core/
│   ├── src/main/java/
│   │   └── com/telcobright/routesphere/core/
│   │       ├── AbstractChannel.java
│   │       ├── PipelineManager.java
│   │       └── ChannelManager.java
│   └── pom.xml
└── pom.xml
```

### LoB Application Structure
```
crm-app/
├── src/main/java/
│   └── com/company/crm/
│       ├── CrmApplication.java
│       ├── config/
│       │   └── CrmConfiguration.java
│       ├── api/
│       │   ├── CustomerResource.java
│       │   └── OrderResource.java
│       ├── pipeline/
│       │   ├── ValidationPipeline.java
│       │   └── EnrichmentPipeline.java
│       └── service/
│           └── CustomerService.java
├── src/main/resources/
│   ├── application.yml
│   └── META-INF/
│       └── resources/
└── pom.xml
```

---

*Document Version: 1.0*
*Date: 2025-09-17*
*Author: RouteSphere Team*