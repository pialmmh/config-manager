package com.telcobright.routesphere.tenant;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages multi-level tenant hierarchy with fast lookups
 */
public class TenantHierarchy {
    private final Map<String, Tenant> tenantMap;
    private final Map<String, List<String>> parentToChildren;
    private final Map<Tenant.TenantLevel, List<String>> levelToTenants;
    private String rootTenantId;
    
    public TenantHierarchy() {
        this.tenantMap = new ConcurrentHashMap<>();
        this.parentToChildren = new ConcurrentHashMap<>();
        this.levelToTenants = new ConcurrentHashMap<>();
        
        // Initialize level maps
        for (Tenant.TenantLevel level : Tenant.TenantLevel.values()) {
            levelToTenants.put(level, new ArrayList<>());
        }
    }
    
    /**
     * Add a tenant to the hierarchy
     */
    public void addTenant(Tenant tenant) {
        tenantMap.put(tenant.getTenantId(), tenant);
        
        // Update level mapping
        levelToTenants.get(tenant.getLevel()).add(tenant.getTenantId());
        
        // Update parent-child mapping
        if (tenant.getParentTenantId() != null) {
            parentToChildren.computeIfAbsent(tenant.getParentTenantId(), 
                k -> new ArrayList<>()).add(tenant.getTenantId());
            
            // Update parent's child list
            Tenant parent = tenantMap.get(tenant.getParentTenantId());
            if (parent != null) {
                parent.addChildTenant(tenant.getTenantId());
            }
        } else if (tenant.isRoot()) {
            this.rootTenantId = tenant.getTenantId();
        }
    }
    
    /**
     * Get tenant by ID
     */
    public Tenant getTenant(String tenantId) {
        return tenantMap.get(tenantId);
    }
    
    /**
     * Get root tenant
     */
    public Tenant getRootTenant() {
        return rootTenantId != null ? tenantMap.get(rootTenantId) : null;
    }
    
    /**
     * Get all ancestors of a tenant (from immediate parent to root)
     */
    public List<Tenant> getAncestors(String tenantId) {
        List<Tenant> ancestors = new ArrayList<>();
        Tenant current = tenantMap.get(tenantId);
        
        while (current != null && current.getParentTenantId() != null) {
            Tenant parent = tenantMap.get(current.getParentTenantId());
            if (parent != null) {
                ancestors.add(parent);
                current = parent;
            } else {
                break;
            }
        }
        
        return ancestors;
    }
    
    /**
     * Get path from root to tenant
     */
    public List<Tenant> getPathFromRoot(String tenantId) {
        List<Tenant> path = new ArrayList<>();
        List<Tenant> ancestors = getAncestors(tenantId);
        
        // Reverse to get root-to-tenant path
        for (int i = ancestors.size() - 1; i >= 0; i--) {
            path.add(ancestors.get(i));
        }
        
        // Add the tenant itself
        Tenant tenant = tenantMap.get(tenantId);
        if (tenant != null) {
            path.add(tenant);
        }
        
        return path;
    }
    
    /**
     * Get all descendants of a tenant
     */
    public List<Tenant> getDescendants(String tenantId) {
        List<Tenant> descendants = new ArrayList<>();
        Queue<String> queue = new LinkedList<>();
        queue.offer(tenantId);
        
        while (!queue.isEmpty()) {
            String currentId = queue.poll();
            List<String> children = parentToChildren.get(currentId);
            
            if (children != null) {
                for (String childId : children) {
                    Tenant child = tenantMap.get(childId);
                    if (child != null) {
                        descendants.add(child);
                        queue.offer(childId);
                    }
                }
            }
        }
        
        return descendants;
    }
    
    /**
     * Get all tenants at a specific level
     */
    public List<Tenant> getTenantsAtLevel(Tenant.TenantLevel level) {
        List<Tenant> tenants = new ArrayList<>();
        List<String> tenantIds = levelToTenants.get(level);
        
        if (tenantIds != null) {
            for (String id : tenantIds) {
                Tenant tenant = tenantMap.get(id);
                if (tenant != null) {
                    tenants.add(tenant);
                }
            }
        }
        
        return tenants;
    }
    
    /**
     * Get all tenants by level (alias for getTenantsAtLevel)
     */
    public List<Tenant> getTenantsByLevel(Tenant.TenantLevel level) {
        return getTenantsAtLevel(level);
    }
    
    /**
     * Remove a tenant and all its descendants
     */
    public void removeTenant(String tenantId) {
        Tenant tenant = tenantMap.get(tenantId);
        if (tenant == null) {
            return;
        }
        
        // Remove all descendants first
        List<Tenant> descendants = getDescendants(tenantId);
        for (Tenant descendant : descendants) {
            removeTenantInternal(descendant.getTenantId());
        }
        
        // Remove the tenant itself
        removeTenantInternal(tenantId);
    }
    
    /**
     * Internal method to remove a single tenant
     */
    private void removeTenantInternal(String tenantId) {
        Tenant tenant = tenantMap.remove(tenantId);
        if (tenant == null) {
            return;
        }
        
        // Remove from parent's children list
        if (tenant.getParentTenantId() != null) {
            List<String> siblings = parentToChildren.get(tenant.getParentTenantId());
            if (siblings != null) {
                siblings.remove(tenantId);
            }
        }
        
        // Remove from level mapping
        List<String> levelTenants = levelToTenants.get(tenant.getLevel());
        if (levelTenants != null) {
            levelTenants.remove(tenantId);
        }
        
        // Remove from parent mapping
        parentToChildren.remove(tenantId);
    }
    
    /**
     * Check if tenant exists
     */
    public boolean tenantExists(String tenantId) {
        return tenantMap.containsKey(tenantId);
    }
    
    /**
     * Get total tenant count
     */
    public int getTenantCount() {
        return tenantMap.size();
    }
    
    /**
     * Print hierarchy tree (for debugging)
     */
    public void printHierarchy() {
        if (rootTenantId != null) {
            printTenantTree(rootTenantId, 0);
        }
    }
    
    private void printTenantTree(String tenantId, int depth) {
        Tenant tenant = tenantMap.get(tenantId);
        if (tenant != null) {
            String indent = "  ".repeat(depth);
            System.out.println(indent + "└─ " + tenant);
            
            List<String> children = parentToChildren.get(tenantId);
            if (children != null) {
                for (String childId : children) {
                    printTenantTree(childId, depth + 1);
                }
            }
        }
    }
}