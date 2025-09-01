package com.telcobright.routesphere.pipeline.processors;

import com.telcobright.routesphere.pipeline.PipelineProcessor;
import com.telcobright.routesphere.pipeline.RoutingContext;
import com.telcobright.routesphere.pipeline.RoutingResponse;
import com.telcobright.routesphere.tenant.Tenant;
import com.telcobright.routesphere.tenant.TenantHierarchy;
import java.util.List;

/**
 * Identifies the tenant and walks through reseller hierarchy
 */
public class TenantIdentificationProcessor implements PipelineProcessor {
    private final TenantHierarchy tenantHierarchy;
    
    public TenantIdentificationProcessor(TenantHierarchy tenantHierarchy) {
        this.tenantHierarchy = tenantHierarchy;
    }
    
    @Override
    public boolean process(RoutingContext context) {
        context.moveToStage(RoutingContext.PipelineStage.TENANT_IDENTIFICATION);
        
        // Get tenant ID from request
        String tenantId = context.getRequest().getTenantId();
        
        if (tenantId == null || tenantId.isEmpty()) {
            // Use default from profile if not specified
            tenantId = "root"; // In real implementation, get from socket profile
        }
        
        // Find tenant
        Tenant tenant = tenantHierarchy.getTenant(tenantId);
        if (tenant == null) {
            context.getResponse()
                .withType(RoutingResponse.ResponseType.REJECTED)
                .withStatus(404, "Tenant not found: " + tenantId);
            return false;
        }
        
        // Check tenant status
        if (tenant.getStatus() != Tenant.TenantStatus.ACTIVE) {
            context.getResponse()
                .withType(RoutingResponse.ResponseType.REJECTED)
                .withStatus(403, "Tenant is not active: " + tenantId);
            return false;
        }
        
        context.setCurrentTenant(tenant);
        
        // Walk through reseller hierarchy from root to current tenant
        List<Tenant> pathFromRoot = tenantHierarchy.getPathFromRoot(tenantId);
        
        System.out.println("Tenant hierarchy path:");
        for (Tenant t : pathFromRoot) {
            System.out.println("  -> " + t.getTenantName() + " (Level: " + t.getLevel() + ")");
            
            // Apply tenant-specific configurations/rules at each level
            applyTenantConfiguration(context, t);
        }
        
        return true; // Continue processing
    }
    
    private void applyTenantConfiguration(RoutingContext context, Tenant tenant) {
        // Apply configurations from this tenant level
        // This could include rate limits, routing rules, permissions, etc.
        context.setAttribute("tenant." + tenant.getLevel() + ".id", tenant.getTenantId());
        context.setAttribute("tenant." + tenant.getLevel() + ".name", tenant.getTenantName());
        
        // In real implementation, load and apply tenant-specific configurations
    }
    
    @Override
    public String getName() {
        return "TenantIdentification";
    }
    
    @Override
    public int getOrder() {
        return 100;
    }
}