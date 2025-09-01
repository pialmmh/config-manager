package com.telcobright.routesphere.pipeline;

/**
 * Interface for pipeline processors
 */
public interface PipelineProcessor {
    
    /**
     * Process the routing context through this processor
     * 
     * @param context The routing context
     * @return true if processing should continue, false to stop the pipeline
     */
    boolean process(RoutingContext context);
    
    /**
     * Get the name of this processor
     */
    String getName();
    
    /**
     * Get the order/priority of this processor (lower numbers execute first)
     */
    default int getOrder() {
        return 0;
    }
}