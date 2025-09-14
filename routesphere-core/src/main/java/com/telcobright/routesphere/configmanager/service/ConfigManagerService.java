package com.telcobright.routesphere.configmanager.service;

import com.telcobright.routesphere.config.GlobalConfigService;
import com.telcobright.rtc.domainmodel.nonentity.Tenant;
import com.telcobright.rtc.domainmodel.nonentity.TenantProfile;
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
    
    private final AtomicReference<Tenant> rootTenant = new AtomicReference<>();
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
            
            Tenant root = null;
            
            if (apiEnabled && !"mock".equals(activeProfile)) {
                // Try to load from ConfigManager API
                root = loadFromConfigManagerApi();
            }
            
            if (root == null) {
                // Fall back to mock data
                LOG.info("Using mock configuration data");
                root = buildMockTenantHierarchy(activeTenant);
            }
            
            // Root tenant dbName is already set in constructor
            
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
    private Tenant loadFromConfigManagerApi() {
        try {
            String url = configManagerApiUrl + "/get-tenant-root";
            LOG.infof("Fetching configuration from: %s", url);
            
            Response response = httpClient
                .target(url)
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(""));
            
            if (response.getStatus() == 200) {
                Tenant root = response.readEntity(Tenant.class);
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
    private Tenant buildMockTenantHierarchy(String rootName) {
        // Create root tenant
        Tenant root = new Tenant(rootName);
        root.setProfile(createMockProfile(rootName));

        // Create Level 1 resellers
        Tenant reseller1 = new Tenant(rootName + "_premium");
        reseller1.setProfile(createMockProfile(reseller1.getDbName()));
        reseller1.setParent(rootName);
        root.addChild(reseller1.getDbName(), reseller1);

        Tenant reseller2 = new Tenant(rootName + "_standard");
        reseller2.setProfile(createMockProfile(reseller2.getDbName()));
        reseller2.setParent(rootName);
        root.addChild(reseller2.getDbName(), reseller2);

        // Create Level 2 resellers under premium
        Tenant reseller2_1 = new Tenant(rootName + "_premium_east");
        reseller2_1.setProfile(createMockProfile(reseller2_1.getDbName()));
        reseller2_1.setParent(reseller1.getDbName());
        reseller1.addChild(reseller2_1.getDbName(), reseller2_1);

        // Create end users
        Tenant endUser1 = new Tenant(rootName + "_premium_east_customer1");
        endUser1.setProfile(createMockProfile(endUser1.getDbName()));
        endUser1.setParent(reseller2_1.getDbName());
        reseller2_1.addChild(endUser1.getDbName(), endUser1);

        return root;
    }
    
    /**
     * Create mock profile for a tenant
     */
    private TenantProfile createMockProfile(String dbName) {
        // Create a mock TenantProfile
        // Note: TenantProfile requires DynamicDatabaseService, DataLoader, etc.
        // For mock data, we'll return null and let the profile be initialized later
        return null;
    }
    
    /**
     * Register tenant hierarchy in global registry
     */
    private void registerTenantHierarchy(Tenant tenant) {
        if (tenant == null) return;
        
        registry.registerTenant(tenant);
        
        // Recursively register children
        for (Tenant child : tenant.getChildren().values()) {
            registerTenantHierarchy(child);
        }
    }
    
    // Public API methods
    
    public Tenant getRootTenant() {
        return rootTenant.get();
    }
    
    public GlobalTenantRegistry getRegistry() {
        return registry;
    }
    
    public Tenant getTenantByDbName(String dbName) {
        return registry.getTenantByDbName(dbName);
    }
    
    public Tenant getTenantByName(String name) {
        return registry.getTenantByName(name);
    }
}