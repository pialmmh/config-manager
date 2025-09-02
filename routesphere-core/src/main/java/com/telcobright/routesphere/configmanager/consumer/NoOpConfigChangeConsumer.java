package com.telcobright.routesphere.configmanager.consumer;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.jboss.logging.Logger;

import java.util.concurrent.CompletionStage;

/**
 * No-op consumer for when Kafka is not available
 * Prevents application startup failure due to missing Kafka broker
 */
@ApplicationScoped
public class NoOpConfigChangeConsumer {
    
    private static final Logger LOG = Logger.getLogger(NoOpConfigChangeConsumer.class);
    
    /**
     * No-op consumer for config-updates channel
     * This prevents errors when Kafka is not available
     */
    @Incoming("config-updates")
    public CompletionStage<Void> consumeConfigUpdate(String message) {
        // No-op - just acknowledge
        return Message.of(message).ack();
    }
    
    /**
     * No-op consumer for db-changes channel
     * This prevents errors when Kafka is not available
     */
    @Incoming("db-changes") 
    public CompletionStage<Void> consumeDatabaseChanges(String message) {
        // No-op - just acknowledge
        return Message.of(message).ack();
    }
}