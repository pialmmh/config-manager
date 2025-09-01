package com.telcobright.routesphere.tenant.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.HashMap;
import java.util.Map;

/**
 * DTO class to receive Tenant object from ConfigManager API
 * Matches the structure of freeswitch.config.dynamic.Tenant
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ConfigManagerTenant {
    private String dbName;
    private String parent;
    private Map<String, ConfigManagerTenant> children = new HashMap<>();
    private TenantProfileDTO profile;
    
    // Default constructor for JSON deserialization
    public ConfigManagerTenant() {
    }
    
    public ConfigManagerTenant(String dbName) {
        this.dbName = dbName;
    }
    
    public void addChild(String childDbName, ConfigManagerTenant child) {
        this.children.put(childDbName, child);
    }
    
    // Getters and Setters
    public String getDbName() {
        return dbName;
    }
    
    public void setDbName(String dbName) {
        this.dbName = dbName;
    }
    
    public String getParent() {
        return parent;
    }
    
    public void setParent(String parent) {
        this.parent = parent;
    }
    
    public Map<String, ConfigManagerTenant> getChildren() {
        return children;
    }
    
    public void setChildren(Map<String, ConfigManagerTenant> children) {
        this.children = children;
    }
    
    public TenantProfileDTO getProfile() {
        return profile;
    }
    
    public void setProfile(TenantProfileDTO profile) {
        this.profile = profile;
    }
    
    @Override
    public String toString() {
        return "ConfigManagerTenant{" +
            "dbName='" + dbName + '\'' +
            ", parent='" + parent + '\'' +
            ", childrenCount=" + (children != null ? children.size() : 0) +
            ", hasProfile=" + (profile != null) +
            '}';
    }
}