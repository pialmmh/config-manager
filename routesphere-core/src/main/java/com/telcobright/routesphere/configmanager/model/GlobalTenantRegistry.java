package com.telcobright.routesphere.configmanager.model;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Global registry of all tenants
 * Provides quick lookup of tenants by various keys
 */
public class GlobalTenantRegistry {
    
    private final Map<String, ConfigTenant> tenantsByDbName = new ConcurrentHashMap<>();
    private final Map<String, ConfigTenant> tenantsByName = new ConcurrentHashMap<>();
    private ConfigTenant rootTenant;
    
    public void registerTenant(ConfigTenant tenant) {
        if (tenant == null || tenant.getDbName() == null) {
            return;
        }
        
        tenantsByDbName.put(tenant.getDbName(), tenant);
        
        if (tenant.getTenantName() != null) {
            tenantsByName.put(tenant.getTenantName(), tenant);
        }
        
        if (tenant.getType() == ConfigTenant.TenantType.ROOT) {
            this.rootTenant = tenant;
        }
    }
    
    public void unregisterTenant(String dbName) {
        ConfigTenant tenant = tenantsByDbName.remove(dbName);
        if (tenant != null && tenant.getTenantName() != null) {
            tenantsByName.remove(tenant.getTenantName());
        }
    }
    
    public ConfigTenant getTenantByDbName(String dbName) {
        return tenantsByDbName.get(dbName);
    }
    
    public ConfigTenant getTenantByName(String name) {
        return tenantsByName.get(name);
    }
    
    public ConfigTenant getRootTenant() {
        return rootTenant;
    }
    
    public void setRootTenant(ConfigTenant rootTenant) {
        this.rootTenant = rootTenant;
        registerTenant(rootTenant);
    }
    
    public Map<String, ConfigTenant> getAllTenants() {
        return new ConcurrentHashMap<>(tenantsByDbName);
    }
    
    public void clear() {
        tenantsByDbName.clear();
        tenantsByName.clear();
        rootTenant = null;
    }
    
    public int getTenantCount() {
        return tenantsByDbName.size();
    }
    
    public boolean hasTenant(String dbName) {
        return tenantsByDbName.containsKey(dbName);
    }
}