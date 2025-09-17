# RouteSphere Pipeline Processing Architecture

## Overview

RouteSphere implements a pipeline processing pattern where incoming requests from various protocols (HTTP, ESL, SIP, Kafka) are processed through a series of configurable stages. Each stage can transform data, apply business rules, accumulate results, and decide whether to continue or abort the pipeline.

## Core Concepts

### 1. Pipeline
A **Pipeline** is an ordered sequence of processing stages that handle requests from channels. Each pipeline:
- Has a unique name and configuration
- Can be synchronous or asynchronous
- Is protocol-agnostic
- Can be shared across multiple channels

### 2. Pipeline Context
The **PipelineContext** carries data through all stages:
```java
public class PipelineContext {
    private Map<String, Object> data;          // Request data and accumulated results
    private PipelineResult result;             // Final or intermediate results
    private boolean shouldContinue = true;     // Flow control flag
    private String abortReason;                // Why pipeline was aborted
    private Map<String, Object> metadata;      // Request metadata (headers, etc.)
}
```

### 3. Pipeline Stages
Each **Stage** is a processing unit that:
- Receives a PipelineContext
- Performs specific business logic
- Can modify the context
- Returns a StageResult (CONTINUE, ABORT, BRANCH)

## Architecture

```
┌─────────────┐     ┌─────────────┐     ┌─────────────┐
│   Channel   │────▶│   Pipeline  │────▶│   Response  │
│  (HTTP/ESL) │     │   Manager   │     │   Handler   │
└─────────────┘     └─────────────┘     └─────────────┘
                            │
                    ┌───────▼────────┐
                    │ Pipeline Queue │
                    └───────┬────────┘
                            │
        ┌───────────────────┼───────────────────┐
        ▼                   ▼                   ▼
┌──────────────┐   ┌──────────────┐   ┌──────────────┐
│   Stage 1    │──▶│   Stage 2    │──▶│   Stage N    │
│ (Validation) │   │ (Enrichment) │   │  (Response)  │
└──────────────┘   └──────────────┘   └──────────────┘
```

## Pipeline Processing Flow

### 1. Request Entry
```java
// Channel receives request and creates context
PipelineContext context = new PipelineContext();
context.setData("request", incomingRequest);
context.setMetadata("channel", channelName);
context.setMetadata("protocol", "http");

// Submit to pipeline
pipelineManager.process("customer-pipeline", context);
```

### 2. Stage Execution
```java
public class ValidationStage implements PipelineStage {

    @Override
    public StageResult execute(PipelineContext context) {
        Request request = context.getData("request");

        // Validate request
        if (!isValid(request)) {
            context.setAbortReason("Invalid request format");
            return StageResult.ABORT;
        }

        // Add validation result to context
        context.setData("validated", true);
        context.setData("validationTime", Instant.now());

        return StageResult.CONTINUE;
    }
}
```

### 3. Conditional Processing
```java
public class RoutingStage implements PipelineStage {

    @Override
    public StageResult execute(PipelineContext context) {
        String customerType = context.getData("customerType");

        switch (customerType) {
            case "PREMIUM":
                return StageResult.branch("premium-pipeline");
            case "REGULAR":
                return StageResult.branch("regular-pipeline");
            default:
                return StageResult.CONTINUE;
        }
    }
}
```

## Pipeline Configuration

### YAML Configuration
```yaml
pipelines:
  - name: call-processing-pipeline
    async: false
    timeout: 30s
    stages:
      - name: authentication
        class: com.telcobright.pipeline.AuthenticationStage
        config:
          required: true
          method: jwt

      - name: validation
        class: com.telcobright.pipeline.ValidationStage
        config:
          strict: true

      - name: enrichment
        class: com.telcobright.pipeline.EnrichmentStage
        config:
          sources:
            - customer-db
            - billing-system

      - name: business-rules
        class: com.telcobright.pipeline.BusinessRulesStage
        config:
          rules:
            - max-call-duration: 3600
            - blocked-destinations: ["premium-numbers"]

      - name: routing
        class: com.telcobright.pipeline.RoutingStage
        config:
          strategy: least-cost

      - name: transformation
        class: com.telcobright.pipeline.TransformationStage
        config:
          format: sip-invite

      - name: response
        class: com.telcobright.pipeline.ResponseStage
        config:
          format: json
```

