package com.telcobright.routesphere.config.deployment;

import io.quarkus.runtime.Startup;
import io.quarkus.runtime.StartupEvent;
import io.smallrye.config.ConfigMapping;
import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Service to manage deployment configurations
 * Loads tenant configurations from Quarkus config system
 */
@ApplicationScoped
@Startup
public class DeploymentConfigService {
    
    @ConfigProperty(name = "routesphere.tenant.id")
    Optional<String> tenantId;

    @ConfigProperty(name = "routesphere.tenant.name")
    Optional<String> tenantName;
    
    @ConfigProperty(name = "routesphere.tenant.active-profile", defaultValue = "dev")
    String activeProfile;
    
    @ConfigProperty(name = "routesphere.tenant.profiles.${routesphere.tenant.active-profile}.general.database.url")
    Optional<String> databaseUrl;
    
    @ConfigProperty(name = "routesphere.tenant.profiles.${routesphere.tenant.active-profile}.general.database.username")
    Optional<String> databaseUsername;
    
    @ConfigProperty(name = "routesphere.tenant.profiles.${routesphere.tenant.active-profile}.general.database.password")
    Optional<String> databasePassword;
    
    @ConfigProperty(name = "routesphere.tenant.profiles.${routesphere.tenant.active-profile}.general.config-manager.base-url")
    Optional<String> configManagerUrl;
    
    @ConfigProperty(name = "routesphere.tenant.profiles.${routesphere.tenant.active-profile}.general.kafka.bootstrap-servers")
    Optional<String> kafkaBootstrapServers;
    
    private Map<String, SocketProfileConfig> activeSocketProfiles = new HashMap<>();
    
    /**
     * Initialize deployment configuration on startup
     */
    void onStart(@Observes @Priority(2) StartupEvent event) {
        System.out.println("\n========================================");
        System.out.println(" Loading Deployment Configuration");
        System.out.println("========================================\n");
        
        System.out.println("Deployment Config:");
        System.out.println("  Tenant ID: " + tenantId.orElse("not configured"));
        System.out.println("  Tenant Name: " + tenantName.orElse("not configured"));
        System.out.println("  Config Profile: " + activeProfile);
        
        // Log database configuration
        if (databaseUrl.isPresent()) {
            System.out.println("\nDatabase Configuration:");
            System.out.println("  URL: " + databaseUrl.get());
            System.out.println("  Username: " + databaseUsername.orElse("not configured"));
        }
        
        // Log ConfigManager configuration
        if (configManagerUrl.isPresent()) {
            System.out.println("\nConfigManager Configuration:");
            System.out.println("  Base URL: " + configManagerUrl.get());
        }
        
        // Log Kafka configuration
        if (kafkaBootstrapServers.isPresent()) {
            System.out.println("\nKafka Configuration:");
            System.out.println("  Bootstrap Servers: " + kafkaBootstrapServers.get());
        }
        
        System.out.println("\n========================================");
        System.out.println(" Deployment Configuration Loaded");
        System.out.println("========================================\n");
    }
    
    /**
     * Get database configuration for current profile
     */
    public DatabaseConfig getDatabaseConfig() {
        return new DatabaseConfig(
            databaseUrl.orElse("jdbc:h2:mem:test"),
            databaseUsername.orElse("sa"),
            databasePassword.orElse("")
        );
    }
    
    /**
     * Get ConfigManager URL from configuration
     */
    public String getConfigManagerUrl() {
        return configManagerUrl.orElse("http://localhost:7070");
    }
    
    /**
     * Get Kafka bootstrap servers
     */
    public String getKafkaBootstrapServers() {
        return kafkaBootstrapServers.orElse("localhost:9092");
    }
    
    /**
     * Get active profile name
     */
    public String getActiveProfile() {
        return activeProfile;
    }
    
    /**
     * Check if a specific profile is active
     */
    public boolean isProfileActive(String profile) {
        return activeProfile.equals(profile);
    }
    
    /**
     * Simple database configuration holder
     */
    public static class DatabaseConfig {
        private final String url;
        private final String username;
        private final String password;
        
        public DatabaseConfig(String url, String username, String password) {
            this.url = url;
            this.username = username;
            this.password = password;
        }
        
        public String getUrl() { return url; }
        public String getUsername() { return username; }
        public String getPassword() { return password; }
    }
}