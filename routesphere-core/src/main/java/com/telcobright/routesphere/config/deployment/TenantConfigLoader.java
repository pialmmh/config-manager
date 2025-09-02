package com.telcobright.routesphere.config.deployment;

import com.telcobright.routesphere.config.GlobalConfigService;
import io.quarkus.runtime.Startup;
import io.quarkus.runtime.StartupEvent;
import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

/**
 * Loads tenant-specific configurations from the filesystem
 * Supports hierarchical tenant structure with profiles
 */
@ApplicationScoped
@Startup
public class TenantConfigLoader {
    
    @Inject
    GlobalConfigService globalConfig;
    
    @ConfigProperty(name = "routesphere.config.tenants.path", defaultValue = "config/tenants")
    String tenantsConfigPath;
    
    private String activeTenant;
    private String activeProfile;
    
    private Map<String, TenantConfiguration> tenantConfigs = new HashMap<>();
    private TenantConfiguration currentTenantConfig;
    
    /**
     * Load tenant configurations on startup
     * Runs after GlobalConfigService (Priority 3)
     */
    void onStart(@Observes @Priority(3) StartupEvent event) {
        // Get active tenant and profile from global config
        activeTenant = globalConfig.getActiveTenant();
        activeProfile = globalConfig.getActiveProfile();
        System.out.println("\n========================================");
        System.out.println(" Loading Tenant Configurations");
        System.out.println("========================================\n");
        
        System.out.println("Active Tenant: " + activeTenant);
        System.out.println("Active Profile: " + activeProfile);
        System.out.println("Config Path: " + tenantsConfigPath);
        
        loadTenantConfigurations();
        
        // Set current tenant configuration
        currentTenantConfig = tenantConfigs.get(activeTenant);
        if (currentTenantConfig != null) {
            System.out.println("\nLoaded configuration for tenant: " + currentTenantConfig.getName());
            System.out.println("  Type: " + currentTenantConfig.getType());
            System.out.println("  Profiles available: " + currentTenantConfig.getProfiles().keySet());
            
            ProfileConfiguration activeProfileConfig = currentTenantConfig.getProfile(activeProfile);
            if (activeProfileConfig != null) {
                System.out.println("  Active profile: " + activeProfile);
                System.out.println("    Database: " + activeProfileConfig.getDatabaseUrl());
                System.out.println("    Kafka: " + activeProfileConfig.getKafkaServers());
                System.out.println("    Sockets: " + activeProfileConfig.getSocketCount());
            }
        }
        
        System.out.println("\n========================================");
        System.out.println(" Tenant Configurations Loaded");
        System.out.println("========================================\n");
    }
    