### Programmatic Configuration
```java
@ApplicationScoped
public class PipelineConfiguration {

    @Produces
    @Named("call-pipeline")
    public Pipeline createCallPipeline() {
        return Pipeline.builder()
            .name("call-pipeline")
            .addStage(new AuthenticationStage())
            .addStage(new ValidationStage())
            .addStage(new EnrichmentStage())
            .addStage(new BusinessRulesStage())
            .addStage(new RoutingStage())
            .addStage(new ResponseStage())
            .build();
    }
}
```

## Stage Types

### 1. Validation Stages
- **Purpose**: Ensure request meets requirements
- **Examples**: Schema validation, authentication, authorization
- **Typical Actions**: CONTINUE if valid, ABORT if invalid

### 2. Enrichment Stages
- **Purpose**: Add additional data to context
- **Examples**: Database lookups, external API calls, cache retrieval
- **Typical Actions**: Always CONTINUE (unless error)

### 3. Business Rules Stages
- **Purpose**: Apply business logic and policies
- **Examples**: Credit checks, rate limiting, fraud detection
- **Typical Actions**: CONTINUE, ABORT, or BRANCH based on rules

### 4. Transformation Stages
- **Purpose**: Convert data format
- **Examples**: JSON to XML, protocol translation
- **Typical Actions**: Always CONTINUE

### 5. Routing Stages
- **Purpose**: Determine next destination
- **Examples**: Load balancing, A/B testing, conditional routing
- **Typical Actions**: CONTINUE or BRANCH

### 6. Response Stages
- **Purpose**: Format and send response
- **Examples**: HTTP response, ESL event, SIP message
- **Typical Actions**: Always CONTINUE (terminal stage)

## Error Handling

### Stage-Level Error Handling
```java
public class SafeStage implements PipelineStage {

    @Override
    public StageResult execute(PipelineContext context) {
        try {
            // Stage logic
            return StageResult.CONTINUE;
        } catch (RecoverableException e) {
            context.setData("error", e.getMessage());
            return StageResult.CONTINUE; // Continue with error noted
        } catch (CriticalException e) {
            context.setAbortReason(e.getMessage());
            return StageResult.ABORT; // Stop pipeline
        }
    }
}
```

### Pipeline-Level Error Handling
```java
@ApplicationScoped
public class PipelineManager {

    public void process(String pipelineName, PipelineContext context) {
        try {
            Pipeline pipeline = getPipeline(pipelineName);

            for (PipelineStage stage : pipeline.getStages()) {
                StageResult result = stage.execute(context);

                if (result == StageResult.ABORT) {
                    handleAbort(context);
                    return;
                }

                if (result.isBranch()) {
                    process(result.getBranchPipeline(), context);
                    return;
                }
            }

            handleSuccess(context);

        } catch (Exception e) {
            handleError(context, e);
        }
    }
}
```

## Async Pipeline Processing

### Configuration
```yaml
pipeline:
  name: async-pipeline
  async: true
  executor: dedicated-pool
  max-concurrent: 100
```

### Implementation
```java
@ApplicationScoped
public class AsyncPipelineManager {

    @Inject
    ManagedExecutor executor;

    public CompletionStage<PipelineResult> processAsync(
            String pipeline, PipelineContext context) {

        return executor.supplyAsync(() -> {
            // Process pipeline in separate thread
            return processPipeline(pipeline, context);
        }).exceptionally(throwable -> {
            // Handle async errors
            return handleError(context, throwable);
        });
    }
}
```

## Monitoring and Metrics

### Pipeline Metrics
```java
@ApplicationScoped
public class PipelineMetrics {

    @Inject
    MeterRegistry registry;

    public void recordExecution(String pipeline, long duration, boolean success) {
        registry.counter("pipeline.executions",
            "pipeline", pipeline,
            "status", success ? "success" : "failure"
        ).increment();

        registry.timer("pipeline.duration", "pipeline", pipeline)
            .record(duration, TimeUnit.MILLISECONDS);
    }
}
```

### Stage Metrics
```java
public class MetricStage implements PipelineStage {

    @Override
    public StageResult execute(PipelineContext context) {
        long start = System.currentTimeMillis();

        try {
            // Stage logic
            return StageResult.CONTINUE;
        } finally {
            long duration = System.currentTimeMillis() - start;
            context.setData("stage.duration." + getName(), duration);
        }
    }
}
```

## Advanced Features

