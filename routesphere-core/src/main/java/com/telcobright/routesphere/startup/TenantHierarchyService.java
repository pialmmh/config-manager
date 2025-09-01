package com.telcobright.routesphere.startup;

import com.telcobright.routesphere.tenant.Tenant;
import com.telcobright.routesphere.tenant.TenantHierarchy;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Service to manage tenant hierarchy with thread-safe access
 * This is a CDI bean that can be injected into other components
 */
@ApplicationScoped
public class TenantHierarchyService {
    
    private TenantHierarchy tenantHierarchy;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    
    /**
     * Set the tenant hierarchy (called during startup)
     */
    public void setTenantHierarchy(TenantHierarchy hierarchy) {
        lock.writeLock().lock();
        try {
            this.tenantHierarchy = hierarchy;
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    /**
     * Get the tenant hierarchy
     */
    public TenantHierarchy getTenantHierarchy() {
        lock.readLock().lock();
        try {
            return tenantHierarchy;
        } finally {
            lock.readLock().unlock();
        }
    }
    
    /**
     * Get tenant by ID
     */
    public Tenant getTenant(String tenantId) {
        lock.readLock().lock();
        try {
            return tenantHierarchy != null ? tenantHierarchy.getTenant(tenantId) : null;
        } finally {
            lock.readLock().unlock();
        }
    }
    
    /**
     * Get tenant path from root
     */
    public List<Tenant> getPathFromRoot(String tenantId) {
        lock.readLock().lock();
        try {
            return tenantHierarchy != null ? tenantHierarchy.getPathFromRoot(tenantId) : null;
        } finally {
            lock.readLock().unlock();
        }
    }
    
    /**
     * Get all ancestors of a tenant
     */
    public List<Tenant> getAncestors(String tenantId) {
        lock.readLock().lock();
        try {
            return tenantHierarchy != null ? tenantHierarchy.getAncestors(tenantId) : null;
        } finally {
            lock.readLock().unlock();
        }
    }
    
    /**
     * Get all descendants of a tenant
     */
    public List<Tenant> getDescendants(String tenantId) {
        lock.readLock().lock();
        try {
            return tenantHierarchy != null ? tenantHierarchy.getDescendants(tenantId) : null;
        } finally {
            lock.readLock().unlock();
        }
    }
    
    /**
     * Add a new tenant
     */
    public void addTenant(Tenant tenant) {
        lock.writeLock().lock();
        try {
            if (tenantHierarchy != null) {
                tenantHierarchy.addTenant(tenant);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    /**
     * Remove a tenant
     */
    public void removeTenant(String tenantId) {
        lock.writeLock().lock();
        try {
            if (tenantHierarchy != null) {
                tenantHierarchy.removeTenant(tenantId);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    /**
     * Update tenant status
     */
    public void updateTenantStatus(String tenantId, Tenant.TenantStatus status) {
        lock.writeLock().lock();
        try {
            Tenant tenant = getTenant(tenantId);
            if (tenant != null) {
                tenant.setStatus(status);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    /**
     * Get tenant count
     */
    public int getTenantCount() {
        lock.readLock().lock();
        try {
            return tenantHierarchy != null ? tenantHierarchy.getTenantCount() : 0;
        } finally {
            lock.readLock().unlock();
        }
    }
    
    /**
     * Check if tenant exists
     */
    public boolean tenantExists(String tenantId) {
        lock.readLock().lock();
        try {
            return tenantHierarchy != null && tenantHierarchy.getTenant(tenantId) != null;
        } finally {
            lock.readLock().unlock();
        }
    }
    
    /**
     * Get tenants by level
     */
    public List<Tenant> getTenantsByLevel(Tenant.TenantLevel level) {
        lock.readLock().lock();
        try {
            return tenantHierarchy != null ? tenantHierarchy.getTenantsByLevel(level) : null;
        } finally {
            lock.readLock().unlock();
        }
    }
    
    /**
     * Print hierarchy to console
     */
    public void printHierarchy() {
        lock.readLock().lock();
        try {
            if (tenantHierarchy != null) {
                tenantHierarchy.printHierarchy();
            }
        } finally {
            lock.readLock().unlock();
        }
    }
}