    /**
     * Load all tenant configurations from filesystem
     */
    private void loadTenantConfigurations() {
        try {
            // Look for tenant directories
            Path tenantsPath = Paths.get(getClass().getClassLoader()
                .getResource(tenantsConfigPath).toURI());
            
            if (Files.exists(tenantsPath)) {
                try (Stream<Path> tenantDirs = Files.list(tenantsPath)) {
                    tenantDirs.filter(Files::isDirectory)
                        .forEach(this::loadTenantConfig);
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading tenant configurations: " + e.getMessage());
            // Fall back to classpath resources
            loadFromClasspath();
        }
    }
    
    /**
     * Load configuration for a specific tenant
     */
    private void loadTenantConfig(Path tenantDir) {
        String tenantId = tenantDir.getFileName().toString();
        System.out.println("Loading configuration for tenant: " + tenantId);
        
        TenantConfiguration tenantConfig = new TenantConfiguration(tenantId);
        
        try {
            // Load tenant base configuration
            Path tenantConfigPath = tenantDir.resolve("tenant-config.yml");
            if (Files.exists(tenantConfigPath)) {
                loadYamlFile(tenantConfigPath, tenantConfig);
            }
            
            // Load profile configurations
            try (Stream<Path> profileFiles = Files.list(tenantDir)) {
                profileFiles.filter(path -> path.getFileName().toString().startsWith("profile-"))
                    .forEach(profilePath -> loadProfileConfig(profilePath, tenantConfig));
            }
            
            tenantConfigs.put(tenantId, tenantConfig);
            
        } catch (IOException e) {
            System.err.println("Error loading tenant " + tenantId + ": " + e.getMessage());
        }
    }
    
    /**
     * Load from classpath (fallback)
     */
    private void loadFromClasspath() {
        // For CCL tenant as example
        if ("ccl".equals(activeTenant)) {
            TenantConfiguration cclConfig = new TenantConfiguration("ccl");
            cclConfig.setName("CCL Communications Ltd");
            cclConfig.setType("END_USER");
            
            // Load profiles from classpath
            loadClasspathProfile("config/tenants/ccl/profile-dev.yml", "dev", cclConfig);
            loadClasspathProfile("config/tenants/ccl/profile-prod.yml", "prod", cclConfig);
            loadClasspathProfile("config/tenants/ccl/profile-staging.yml", "staging", cclConfig);
            loadClasspathProfile("config/tenants/ccl/profile-mock.yml", "mock", cclConfig);
            
            tenantConfigs.put("ccl", cclConfig);
        }
    }
    
    private void loadClasspathProfile(String resourcePath, String profileName, TenantConfiguration tenantConfig) {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            if (is != null) {
                ProfileConfiguration profile = new ProfileConfiguration(profileName);
                // Parse YAML and populate profile
                Yaml yaml = new Yaml();
                Map<String, Object> data = yaml.load(is);
                parseProfileData(data, profile);
                tenantConfig.addProfile(profileName, profile);
            }
        } catch (IOException e) {
            System.err.println("Error loading profile " + profileName + ": " + e.getMessage());
        }
    }
    
    private void loadYamlFile(Path path, TenantConfiguration config) {
        // Implementation to parse YAML and populate config
    }
    
    private void loadProfileConfig(Path profilePath, TenantConfiguration tenantConfig) {
        // Implementation to load profile configuration
    }
    
    private void parseProfileData(Map<String, Object> data, ProfileConfiguration profile) {
        // Implementation to parse profile data from YAML
    }
    
    /**
     * Get current tenant configuration
     */
    public TenantConfiguration getCurrentTenantConfig() {
        return currentTenantConfig;
    }
    
    /**
     * Get configuration for a specific tenant
     */
    public TenantConfiguration getTenantConfig(String tenantId) {
        return tenantConfigs.get(tenantId);
    }
    
    /**
     * Get all loaded tenant configurations
     */
    public Map<String, TenantConfiguration> getAllTenantConfigs() {
        return Collections.unmodifiableMap(tenantConfigs);
    }
    
    /**
     * Tenant Configuration holder
     */
    public static class TenantConfiguration {
        private String id;
        private String name;
        private String type;
        private String parentId;
        private Map<String, ProfileConfiguration> profiles = new HashMap<>();
        private Map<String, String> metadata = new HashMap<>();
        private Map<String, String> settings = new HashMap<>();
        
        public TenantConfiguration(String id) {
            this.id = id;
        }
        
        public void addProfile(String name, ProfileConfiguration profile) {
            profiles.put(name, profile);
        }
        
        public ProfileConfiguration getProfile(String profileName) {
            return profiles.get(profileName);
        }
        
        // Getters and setters
        public String getId() { return id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public String getParentId() { return parentId; }
        public void setParentId(String parentId) { this.parentId = parentId; }
        public Map<String, ProfileConfiguration> getProfiles() { return profiles; }
        public Map<String, String> getMetadata() { return metadata; }
        public Map<String, String> getSettings() { return settings; }
    }
    
    /**
     * Profile Configuration holder
     */
    public static class ProfileConfiguration {
        private String name;
        private String environment;
        private boolean active;
        private String databaseUrl;
        private String kafkaServers;
        private Map<String, SocketConfiguration> sockets = new HashMap<>();
        
        public ProfileConfiguration(String name) {
            this.name = name;
        }
        
        public int getSocketCount() {
            return sockets.size();
        }
        
        // Getters and setters
        public String getName() { return name; }
        public String getEnvironment() { return environment; }
        public void setEnvironment(String environment) { this.environment = environment; }
        public boolean isActive() { return active; }
        public void setActive(boolean active) { this.active = active; }
        public String getDatabaseUrl() { return databaseUrl; }
        public void setDatabaseUrl(String databaseUrl) { this.databaseUrl = databaseUrl; }
        public String getKafkaServers() { return kafkaServers; }
        public void setKafkaServers(String kafkaServers) { this.kafkaServers = kafkaServers; }
        public Map<String, SocketConfiguration> getSockets() { return sockets; }
    }
    
    /**
     * Socket Configuration holder
     */
    public static class SocketConfiguration {
        private String name;
        private String protocol;
        private String bindAddress;
        private int bindPort;
        private Map<String, String> settings = new HashMap<>();
        
        // Getters and setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getProtocol() { return protocol; }
        public void setProtocol(String protocol) { this.protocol = protocol; }
        public String getBindAddress() { return bindAddress; }
        public void setBindAddress(String bindAddress) { this.bindAddress = bindAddress; }
        public int getBindPort() { return bindPort; }
        public void setBindPort(int bindPort) { this.bindPort = bindPort; }
        public Map<String, String> getSettings() { return settings; }
    }
}