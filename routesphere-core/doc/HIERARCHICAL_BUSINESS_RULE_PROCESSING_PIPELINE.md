# Hierarchical Business Rule Processing Pipeline

## Overview

RouteSphere implements a hierarchical business rule processing system where rules are evaluated across a multi-level tenant hierarchy. Each tenant level (root, reseller, sub-reseller) can have its own set of business rules that are evaluated sequentially, with the ability to abort the entire pipeline at any level.

## Architecture Principles

### 1. Single Responsibility Rules
Each business rule implementation focuses on exactly ONE aspect of validation/processing:
- Credit check
- Rate limiting
- Blacklist validation
- Time restrictions
- Destination control
- Minimum duration validation

### 2. Protocol-Specific Organization
Rules are organized by protocol to handle protocol-specific requirements:
```
rules/
├── common/           # Protocol-agnostic rules
├── http/            # HTTP-specific rules
├── sip/             # SIP-specific rules
├── esl/             # ESL/FreeSWITCH-specific rules
└── kafka/           # Kafka-specific rules
```

### 3. Hierarchical Processing
Rules are processed in tenant hierarchy order:
1. Root tenant (admin, level 0)
2. Level 1 reseller
3. Level 2 sub-reseller
4. Level N (theoretically unlimited, practically ~3 levels)

## Core Interfaces

### Business Rule Interface
```java
package com.telcobright.routesphere.rules;

public interface BizRule {
    /**
     * Unique identifier for this rule type
     */
    String getRuleId();

    /**
     * Execute the rule against the context
     * @param context The pipeline context containing request data
     * @param config Rule-specific configuration parameters
     * @return RuleResult indicating CONTINUE or ABORT with reason
     */
    RuleResult execute(PipelineContext context, RuleConfig config);

    /**
     * Validate that the configuration is valid for this rule
     */
    boolean validateConfig(RuleConfig config);

    /**
     * Get the rule description
     */
    String getDescription();
}
```

### Rule Result
```java
public class RuleResult {
    private final ResultType type;     // CONTINUE, ABORT
    private final String reason;        // Abort reason if applicable
    private final Map<String, Object> metadata;  // Rule execution metadata

    public static RuleResult continueExecution() {
        return new RuleResult(ResultType.CONTINUE, null, null);
    }

    public static RuleResult abort(String reason) {
        return new RuleResult(ResultType.ABORT, reason, null);
    }

    public static RuleResult continueWithData(Map<String, Object> data) {
        return new RuleResult(ResultType.CONTINUE, null, data);
    }
}
```

### Rule Configuration
```java
public class RuleConfig {
    private final Map<String, Object> parameters;

    public <T> T get(String key, Class<T> type) {
        return type.cast(parameters.get(key));
    }

    public <T> T get(String key, T defaultValue) {
        return (T) parameters.getOrDefault(key, defaultValue);
    }

    public boolean hasParameter(String key) {
        return parameters.containsKey(key);
    }
}
```

## Rule Implementation Examples

### Common Rules

#### Credit Check Rule
```java
package com.telcobright.routesphere.rules.common;

@ApplicationScoped
@RuleType("credit_check")
public class CreditCheckRule implements BizRule {

    @Inject
    CreditService creditService;

    @Override
    public String getRuleId() {
        return "credit_check";
    }

    @Override
    public RuleResult execute(PipelineContext context, RuleConfig config) {
        String tenantId = context.getTenantId();
        BigDecimal minCredit = config.get("minCredit", BigDecimal.class);
        BigDecimal reserveAmount = config.get("reserveAmount", BigDecimal.ZERO);

        BigDecimal availableCredit = creditService.getAvailableCredit(tenantId);

        if (availableCredit.compareTo(minCredit) < 0) {
            return RuleResult.abort("INSUFFICIENT_CREDIT");
        }

        if (reserveAmount.compareTo(BigDecimal.ZERO) > 0) {
            creditService.reserve(tenantId, reserveAmount);
            context.setData("creditReserved", reserveAmount);
        }

        return RuleResult.continueWithData(Map.of(
            "availableCredit", availableCredit,
            "creditReserved", reserveAmount
        ));
    }

    @Override
    public boolean validateConfig(RuleConfig config) {
        return config.hasParameter("minCredit");
    }

    @Override
    public String getDescription() {
        return "Validates tenant has sufficient credit balance";
    }
}
```

