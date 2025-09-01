package com.telcobright.routesphere.tenant.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * DTO class for TenantProfile from ConfigManager
 * Simplified version - only includes context and cache references
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TenantProfileDTO {
    private DynamicContextDTO context;
    private AllCacheDTO cache;
    
    // Default constructor for JSON deserialization
    public TenantProfileDTO() {
    }
    
    // Getters and Setters
    public DynamicContextDTO getContext() {
        return context;
    }
    
    public void setContext(DynamicContextDTO context) {
        this.context = context;
    }
    
    public AllCacheDTO getCache() {
        return cache;
    }
    
    public void setCache(AllCacheDTO cache) {
        this.cache = cache;
    }
    
    @Override
    public String toString() {
        return "TenantProfileDTO{" +
            "hasContext=" + (context != null) +
            ", hasCache=" + (cache != null) +
            '}';
    }
}