# RouteSphere Architecture - Comprehensive Summary

## Executive Overview

RouteSphere is a **universal request processing framework** designed for telecom and real-time communication systems. It provides a unified architecture for handling multiple protocols (HTTP, SIP, SMS, FreeSWITCH ESL) through a common processing pipeline while supporting complex multi-tenant hierarchical structures with complete data isolation.

## Core Architecture Components

### 1. Multi-Tenant Forest Architecture

#### Hierarchical Structure
The system implements a **forest-tree structure** for tenant organization:

- **Forest Level**: Collection of root tenants (e.g., ccl, brilliant, telcobright)
- **Tree Structure**: Each root tenant forms a tree with multiple levels of resellers
- **Database Isolation**: Each tenant/reseller maintains its own database with identical schemas

#### Database Naming Convention
```
Root Tenants:     ccl, brilliant, telcobright
Reseller Pattern: res_grandGrandParentId_grandParentId_parentId_partnerId
Example:          res_2_6 (reseller with partnerId=6 under parent partnerId=2)
```

#### Partner Classification
- **Partner**: General term for all clients (stored in `partner` table)
- **Reseller**: Special partner type where `Partner.type = 4`
- **Database Creation**: When `Partner.type = 4`, system creates a dedicated database for that reseller

### 2. Protocol Processing Pipeline

#### Supported Protocols
- **HTTP/HTTPS**: REST API endpoints
- **SIP** (UDP/TCP/TLS): VoIP signaling
- **SMS**: Text messaging
- **ESL**: FreeSWITCH Event Socket Layer for call control

#### Unified Processing Model
All protocols flow through a common `RoutingPipeline` with configurable processors:

```
Request → Tenant Identification → Admission Control → Business Rules →
Routing Decision → Action Execution → Response
```

### 3. Business Rules Engine

The system implements a **chain of responsibility pattern** for business rule processing:

#### BizRule Interface
- Each rule performs specific business logic
- Rules operate on a shared context (fat context pattern)
- Failure handling: Rules can abort or continue the chain

#### Core Business Rules
1. **IdentifyRootTenant**: Maps incoming requests to root tenant
2. **IdentifyPartner**: Determines which partner initiated the request
3. **Additional Rules**: Extensible framework for custom business logic

### 4. Call State Management (ESL)

#### EslCallMachine State Diagram
```
Admission → Trying → Ringing → Connected → HungUp

State Transitions:
- Admission: performAdmission() → Success/Failed
- Trying: Ring event → Ringing
- Ringing: Answer event → Connected
- Connected: Hangup/BalanceFailure → HungUp
- HungUp: finalizeRating() → generateCdr()
```

#### Key State Actions
- **Admission**: Validate caller, check permissions
- **Connected**: Start periodic balance reservation
- **HungUp**: Finalize billing, generate CDR

### 5. Prepaid Billing System

#### Package Management Architecture

##### Core Entities
- **Package**: Billing package definition (price, validity, VAT)
- **PackageItem**: Individual components (minutes, SMS, data)
- **PackagePurchase**: Purchase records with priority ordering
- **PackageAccount**: Balance tracking per item
- **PackageAccountReserve**: Real-time balance reservation
- **CDR**: Call Detail Records for billing

##### Billing Workflow

1. **Package Creation**
   - Define packages with multiple items
   - Set quantity, unit of measurement, prefix restrictions
   - Configure validity periods

2. **Package Purchase**
   - Create `PackagePurchase` record
   - Initialize `PackageAccount` balances
   - Assign priority (earliest expiry first, top-ups last)

3. **Real-time Charging**
   - Reserve balance on call/SMS initiation
   - Update reservation per minute (calls)
   - Refund unused balance on termination

4. **Caching Strategy**
   - Maintain `Map<partnerId, List<PackageAccount>>` in memory
   - Reload on purchase/top-up/expiry events
   - Use cache for all real-time operations

### 6. Tenant Resolution Workflow

#### ESL Event Processing Example

1. **Input Extraction**
   ```
   partnerId: 60
   ipAddr: 103.87.98.04
   ```

2. **Root Tenant Identification**
   ```
   IP 103.87.98.04 → Root Tenant: ccl
   ```

