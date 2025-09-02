package com.telcobright.routesphere.configmanager.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Tenant model for ConfigManager
 * Represents a tenant in the configuration hierarchy
 */
public class ConfigTenant {
    
    private String dbName;
    private String tenantName;
    private String parent;
    private TenantType type;
    private ConfigTenantProfile profile;
    private final Map<String, ConfigTenant> children = new ConcurrentHashMap<>();
    private Map<String, Object> properties = new HashMap<>();
    
    public enum TenantType {
        ROOT,
        RESELLER_L1,
        RESELLER_L2,
        RESELLER_L3,
        RESELLER_L4,
        RESELLER_L5,
        END_USER
    }
    
    public ConfigTenant() {
    }
    
    public ConfigTenant(String dbName) {
        this.dbName = dbName;
        this.tenantName = dbName;
        this.type = determineTypeFromDbName(dbName);
    }
    
    public ConfigTenant(String dbName, String tenantName) {
        this.dbName = dbName;
        this.tenantName = tenantName;
        this.type = determineTypeFromDbName(dbName);
    }
    
    private TenantType determineTypeFromDbName(String dbName) {
        if (dbName == null) return TenantType.END_USER;
        
        String[] parts = dbName.split("_");
        if (parts.length == 1) {
            return TenantType.ROOT;
        } else if (parts.length == 2) {
            return TenantType.RESELLER_L1;
        } else if (parts.length == 3) {
            return TenantType.RESELLER_L2;
        } else if (parts.length == 4) {
            return TenantType.RESELLER_L3;
        } else if (parts.length == 5) {
            return TenantType.RESELLER_L4;
        } else if (parts.length == 6) {
            return TenantType.RESELLER_L5;
        } else {
            return TenantType.END_USER;
        }
    }
    
    public void addChild(String key, ConfigTenant child) {
        children.put(key, child);
        child.setParent(this.dbName);
    }
    
    public ConfigTenant getChild(String key) {
        return children.get(key);
    }
    
    @JsonIgnore
    public Map<String, ConfigTenant> getAllChildren() {
        return new HashMap<>(children);
    }
    
    // Getters and Setters
    public String getDbName() {
        return dbName;
    }
    
    public void setDbName(String dbName) {
        this.dbName = dbName;
    }
    
    public String getTenantName() {
        return tenantName;
    }
    
    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }
    
    public String getParent() {
        return parent;
    }
    
    public void setParent(String parent) {
        this.parent = parent;
    }
    
    public TenantType getType() {
        return type;
    }
    
    public void setType(TenantType type) {
        this.type = type;
    }
    
    public ConfigTenantProfile getProfile() {
        return profile;
    }
    
    public void setProfile(ConfigTenantProfile profile) {
        this.profile = profile;
    }
    
    public Map<String, ConfigTenant> getChildren() {
        return children;
    }
    
    public Map<String, Object> getProperties() {
        return properties;
    }
    
    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }
    
    public void addProperty(String key, Object value) {
        this.properties.put(key, value);
    }
    
    public Object getProperty(String key) {
        return properties.get(key);
    }
}