### 1. Parallel Stage Execution
```java
public class ParallelStage implements PipelineStage {

    private List<PipelineStage> parallelStages;

    @Override
    public StageResult execute(PipelineContext context) {
        List<CompletableFuture<StageResult>> futures =
            parallelStages.stream()
                .map(stage -> CompletableFuture.supplyAsync(
                    () -> stage.execute(context.clone())
                ))
                .collect(Collectors.toList());

        // Wait for all to complete
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        // Merge results
        return mergeResults(futures);
    }
}
```

### 2. Conditional Stage Execution
```java
public class ConditionalStage implements PipelineStage {

    private Predicate<PipelineContext> condition;
    private PipelineStage stage;

    @Override
    public StageResult execute(PipelineContext context) {
        if (condition.test(context)) {
            return stage.execute(context);
        }
        return StageResult.CONTINUE;
    }
}
```

### 3. Retry Logic
```java
public class RetryableStage implements PipelineStage {

    private int maxRetries = 3;
    private long retryDelay = 1000;

    @Override
    public StageResult execute(PipelineContext context) {
        int attempts = 0;
        Exception lastException = null;

        while (attempts < maxRetries) {
            try {
                return doExecute(context);
            } catch (RetryableException e) {
                lastException = e;
                attempts++;
                sleep(retryDelay * attempts);
            }
        }

        context.setAbortReason("Max retries exceeded: " + lastException.getMessage());
        return StageResult.ABORT;
    }
}
```

### 4. Circuit Breaker
```java
public class CircuitBreakerStage implements PipelineStage {

    private CircuitBreaker circuitBreaker;

    @Override
    public StageResult execute(PipelineContext context) {
        return circuitBreaker.executeSupplier(() -> {
            // Stage logic that might fail
            return doExecute(context);
        }).recover(throwable -> {
            context.setData("circuitBreaker.open", true);
            return StageResult.ABORT;
        }).get();
    }
}
```

## Integration with Channels

### HTTP Channel Integration
```java
@Path("/api")
public class HttpEndpoint {

    @Inject
    PipelineManager pipelineManager;

    @POST
    @Path("/process")
    public Response process(Request request) {
        PipelineContext context = new PipelineContext();
        context.setData("request", request);
        context.setMetadata("protocol", "http");

        PipelineResult result = pipelineManager.process("http-pipeline", context);

        if (result.isSuccess()) {
            return Response.ok(result.getData()).build();
        } else {
            return Response.status(500).entity(result.getError()).build();
        }
    }
}
```

### ESL Channel Integration
```java
public class EslEventHandler {

    @Inject
    PipelineManager pipelineManager;

    public void handleEvent(EslEvent event) {
        PipelineContext context = new PipelineContext();
        context.setData("event", event);
        context.setMetadata("protocol", "esl");
        context.setMetadata("channelId", event.getChannelId());

        // Process asynchronously for ESL events
        pipelineManager.processAsync("esl-pipeline", context)
            .thenAccept(result -> sendResponse(event, result));
    }
}
```

## Best Practices

### 1. Stage Design
- Keep stages focused on single responsibility
- Make stages stateless and reusable
- Use dependency injection for external services
- Implement proper error handling

### 2. Context Management
- Don't store large objects in context
- Use consistent naming for context keys
- Clean sensitive data before logging
- Consider context size for async processing

### 3. Performance
- Profile pipeline execution time
- Use async processing for I/O operations
- Implement caching where appropriate
- Monitor memory usage in context

### 4. Testing
```java
@Test
public void testPipeline() {
    PipelineContext context = new PipelineContext();
    context.setData("request", testRequest);

    PipelineResult result = pipelineManager.process("test-pipeline", context);

    assertThat(result.isSuccess()).isTrue();
    assertThat(context.getData("validated")).isEqualTo(true);
}
```

## Configuration Examples

### Call Processing Pipeline
```yaml
pipeline:
  name: call-processing
  stages:
    - authentication
    - number-validation
    - rate-lookup
    - fraud-check
    - routing-decision
    - billing-reservation
    - call-setup
```

### REST API Pipeline
```yaml
pipeline:
  name: rest-api
  stages:
    - cors-check
    - authentication
    - rate-limiting
    - request-validation
    - business-logic
    - response-transformation
    - logging
```

### Event Processing Pipeline
```yaml
pipeline:
  name: event-processing
  async: true
  stages:
    - event-parsing
    - event-enrichment
    - event-filtering
    - event-routing
    - event-persistence
    - notification
```

---

*Document Version: 1.0*
*Date: 2025-09-17*
*Author: RouteSphere Team*