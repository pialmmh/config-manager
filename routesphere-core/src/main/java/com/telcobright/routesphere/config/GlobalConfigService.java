package com.telcobright.routesphere.config;

import io.quarkus.runtime.Startup;
import io.quarkus.runtime.StartupEvent;
import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.Map;
import java.util.Optional;

/**
 * Global Configuration Service - Simplified Version
 * Loads ONLY the active tenant and profile from routesphere-global.yml
 * This is the first configuration loaded at startup
 */
@ApplicationScoped
@Startup
public class GlobalConfigService {
    
    @ConfigProperty(name = "routesphere.global.config-file", defaultValue = "routesphere-global.yml")
    String globalConfigFile;
    
    // Environment variable overrides
    @ConfigProperty(name = "ROUTESPHERE_ACTIVE_TENANT")
    Optional<String> envActiveTenant;
    
    @ConfigProperty(name = "ROUTESPHERE_ACTIVE_PROFILE")
    Optional<String> envActiveProfile;
    
    // Allow environment override (can be disabled in production)
    @ConfigProperty(name = "routesphere.global.allow-env-override", defaultValue = "true")
    boolean allowEnvOverride;
    
    private String activeTenant;
    private String activeProfile;
    
    /**
     * Load global configuration with highest priority
     * This runs before all other configuration services
     */
    void onStart(@Observes @Priority(1) StartupEvent event) {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║     RouteSphere Global Configuration    ║");
        System.out.println("╚════════════════════════════════════════╝\n");
        
        // Load from global config file
        loadGlobalConfiguration();
        
        // Apply environment overrides if allowed
        applyEnvironmentOverrides();
        
        // Validate and apply defaults
        validateAndApplyDefaults();
        
        // Display active configuration
        displayConfiguration();
    }
    
    /**
     * Load global configuration from YAML file
     */
    private void loadGlobalConfiguration() {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(globalConfigFile)) {
            if (is != null) {
                Yaml yaml = new Yaml();
                Map<String, Object> config = yaml.load(is);
                
                if (config != null && config.containsKey("routesphere")) {
                    Map<String, Object> routesphere = (Map<String, Object>) config.get("routesphere");
                    if (routesphere.containsKey("global")) {
                        Map<String, Object> global = (Map<String, Object>) routesphere.get("global");
                        
                        // Load only tenant and profile
                        activeTenant = (String) global.get("tenant");
                        activeProfile = (String) global.get("profile");
                        
                        System.out.println("✓ Loaded configuration from: " + globalConfigFile);
                    }
                }
            } else {
                System.out.println("⚠️  Global config file not found: " + globalConfigFile);
            }
        } catch (Exception e) {
            System.err.println("❌ Error loading global configuration: " + e.getMessage());
        }
    }
    
    /**
     * Apply environment variable overrides if allowed
     */
    private void applyEnvironmentOverrides() {
        if (allowEnvOverride) {
            boolean overrideApplied = false;
            
            if (envActiveTenant.isPresent()) {
                activeTenant = envActiveTenant.get();
                System.out.println("📌 Environment override: ROUTESPHERE_ACTIVE_TENANT=" + activeTenant);
                overrideApplied = true;
            }
            
            if (envActiveProfile.isPresent()) {
                activeProfile = envActiveProfile.get();
                System.out.println("📌 Environment override: ROUTESPHERE_ACTIVE_PROFILE=" + activeProfile);
                overrideApplied = true;
            }
            
            if (!overrideApplied && (envActiveTenant.isEmpty() && envActiveProfile.isEmpty())) {
                System.out.println("ℹ️  No environment overrides applied");
            }
        } else {
            System.out.println("ℹ️  Environment overrides disabled");
        }
    }
    
    /**
     * Validate configuration and apply defaults if needed
     */
    private void validateAndApplyDefaults() {
        // Apply default tenant if not set
        if (activeTenant == null || activeTenant.trim().isEmpty()) {
            activeTenant = "telcobright_root";
            System.out.println("⚠️  No tenant specified, using default: " + activeTenant);
        }
        
        // Apply default profile if not set
        if (activeProfile == null || activeProfile.trim().isEmpty()) {
            activeProfile = "dev";
            System.out.println("⚠️  No profile specified, using default: " + activeProfile);
        }
        
        // Validate profile name
        if (!isValidProfile(activeProfile)) {
            System.out.println("⚠️  Warning: Profile '" + activeProfile + 
                "' may not be standard (expected: dev, staging, prod, mock)");
        }
    }
    
    /**
     * Check if profile name is valid
     */
    private boolean isValidProfile(String profile) {
        return "dev".equals(profile) || 
               "staging".equals(profile) || 
               "prod".equals(profile) || 
               "mock".equals(profile);
    }
    
    /**
     * Display the active configuration
     */
    private void displayConfiguration() {
        System.out.println("\n┌─────────────────────────────────────────┐");
        System.out.println("│ Active Configuration:                   │");
        System.out.println("├─────────────────────────────────────────┤");
        System.out.println("│ Tenant  : " + String.format("%-30s", activeTenant) + "│");
        System.out.println("│ Profile : " + String.format("%-30s", activeProfile) + "│");
        System.out.println("└─────────────────────────────────────────┘\n");
    }
    
    // Public API Methods
    
    /**
     * Get the active tenant name
     */
    public String getActiveTenant() {
        return activeTenant;
    }
    
    /**
     * Get the active profile name
     */
    public String getActiveProfile() {
        return activeProfile;
    }
    
    /**
     * Check if environment override is allowed
     */
    public boolean isEnvironmentOverrideAllowed() {
        return allowEnvOverride;
    }
    
    /**
     * Get configuration as a formatted string
     */
    public String getConfigurationSummary() {
        return String.format("Tenant: %s, Profile: %s", activeTenant, activeProfile);
    }
    
    /**
     * Check if running in production mode
     */
    public boolean isProduction() {
        return "prod".equals(activeProfile) || "production".equals(activeProfile);
    }
    
    /**
     * Check if running in development mode
     */
    public boolean isDevelopment() {
        return "dev".equals(activeProfile) || "development".equals(activeProfile);
    }
    
    /**
     * Check if running in staging mode
     */
    public boolean isStaging() {
        return "staging".equals(activeProfile);
    }
    
    /**
     * Check if running in mock/test mode
     */
    public boolean isMock() {
        return "mock".equals(activeProfile) || "test".equals(activeProfile);
    }
    
    @Override
    public String toString() {
        return "GlobalConfig[tenant=" + activeTenant + ", profile=" + activeProfile + "]";
    }
}