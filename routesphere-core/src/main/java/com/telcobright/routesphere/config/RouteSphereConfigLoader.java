package com.telcobright.routesphere.config;

import com.telcobright.routesphere.config.profiles.SocketProfile;
import com.telcobright.routesphere.protocols.Protocol;
import com.telcobright.routesphere.tenant.Tenant;
import com.telcobright.routesphere.tenant.TenantHierarchy;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Loads and manages RouteSphere configuration at startup
 */
public class RouteSphereConfigLoader {
    private final Map<String, SocketProfile> socketProfiles;
    private final TenantHierarchy tenantHierarchy;
    private final Properties configProperties;
    private boolean initialized = false;
    
    public RouteSphereConfigLoader() {
        this.socketProfiles = new ConcurrentHashMap<>();
        this.tenantHierarchy = new TenantHierarchy();
        this.configProperties = new Properties();
    }
    
    /**
     * Initialize configuration from properties file
     */
    public void initialize(String configFile) throws IOException {
        if (initialized) {
            System.out.println("Configuration already initialized");
            return;
        }
        
        // Load properties
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(configFile)) {
            if (input != null) {
                configProperties.load(input);
            } else {
                throw new IOException("Configuration file not found: " + configFile);
            }
        }
        
        // Load socket profiles
        loadSocketProfiles();
        
        // Load tenant hierarchy
        loadTenantHierarchy();
        
        initialized = true;
        System.out.println("RouteSphere configuration loaded successfully");
    }
    
    /**
     * Load socket profiles from configuration
     */
    private void loadSocketProfiles() {
        // Example configuration loading - in real implementation, this would read from properties
        
        // SIP Public Profile
        SocketProfile sipPublic = new SocketProfile("sip-public", Protocol.SIP_UDP, "0.0.0.0", 5060);
        sipPublic.setDefaultTenantId("root");
        sipPublic.setDefaultTenantName("Root Tenant");
        sipPublic.addExtendedProperty("realm", "public.routesphere.com");
        sipPublic.addExtendedProperty("max_connections", 10000);
        socketProfiles.put("sip-public", sipPublic);
        
        // SIP Private Profile
        SocketProfile sipPrivate = new SocketProfile("sip-private", Protocol.SIP_UDP, "192.168.1.100", 5080);
        sipPrivate.setDefaultTenantId("root");
        sipPrivate.setDefaultTenantName("Root Tenant");
        sipPrivate.addExtendedProperty("realm", "private.routesphere.com");
        sipPrivate.addExtendedProperty("max_connections", 5000);
        socketProfiles.put("sip-private", sipPrivate);
        
        // HTTP REST Profile
        SocketProfile httpRest = new SocketProfile("http-rest", Protocol.HTTP, "0.0.0.0", 8080);
        httpRest.setDefaultTenantId("root");
        httpRest.setDefaultTenantName("Root Tenant");
        httpRest.addExtendedProperty("context_path", "/api");
        httpRest.addExtendedProperty("thread_pool_size", 100);
        socketProfiles.put("http-rest", httpRest);
        
        // FreeSWITCH ESL Profile
        SocketProfile eslProfile = new SocketProfile("esl-default", Protocol.ESL, "127.0.0.1", 8021);
        eslProfile.setDefaultTenantId("root");
        eslProfile.setDefaultTenantName("Root Tenant");
        eslProfile.addExtendedProperty("password", "ClueCon");
        eslProfile.addExtendedProperty("reconnect_interval", 5000);
        socketProfiles.put("esl-default", eslProfile);
        
        System.out.println("Loaded " + socketProfiles.size() + " socket profiles");
    }
    
    /**
     * Load tenant hierarchy from configuration
     */
    private void loadTenantHierarchy() {
        // Create root tenant
        Tenant rootTenant = new Tenant("root", "Root Tenant", Tenant.TenantLevel.ROOT);
        rootTenant.setStatus(Tenant.TenantStatus.ACTIVE);
        tenantHierarchy.addTenant(rootTenant);
        
        // Create Level 1 resellers
        Tenant reseller1 = new Tenant("reseller1", "Premium Reseller", Tenant.TenantLevel.RESELLER_L1);
        reseller1.setParentTenantId("root");
        reseller1.setStatus(Tenant.TenantStatus.ACTIVE);
        tenantHierarchy.addTenant(reseller1);
        
        Tenant reseller2 = new Tenant("reseller2", "Standard Reseller", Tenant.TenantLevel.RESELLER_L1);
        reseller2.setParentTenantId("root");
        reseller2.setStatus(Tenant.TenantStatus.ACTIVE);
        tenantHierarchy.addTenant(reseller2);
        
        // Create Level 2 resellers
        Tenant subReseller1 = new Tenant("sub-reseller1", "Sub Reseller 1", Tenant.TenantLevel.RESELLER_L2);
        subReseller1.setParentTenantId("reseller1");
        subReseller1.setStatus(Tenant.TenantStatus.ACTIVE);
        tenantHierarchy.addTenant(subReseller1);
        
        // Create end users
        Tenant endUser1 = new Tenant("customer1", "Customer ABC", Tenant.TenantLevel.END_USER);
        endUser1.setParentTenantId("sub-reseller1");
        endUser1.setStatus(Tenant.TenantStatus.ACTIVE);
        tenantHierarchy.addTenant(endUser1);
        
        Tenant endUser2 = new Tenant("customer2", "Customer XYZ", Tenant.TenantLevel.END_USER);
        endUser2.setParentTenantId("reseller2");
        endUser2.setStatus(Tenant.TenantStatus.ACTIVE);
        tenantHierarchy.addTenant(endUser2);
        
        System.out.println("Loaded tenant hierarchy with " + tenantHierarchy.getTenantCount() + " tenants");
    }
    
    /**
     * Get socket profile by name
     */
    public SocketProfile getSocketProfile(String profileName) {
        return socketProfiles.get(profileName);
    }
    
    /**
     * Get all socket profiles
     */
    public Collection<SocketProfile> getAllSocketProfiles() {
        return socketProfiles.values();
    }
    
    /**
     * Get socket profiles by protocol
     */
    public List<SocketProfile> getProfilesByProtocol(Protocol protocol) {
        List<SocketProfile> profiles = new ArrayList<>();
        for (SocketProfile profile : socketProfiles.values()) {
            if (profile.getProtocol() == protocol) {
                profiles.add(profile);
            }
        }
        return profiles;
    }
    
    /**
     * Get tenant hierarchy
     */
    public TenantHierarchy getTenantHierarchy() {
        return tenantHierarchy;
    }
    
    /**
     * Check if configuration is initialized
     */
    public boolean isInitialized() {
        return initialized;
    }
    
    /**
     * Print configuration summary
     */
    public void printConfigurationSummary() {
        System.out.println("\n=== RouteSphere Configuration Summary ===");
        
        System.out.println("\nSocket Profiles:");
        for (SocketProfile profile : socketProfiles.values()) {
            System.out.println("  " + profile);
        }
        
        System.out.println("\nTenant Hierarchy:");
        tenantHierarchy.printHierarchy();
        
        System.out.println("\n=========================================\n");
    }
}