#### Rate Limit Rule
```java
package com.telcobright.routesphere.rules.common;

@ApplicationScoped
@RuleType("rate_limit")
public class RateLimitRule implements BizRule {

    @Inject
    RateLimiter rateLimiter;

    @Override
    public String getRuleId() {
        return "rate_limit";
    }

    @Override
    public RuleResult execute(PipelineContext context, RuleConfig config) {
        String tenantId = context.getTenantId();
        int maxRequests = config.get("maxRequests", Integer.class);
        int timeWindowSeconds = config.get("timeWindowSeconds", 60);

        boolean allowed = rateLimiter.tryAcquire(
            tenantId,
            maxRequests,
            timeWindowSeconds
        );

        if (!allowed) {
            return RuleResult.abort("RATE_LIMIT_EXCEEDED");
        }

        return RuleResult.continueExecution();
    }
}
```

### Protocol-Specific Rules

#### SIP Minimum Duration Rule
```java
package com.telcobright.routesphere.rules.sip;

@ApplicationScoped
@RuleType("sip_min_duration")
public class SipMinimumDurationRule implements BizRule {

    @Override
    public String getRuleId() {
        return "sip_min_duration";
    }

    @Override
    public RuleResult execute(PipelineContext context, RuleConfig config) {
        double minDurationSeconds = config.get("minDurationSeconds", 0.1);

        // For SIP, we set this for CDR processing later
        context.setData("billing.minDuration", minDurationSeconds);
        context.setData("billing.increment", config.get("increment", 1));

        return RuleResult.continueExecution();
    }
}
```

#### HTTP API Key Validation Rule
```java
package com.telcobright.routesphere.rules.http;

@ApplicationScoped
@RuleType("http_api_key")
public class HttpApiKeyRule implements BizRule {

    @Inject
    ApiKeyService apiKeyService;

    @Override
    public RuleResult execute(PipelineContext context, RuleConfig config) {
        String apiKey = context.getHeader("X-API-Key");
        boolean requireApiKey = config.get("required", true);

        if (requireApiKey && (apiKey == null || !apiKeyService.isValid(apiKey))) {
            return RuleResult.abort("INVALID_API_KEY");
        }

        return RuleResult.continueExecution();
    }
}
```

## Tenant Hierarchy Configuration

### Configuration Structure
```yaml
# Root tenant configuration
tenants:
  root:
    id: "telcobright"
    name: "TelcoBright Root"
    level: 0
    database: "jdbc:mysql://127.0.0.1:3306/telcobright_db"

    # Default ruleset for root tenant
    rulesets:
      default:
        - rule: credit_check
          config:
            minCredit: 100.00
            reserveAmount: 10.00

        - rule: rate_limit
          config:
            maxRequests: 1000
            timeWindowSeconds: 60

        - rule: blacklist_check
          config:
            listName: "global_blacklist"

        - rule: time_restriction
          config:
            allowedHours: "00:00-23:59"
            timezone: "UTC"

      sip_specific:
        - rule: sip_min_duration
          config:
            minDurationSeconds: 0.1
            increment: 6

        - rule: sip_destination_check
          config:
            blockedPrefixes: ["1900", "1976"]

    # Reseller configurations
    resellers:
      # Default configuration for all resellers (unless overridden)
      default:
        level: 1
        rulesets:
          default:
            - rule: credit_check
              config:
                minCredit: 50.00
                reserveAmount: 5.00

            - rule: rate_limit
              config:
                maxRequests: 100
                timeWindowSeconds: 60

      # Level-specific overrides
      level_overrides:
        # Level 1 resellers
        1:
          rulesets:
            default:
              - rule: rate_limit
                config:
                  maxRequests: 500
                  timeWindowSeconds: 60

        # Level 2 sub-resellers
        2:
          rulesets:
            default:
              - rule: rate_limit
                config:
                  maxRequests: 200
                  timeWindowSeconds: 60

              - rule: destination_restriction
                config:
                  allowedCountries: ["US", "CA", "GB"]

      # Specific reseller overrides (by ID)
      specific:
        - id: "reseller_premium_001"
          level: 1
          rulesets:
            default:
              - rule: credit_check
                config:
                  minCredit: 10.00  # Lower threshold for premium

              - rule: rate_limit
                config:
                  maxRequests: 5000  # Higher limit for premium
                  timeWindowSeconds: 60

        - id: "reseller_restricted_002"
          level: 1
          rulesets:
            default:
              - rule: time_restriction
                config:
                  allowedHours: "09:00-17:00"
                  timezone: "EST"

              - rule: destination_restriction
                config:
                  allowedCountries: ["US"]
```

