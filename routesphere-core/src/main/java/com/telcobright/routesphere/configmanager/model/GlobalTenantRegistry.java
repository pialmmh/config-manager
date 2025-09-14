package com.telcobright.routesphere.configmanager.model;

import com.telcobright.rtc.domainmodel.nonentity.Tenant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Global registry of all tenants
 * Provides quick lookup of tenants by various keys
 */
public class GlobalTenantRegistry {
    
    private final Map<String, Tenant> tenantsByDbName = new ConcurrentHashMap<>();
    private final Map<String, Tenant> tenantsByName = new ConcurrentHashMap<>();
    private Tenant rootTenant;
    
    public void registerTenant(Tenant tenant) {
        if (tenant == null || tenant.getDbName() == null) {
            return;
        }

        tenantsByDbName.put(tenant.getDbName(), tenant);

        // For Tenant class, we use dbName as the tenant name
        tenantsByName.put(tenant.getDbName(), tenant);

        // Check if this is the root tenant (no parent)
        if (tenant.getParent() == null) {
            this.rootTenant = tenant;
        }
    }
    
    public void unregisterTenant(String dbName) {
        Tenant tenant = tenantsByDbName.remove(dbName);
        if (tenant != null) {
            tenantsByName.remove(tenant.getDbName());
        }
    }
    
    public Tenant getTenantByDbName(String dbName) {
        return tenantsByDbName.get(dbName);
    }
    
    public Tenant getTenantByName(String name) {
        return tenantsByName.get(name);
    }
    
    public Tenant getRootTenant() {
        return rootTenant;
    }
    
    public void setRootTenant(Tenant rootTenant) {
        this.rootTenant = rootTenant;
        registerTenant(rootTenant);
    }
    
    public Map<String, Tenant> getAllTenants() {
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