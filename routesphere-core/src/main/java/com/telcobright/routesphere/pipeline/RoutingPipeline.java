package com.telcobright.routesphere.pipeline;

import com.telcobright.routesphere.pipeline.processors.*;
import com.telcobright.routesphere.tenant.TenantHierarchy;
import java.util.ArrayList;
import java.util.List;

/**
 * Main routing pipeline that processes requests through various stages
 */
public class RoutingPipeline {
    private final List<PipelineProcessor> processors;
    private final TenantHierarchy tenantHierarchy;
    
    public RoutingPipeline(TenantHierarchy tenantHierarchy) {
        this.tenantHierarchy = tenantHierarchy;
        this.processors = new ArrayList<>();
        initializeDefaultProcessors();
    }
    
    /**
     * Initialize default processors in order
     */
    private void initializeDefaultProcessors() {
        // These will be implemented as separate classes
        // processors.add(new TenantIdentificationProcessor(tenantHierarchy));
        // processors.add(new AdmissionAuthenticationProcessor());
        // processors.add(new AdmissionAuthorizationProcessor());
        // processors.add(new BusinessRulesProcessor());
        // processors.add(new RoutingDecisionProcessor());
        // processors.add(new ActionExecutionProcessor());
        
        // Sort by order
        processors.sort((p1, p2) -> Integer.compare(p1.getOrder(), p2.getOrder()));
    }
    
    /**
     * Process a routing request through the pipeline
     */
    public RoutingResponse process(RoutingRequest request) {
        RoutingContext context = new RoutingContext(request);
        
        try {
            // Process through each processor
            for (PipelineProcessor processor : processors) {
                System.out.println("Processing: " + processor.getName() + 
                    " for request: " + request.getRequestId());
                
                boolean continueProcessing = processor.process(context);
                
                if (!continueProcessing) {
                    System.out.println("Pipeline stopped at: " + processor.getName());
                    break;
                }
            }
            
            context.moveToStage(RoutingContext.PipelineStage.COMPLETED);
            
        } catch (Exception e) {
            context.moveToStage(RoutingContext.PipelineStage.FAILED);
            context.getResponse()
                .withType(RoutingResponse.ResponseType.ERROR)
                .withStatus(500, "Pipeline processing error: " + e.getMessage());
        }
        
        return context.getResponse();
    }
    
    /**
     * Add a custom processor to the pipeline
     */
    public void addProcessor(PipelineProcessor processor) {
        processors.add(processor);
        processors.sort((p1, p2) -> Integer.compare(p1.getOrder(), p2.getOrder()));
    }
    
    /**
     * Remove a processor by name
     */
    public void removeProcessor(String processorName) {
        processors.removeIf(p -> p.getName().equals(processorName));
    }
    
    /**
     * Get all processors
     */
    public List<PipelineProcessor> getProcessors() {
        return new ArrayList<>(processors);
    }
}