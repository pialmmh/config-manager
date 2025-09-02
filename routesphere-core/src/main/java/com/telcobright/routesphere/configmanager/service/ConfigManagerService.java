package com.telcobright.routesphere.configmanager.service;

import com.telcobright.routesphere.config.GlobalConfigService;
import com.telcobright.routesphere.configmanager.model.ConfigTenant;
import com.telcobright.routesphere.configmanager.model.ConfigTenantProfile;
import com.telcobright.routesphere.configmanager.model.GlobalTenantRegistry;
import io.quarkus.runtime.StartupEvent;
import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Main service for managing configuration
 * Loads and maintains tenant hierarchy
 */
@ApplicationScoped
public class ConfigManagerService {
    
    private static final Logger LOG = Logger.getLogger(ConfigManagerService.class);
    
    @Inject
    GlobalConfigService globalConfigService;
    
    @ConfigProperty(name = "configmanager.api.url", defaultValue = "http://localhost:7070")
    String configManagerApiUrl;
    
    @ConfigProperty(name = "configmanager.api.enabled", defaultValue = "true")
    boolean apiEnabled;
    
    private final AtomicReference<ConfigTenant> rootTenant = new AtomicReference<>();
    private final GlobalTenantRegistry registry = new GlobalTenantRegistry();
    private final Client httpClient = ClientBuilder.newClient();
    
    /**
     * Initialize configuration on startup
     * Priority 5 - runs after GlobalConfigService and DeploymentConfigService
     */
    void onStart(@Observes @Priority(5) StartupEvent event) {
        LOG.info("╔════════════════════════════════════════╗");
        LOG.info("║     ConfigManager Service Starting      ║");
        LOG.info("╚════════════════════════════════════════╝");
        
        // Load initial configuration
        loadConfiguration();
    }
    
    /**
     * Load configuration from ConfigManager API or build mock data
     */
    public synchronized void loadConfiguration() {
        try {
            String activeTenant = globalConfigService.getActiveTenant();
            String activeProfile = globalConfigService.getActiveProfile();
            
            LOG.infof("Loading configuration for tenant: %s, profile: %s", 
                activeTenant, activeProfile);
            
            ConfigTenant root = null;
            
            if (apiEnabled && !"mock".equals(activeProfile)) {
                // Try to load from ConfigManager API
                root = loadFromConfigManagerApi();
            }
            
            if (root == null) {
                // Fall back to mock data
                LOG.info("Using mock configuration data");
                root = buildMockTenantHierarchy(activeTenant);
            }
            
            // Update root tenant name to match active tenant
            if (root != null) {
                root.setDbName(activeTenant);
                root.setTenantName(activeTenant + " Organization");
            }
            
            // Store in registry and atomic reference
            rootTenant.set(root);
            registry.clear();
            registerTenantHierarchy(root);
            
            LOG.infof("Configuration loaded successfully. Total tenants: %d", 
                registry.getTenantCount());
            
        } catch (Exception e) {
            LOG.errorf("Error loading configuration: %s", e.getMessage());
        }
    }
    
    /**
     * Reload configuration - called when Kafka notification received
     */
    public void reloadConfiguration() {
        LOG.info("Reloading configuration...");
        loadConfiguration();
    }
    
