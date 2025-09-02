package com.telcobright.routesphere.configmanager.consumer;

import com.telcobright.routesphere.configmanager.service.ConfigManagerService;
import io.smallrye.reactive.messaging.kafka.api.IncomingKafkaRecordMetadata;
import io.quarkus.arc.profile.UnlessBuildProfile;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.jboss.logging.Logger;

import java.util.concurrent.CompletionStage;
import java.util.concurrent.CompletableFuture;

/**
 * Kafka consumer for configuration change notifications
 * Listens to config_event_loader topic for configuration updates
 * Note: This consumer is disabled when Kafka is not available
 */
@ApplicationScoped
public class ConfigChangeConsumer {
    
    private static final Logger LOG = Logger.getLogger(ConfigChangeConsumer.class);
    
    @Inject
    ConfigManagerService configManagerService;
    
    @ConfigProperty(name = "configmanager.kafka.enabled", defaultValue = "true")
    boolean kafkaEnabled;
    
    /**
     * Consume configuration change events from Kafka
     * Topic: config_event_loader
     * Expected message: "config reloaded"
     */
    @Incoming("config-updates")
    public CompletionStage<Void> consumeConfigUpdate(Message<String> message) {
        if (!kafkaEnabled) {
            LOG.debug("Kafka consumer disabled, ignoring message");
            return message.ack();
        }
        
        try {
            String payload = message.getPayload();
            LOG.infof("Received config update notification: %s", payload);
            
            // Get Kafka metadata if needed
            message.getMetadata(IncomingKafkaRecordMetadata.class)
                .ifPresent(metadata -> {
                    LOG.debugf("Received from partition %d with offset %d", 
                        metadata.getPartition(), metadata.getOffset());
                });
            
            // Reload configuration
            if ("config reloaded".equals(payload)) {
                LOG.info("Reloading configuration from ConfigManager...");
                configManagerService.reloadConfiguration();
            }
            
            // Acknowledge the message
            return message.ack();
            
        } catch (Exception e) {
            LOG.errorf("Error processing config update: %s", e.getMessage());
            // Negative acknowledgment to retry
            return message.nack(e);
        }
    }
    
    /**
     * Alternative consumer for Debezium CDC events
     * Topic: all-mysql-changes
     * This can be used to directly listen to database changes
     */
    @Incoming("db-changes")
    public CompletionStage<Void> consumeDatabaseChanges(Message<String> message) {
        if (!kafkaEnabled) {
            return message.ack();
        }
        
        try {
            String payload = message.getPayload();
            LOG.debugf("Received database change event: %s", payload);
            
            // Parse and process Debezium event if needed
            // For now, we'll rely on the config_event_loader topic
            
            return message.ack();
            
        } catch (Exception e) {
            LOG.errorf("Error processing database change: %s", e.getMessage());
            return message.nack(e);
        }
    }
}