## Hierarchical Processing Logic

### Processing Flow
```
1. Extract tenant hierarchy from request
2. Start with root tenant (level 0)
3. For each level in hierarchy:
   a. Load ruleset for current tenant
   b. Apply level-specific overrides
   c. Apply tenant-specific overrides
   d. Execute each rule in sequence
   e. If any rule returns ABORT, stop processing
   f. Accumulate rule results in context
4. Continue to next level (child tenant)
5. Return final result
```

### Implementation
```java
@ApplicationScoped
public class HierarchicalRuleProcessor {

    @Inject
    TenantService tenantService;

    @Inject
    RuleRegistry ruleRegistry;

    @Inject
    RulesetConfigLoader configLoader;

    public PipelineResult process(PipelineContext context) {
        // 1. Identify tenant hierarchy
        TenantHierarchy hierarchy = tenantService.getHierarchy(
            context.getTenantId()
        );

        // 2. Process each level
        for (TenantNode node : hierarchy.getNodes()) {
            RulesetConfig ruleset = loadRuleset(node);

            // 3. Execute rules at this level
            for (RuleDefinition ruleDef : ruleset.getRules()) {
                BizRule rule = ruleRegistry.getRule(ruleDef.getRuleId());
                RuleConfig config = ruleDef.getConfig();

                // 4. Execute rule
                RuleResult result = rule.execute(context, config);

                // 5. Store result in context
                context.accumulate(
                    node.getLevel(),
                    node.getId(),
                    ruleDef.getRuleId(),
                    result
                );

                // 6. Check for abort
                if (result.isAbort()) {
                    return PipelineResult.abort(
                        node.getLevel(),
                        node.getId(),
                        result.getReason()
                    );
                }
            }
        }

        return PipelineResult.success(context);
    }

    private RulesetConfig loadRuleset(TenantNode node) {
        // 1. Start with default ruleset for level
        RulesetConfig ruleset = configLoader.getDefaultForLevel(node.getLevel());

        // 2. Apply level-specific overrides
        RulesetConfig levelOverride = configLoader.getLevelOverride(node.getLevel());
        if (levelOverride != null) {
            ruleset = ruleset.merge(levelOverride);
        }

        // 3. Apply tenant-specific overrides
        RulesetConfig tenantOverride = configLoader.getTenantOverride(node.getId());
        if (tenantOverride != null) {
            ruleset = ruleset.merge(tenantOverride);
        }

        return ruleset;
    }
}
```

## Rule Discovery and Registration

### Automatic Rule Discovery
```java
@ApplicationScoped
public class RuleRegistry {

    private final Map<String, BizRule> rules = new HashMap<>();

    @Inject
    Instance<BizRule> ruleInstances;

    @PostConstruct
    void init() {
        // Auto-discover all rule implementations
        for (BizRule rule : ruleInstances) {
            RuleType annotation = rule.getClass().getAnnotation(RuleType.class);
            if (annotation != null) {
                register(annotation.value(), rule);
            }
        }

        LOG.info("Registered {} business rules", rules.size());
    }

    public void register(String ruleId, BizRule rule) {
        rules.put(ruleId, rule);
    }

    public BizRule getRule(String ruleId) {
        BizRule rule = rules.get(ruleId);
        if (rule == null) {
            throw new RuleNotFoundException(ruleId);
        }
        return rule;
    }

    public Set<String> getAvailableRules() {
        return rules.keySet();
    }
}
```

## Protocol Response Mapping