    /**
     * Load tenant hierarchy from ConfigManager API
     */
    private ConfigTenant loadFromConfigManagerApi() {
        try {
            String url = configManagerApiUrl + "/get-tenant-root";
            LOG.infof("Fetching configuration from: %s", url);
            
            Response response = httpClient
                .target(url)
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(""));
            
            if (response.getStatus() == 200) {
                ConfigTenant root = response.readEntity(ConfigTenant.class);
                LOG.info("Successfully loaded configuration from ConfigManager API");
                return root;
            } else {
                LOG.warnf("ConfigManager API returned status: %d", response.getStatus());
                return null;
            }
            
        } catch (Exception e) {
            LOG.errorf("Failed to load from ConfigManager API: %s", e.getMessage());
            return null;
        }
    }
    
    /**
     * Build mock tenant hierarchy for testing
     */
    private ConfigTenant buildMockTenantHierarchy(String rootName) {
        // Create root tenant
        ConfigTenant root = new ConfigTenant(rootName, rootName + " Organization");
        root.setType(ConfigTenant.TenantType.ROOT);
        root.setProfile(createMockProfile(rootName));
        
        // Add some properties
        root.addProperty("max_resellers", 1000);
        root.addProperty("max_end_users", 100000);
        root.addProperty("api_version", "v2");
        
        // Create Level 1 resellers
        ConfigTenant reseller1 = new ConfigTenant(rootName + "_premium", "Premium Partner");
        reseller1.setType(ConfigTenant.TenantType.RESELLER_L1);
        reseller1.setProfile(createMockProfile(reseller1.getDbName()));
        root.addChild(reseller1.getDbName(), reseller1);
        
        ConfigTenant reseller2 = new ConfigTenant(rootName + "_standard", "Standard Partner");
        reseller2.setType(ConfigTenant.TenantType.RESELLER_L1);
        reseller2.setProfile(createMockProfile(reseller2.getDbName()));
        root.addChild(reseller2.getDbName(), reseller2);
        
        // Create Level 2 resellers under premium
        ConfigTenant reseller2_1 = new ConfigTenant(rootName + "_premium_east", "Eastern Region");
        reseller2_1.setType(ConfigTenant.TenantType.RESELLER_L2);
        reseller2_1.setProfile(createMockProfile(reseller2_1.getDbName()));
        reseller1.addChild(reseller2_1.getDbName(), reseller2_1);
        
        // Create end users
        ConfigTenant endUser1 = new ConfigTenant(rootName + "_premium_east_customer1", "Customer 1");
        endUser1.setType(ConfigTenant.TenantType.END_USER);
        endUser1.setProfile(createMockProfile(endUser1.getDbName()));
        reseller2_1.addChild(endUser1.getDbName(), endUser1);
        
        return root;
    }
    
    /**
     * Create mock profile for a tenant
     */
    private ConfigTenantProfile createMockProfile(String dbName) {
        ConfigTenantProfile profile = new ConfigTenantProfile(dbName);
        
        // Database config
        ConfigTenantProfile.DatabaseConfig dbConfig = new ConfigTenantProfile.DatabaseConfig();
        dbConfig.setUrl("jdbc:mysql://127.0.0.1:3306/" + dbName);
        dbConfig.setUsername("root");
        dbConfig.setPassword("123456");
        dbConfig.setDriver("com.mysql.cj.jdbc.Driver");
        profile.setDatabaseConfig(dbConfig);
        
        // Kafka config
        ConfigTenantProfile.KafkaConfig kafkaConfig = new ConfigTenantProfile.KafkaConfig();
        kafkaConfig.setBootstrapServers("localhost:9092");
        kafkaConfig.setGroupId(dbName + "_group");
        profile.setKafkaConfig(kafkaConfig);
        
        // Redis config
        ConfigTenantProfile.RedisConfig redisConfig = new ConfigTenantProfile.RedisConfig();
        redisConfig.setHost("localhost");
        redisConfig.setPort(6379);
        redisConfig.setDatabase(0);
        profile.setRedisConfig(redisConfig);
        
        // Add some cache data
        profile.putInCache("routes", "mock_routes_data");
        profile.putInCache("partners", "mock_partners_data");
        profile.putInCache("dialplans", "mock_dialplans_data");
        
        return profile;
    }
    
    /**
     * Register tenant hierarchy in global registry
     */
    private void registerTenantHierarchy(ConfigTenant tenant) {
        if (tenant == null) return;
        
        registry.registerTenant(tenant);
        
        // Recursively register children
        for (ConfigTenant child : tenant.getChildren().values()) {
            registerTenantHierarchy(child);
        }
    }
    
    // Public API methods
    
    public ConfigTenant getRootTenant() {
        return rootTenant.get();
    }
    
    public GlobalTenantRegistry getRegistry() {
        return registry;
    }
    
    public ConfigTenant getTenantByDbName(String dbName) {
        return registry.getTenantByDbName(dbName);
    }
    
    public ConfigTenant getTenantByName(String name) {
        return registry.getTenantByName(name);
    }
}