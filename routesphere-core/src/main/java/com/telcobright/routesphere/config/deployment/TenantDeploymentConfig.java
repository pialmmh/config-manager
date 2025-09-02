package com.telcobright.routesphere.config.deployment;

import io.quarkus.runtime.annotations.ConfigItem;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithName;

import java.util.Map;
import java.util.Optional;

/**
 * Tenant Deployment Configuration using Quarkus ConfigMapping
 * Each tenant (party/partner) can have multiple deployment profiles
 * 
 * Configuration in application.yml:
 * routesphere:
 *   tenants:
 *     tenant1:
 *       name: "Premium Partner"
 *       type: "RESELLER_L1"
 *       profiles:
 *         dev:
 *           active: true
 */
@ConfigMapping(prefix = "routesphere.tenant")
public interface TenantDeploymentConfig {
    
    /**
     * Tenant identifier
     */
    String id();
    
    /**
     * Human-readable tenant name
     */
    String name();
    
    /**
     * Tenant type: ROOT, RESELLER_L1, RESELLER_L2, END_USER
     */
    @WithName("type")
    Optional<String> tenantType();
    
    /**
     * Parent tenant ID for hierarchy
     */
    @WithName("parent-id")
    Optional<String> parentTenantId();
    
    /**
     * Database name for this tenant (from ConfigManager)
     */
    @WithName("db-name")
    Optional<String> databaseName();
    
    /**
     * Deployment profiles (mock, dev, staging, prod)
     */
    Map<String, ProfileConfig> profiles();
    
    /**
     * Currently active profile
     */
    @WithName("active-profile")
    Optional<String> activeProfile();
    
    /**
     * Tenant metadata
     */
    Map<String, String> metadata();
    
    /**
     * Is tenant active
     */
    @WithName("active")
    Optional<Boolean> isActive();
    
    /**
     * Profile Configuration for each environment
     */
    interface ProfileConfig {
        
        /**
         * Profile name (dev, staging, prod, mock)
         */
        String name();
        
        /**
         * General configuration for this profile
         */
        GeneralConfig general();
        
        /**
         * Socket configurations for different protocols
         */
        Map<String, SocketProfileConfig> sockets();
        
        /**
         * Is this profile active
         */
        @WithName("active")
        Optional<Boolean> isActive();
        
        /**
         * Profile-specific metadata
         */
        Map<String, String> metadata();
    }
}