### Response Code Resolution
```java
@ApplicationScoped
public class ProtocolResponseResolver {

    public Object resolveResponse(String protocol, String abortReason,
                                 int abortLevel, String abortTenant) {
        switch (protocol.toLowerCase()) {
            case "http":
                return HttpResponseCode.fromAbortReason(abortReason);

            case "sip":
                return SipCauseCode.fromAbortReason(abortReason);

            case "esl":
                return EslCauseCode.fromAbortReason(abortReason);

            default:
                return GenericResponseCode.PROCESSING_ERROR;
        }
    }
}
```

## Testing Strategy

### Unit Testing Rules
```java
@Test
public void testCreditCheckRule() {
    // Given
    CreditCheckRule rule = new CreditCheckRule();
    PipelineContext context = new PipelineContext();
    context.setTenantId("test_tenant");

    RuleConfig config = RuleConfig.builder()
        .parameter("minCredit", new BigDecimal("100.00"))
        .parameter("reserveAmount", new BigDecimal("10.00"))
        .build();

    // When
    RuleResult result = rule.execute(context, config);

    // Then
    assertThat(result.getType()).isEqualTo(ResultType.CONTINUE);
    assertThat(context.getData("creditReserved")).isEqualTo(new BigDecimal("10.00"));
}
```

### Integration Testing Hierarchy
```java
@Test
public void testHierarchicalProcessing() {
    // Given
    TenantHierarchy hierarchy = TenantHierarchy.builder()
        .root("telcobright")
        .addReseller("reseller1", 1)
        .addSubReseller("subreseller1", 2)
        .build();

    PipelineContext context = new PipelineContext();
    context.setTenantHierarchy(hierarchy);

    // When
    PipelineResult result = processor.process(context);

    // Then
    assertThat(result.isSuccess()).isTrue();
    assertThat(context.getAccumulatedRules()).hasSize(3); // 3 levels
}
```

## Performance Considerations

### Rule Caching
```java
@ApplicationScoped
public class RulesetCache {

    @Inject
    @ConfigProperty(name = "rules.cache.ttl", defaultValue = "300")
    int cacheTtlSeconds;

    private final Cache<String, RulesetConfig> cache =
        Caffeine.newBuilder()
            .expireAfterWrite(cacheTtlSeconds, TimeUnit.SECONDS)
            .build();

    public RulesetConfig get(String tenantId) {
        return cache.get(tenantId, this::loadFromConfig);
    }
}
```

### Parallel Rule Execution
```java
@ApplicationScoped
public class ParallelRuleExecutor {

    @Inject
    ManagedExecutor executor;

    public CompletableFuture<List<RuleResult>> executeParallel(
            List<RuleDefinition> rules,
            PipelineContext context) {

        List<CompletableFuture<RuleResult>> futures = rules.stream()
            .map(rule -> CompletableFuture.supplyAsync(
                () -> executeRule(rule, context),
                executor
            ))
            .collect(Collectors.toList());

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
            .thenApply(v -> futures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList()));
    }
}
```

## Monitoring and Observability

### Rule Metrics
```java
@ApplicationScoped
public class RuleMetrics {

    @Inject
    MeterRegistry registry;

    public void recordRuleExecution(String ruleId, String tenantId,
                                  int level, long duration, boolean success) {
        registry.counter("rule.executions",
            "rule", ruleId,
            "tenant", tenantId,
            "level", String.valueOf(level),
            "status", success ? "success" : "failure"
        ).increment();

        registry.timer("rule.duration", "rule", ruleId)
            .record(duration, TimeUnit.MILLISECONDS);
    }
}
```

## Best Practices

### 1. Rule Design
- Keep rules focused on single responsibility
- Make rules stateless
- Use dependency injection for external services
- Validate configuration in `validateConfig()`
- Return meaningful abort reasons

### 2. Configuration Management
- Use version control for rule configurations
- Document all configuration parameters
- Provide sensible defaults
- Validate configurations at startup

### 3. Error Handling
- Always provide specific abort reasons
- Log rule failures with context
- Monitor rule performance
- Implement circuit breakers for external calls

### 4. Testing
- Unit test each rule independently
- Integration test rule combinations
- Test hierarchy traversal
- Performance test with realistic loads

---

*Document Version: 1.0*
*Date: 2025-09-17*
*Author: RouteSphere Team*