3. **Database Lookup**
   ```
   partnerId 60 → Database: res_2_6
   ```

4. **Tree Traversal**
   ```
   Path: ccl → res_2 → res_2_6
   ```

5. **Profile Access**
   ```
   Load TenantProfile for res_2_6
   Process request with cached data
   ```

### 7. Configuration Management

#### Environment-Based Structure
```
tenants/
└── {tenant}/
    ├── dev/
    ├── prod/
    ├── staging/
    └── mock/
        ├── profile-{env}.yml
        └── protocol-instances/
            ├── esl/esl-listener.yml
            ├── http/http-listener.yml
            └── sip/sip-listener.yml
```

#### Protocol Instance Configuration

##### ESL Listener
- Connection settings (host, port, password)
- Tenant identification patterns (channel variables)
- Event subscriptions (CHANNEL_CREATE, ANSWER, HANGUP)
- Processing pipeline configuration

##### SIP Listener
- Transport protocols (UDP, TCP, TLS)
- Profile configuration (domains, NAT handling)
- Context-based routing (public, authenticated, internal)
- Security settings (authentication, rate limiting)

##### HTTP Listener
- Server configuration (host, port, SSL)
- Tenant identification methods (headers, subdomain, path)
- Route definitions with authentication requirements
- CORS and rate limiting settings

### 8. Context-Based Routing (FreeSWITCH Pattern)

The system implements FreeSWITCH-like context concepts:

#### Context Types
- **public**: Entry point for unauthenticated requests
- **authenticated**: For authorized users/partners
- **internal**: Internal routing between extensions
- **emergency**: Priority routing for emergency services

#### Context Mapping
```yaml
tenant-identifier:
  context: ccl-prod        # Tenant context
  initial-context: public  # Entry point

routing:
  contexts:
    public:
      processors: [tenant-identification, admission-control]
    authenticated:
      processors: [authorization, business-rules, routing-decision]
```

### 9. Performance Optimizations

#### Caching Layers
1. **Tenant Forest Cache**: Complete hierarchy loaded at startup
2. **Partner Lookup Map**: `HashMap<partnerId, dbName>` for O(1) lookup
3. **Package Account Cache**: Active balances per partner
4. **TenantProfile Cache**: Pre-loaded configuration data

#### Connection Management
- HikariCP for database connection pooling
- Persistent ESL connections with reconnection logic
- HTTP/2 support for API endpoints

#### Asynchronous Processing
- Quarkus reactive extensions
- Event-driven architecture with Kafka
- Non-blocking I/O for protocol handlers

### 10. High Availability Features

#### Redundancy
- Multiple FreeSWITCH instances with load balancing
- Database replication per tenant
- Stateless service design for horizontal scaling

#### Monitoring
- Real-time metrics with Grafana
- WebSocket debugging for state machines
- Configurable logging levels per environment

## Technology Stack

### Core Technologies
- **Java 21**: Modern Java with virtual threads
- **Quarkus**: Cloud-native, container-first framework
- **Maven**: Multi-module project management
- **Spring Boot**: Microservices in RTC-Manager

### Data Layer
- **MySQL/PostgreSQL**: Per-tenant databases
- **Chronicle DB**: High-performance caching
- **Partitioned Repository**: Custom sharding solution

### Communication
- **Kafka**: Event streaming between services
- **WebSocket**: Real-time monitoring
- **gRPC**: High-performance inter-service communication

## Key Design Principles

1. **Protocol Agnostic**: Single pipeline for all protocols
2. **Tenant Isolation**: Complete data separation
3. **Horizontal Scalability**: Stateless services
4. **Real-time Processing**: In-memory caching, async operations
5. **Extensibility**: Plugin architecture for business rules
6. **Context-Based Routing**: FreeSWITCH-inspired design patterns

## Summary

RouteSphere represents a sophisticated telecom framework that successfully abstracts the complexity of multi-protocol communication into a unified processing model. Its multi-tenant forest architecture provides enterprise-grade isolation while maintaining performance through strategic caching. The system's design parallels proven telecom patterns (especially FreeSWITCH concepts) while adding modern cloud-native capabilities, making it suitable for large-scale, production telecom deployments requiring high availability, real-time billing, and complex routing logic.