package com.telcobright.routesphere.tenant;

import com.telcobright.routesphere.tenant.dto.ConfigManagerTenant;
import java.util.Map;

/**
 * Mapper to convert ConfigManager Tenant to RouteSphere Tenant
 */
public class TenantMapper {
    
    /**
     * Convert ConfigManagerTenant hierarchy to RouteSphere TenantHierarchy
     */
    public static TenantHierarchy mapToTenantHierarchy(ConfigManagerTenant rootTenant) {
        TenantHierarchy hierarchy = new TenantHierarchy();
        
        if (rootTenant != null) {
            // Map root tenant
            Tenant root = mapToTenant(rootTenant, null, Tenant.TenantLevel.ROOT);
            hierarchy.addTenant(root);
            
            // Recursively map children
            mapChildren(rootTenant, root.getTenantId(), hierarchy, 1);
        }
        
        return hierarchy;
    }
    
    /**
     * Recursively map children tenants
     */
    private static void mapChildren(ConfigManagerTenant parent, String parentId, 
                                   TenantHierarchy hierarchy, int level) {
        Map<String, ConfigManagerTenant> children = parent.getChildren();
        
        if (children != null && !children.isEmpty()) {
            for (Map.Entry<String, ConfigManagerTenant> entry : children.entrySet()) {
                ConfigManagerTenant childTenant = entry.getValue();
                
                // Determine tenant level based on depth
                Tenant.TenantLevel tenantLevel = determineTenantLevel(level);
                
                // Create tenant
                Tenant tenant = mapToTenant(childTenant, parentId, tenantLevel);
                hierarchy.addTenant(tenant);
                
                // Recursively map this tenant's children
                mapChildren(childTenant, tenant.getTenantId(), hierarchy, level + 1);
            }
        }
    }
    
    /**
     * Map single ConfigManagerTenant to RouteSphere Tenant
     */
    private static Tenant mapToTenant(ConfigManagerTenant cmTenant, String parentId, 
                                     Tenant.TenantLevel level) {
        Tenant tenant = new Tenant(
            cmTenant.getDbName(),  // Use dbName as tenantId
            cmTenant.getDbName(),  // Use dbName as tenantName initially
            level
        );
        
        tenant.setParentTenantId(parentId);
        tenant.setStatus(Tenant.TenantStatus.ACTIVE);
        
        // Add properties from profile if available
        if (cmTenant.getProfile() != null) {
            tenant.addProperty("has_profile", "true");
            
            if (cmTenant.getProfile().getContext() != null) {
                tenant.addProperty("has_context", "true");
                
                // Store some context metrics as properties
                var context = cmTenant.getProfile().getContext();
                if (context.getPartners() != null) {
                    tenant.addProperty("partner_count", String.valueOf(context.getPartners().size()));
                }
                if (context.getRatePlans() != null) {
                    tenant.addProperty("rateplan_count", String.valueOf(context.getRatePlans().size()));
                }
            }
            
            if (cmTenant.getProfile().getCache() != null) {
                tenant.addProperty("has_cache", "true");
            }
        }
        
        // Store child count
        if (cmTenant.getChildren() != null) {
            tenant.addProperty("child_count", String.valueOf(cmTenant.getChildren().size()));
        }
        
        return tenant;
    }
    
    /**
     * Determine tenant level based on hierarchy depth
     */
    private static Tenant.TenantLevel determineTenantLevel(int depth) {
        // Map depth to tenant levels
        // Depth 0 = ROOT (handled separately)
        // Depth 1-5 = RESELLER_L1 to RESELLER_L5
        // Depth 6+ = END_USER
        
        switch (depth) {
            case 0:
                return Tenant.TenantLevel.ROOT;
            case 1:
                return Tenant.TenantLevel.RESELLER_L1;
            case 2:
                return Tenant.TenantLevel.RESELLER_L2;
            case 3:
                return Tenant.TenantLevel.RESELLER_L3;
            case 4:
                return Tenant.TenantLevel.RESELLER_L4;
            case 5:
                return Tenant.TenantLevel.RESELLER_L5;
            default:
                return Tenant.TenantLevel.END_USER;
        }
    }
    
    /**
     * Determine tenant type name based on level
     */
    public static String getTenantTypeName(Tenant.TenantLevel level) {
        switch (level) {
            case ROOT:
                return "Root Organization";
            case RESELLER_L1:
                return "Primary Reseller";
            case RESELLER_L2:
                return "Regional Reseller";
            case RESELLER_L3:
                return "Local Reseller";
            case RESELLER_L4:
                return "Sub-Reseller";
            case RESELLER_L5:
                return "Micro-Reseller";
            case END_USER:
                return "End Customer";
            default:
                return "Unknown";
        }
    }
}