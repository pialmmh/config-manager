package com.telcobright.routesphere.startup;

import com.telcobright.routesphere.tenant.Tenant;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST endpoint to demonstrate tenant hierarchy access
 * This would be used in a Quarkus application
 */
@Path("/api/tenants")
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TenantHierarchyResource {
    
    @Inject
    TenantHierarchyService tenantHierarchyService;
    
    /**
     * Get tenant by ID
     */
    @GET
    @Path("/{tenantId}")
    public Response getTenant(@PathParam("tenantId") String tenantId) {
        Tenant tenant = tenantHierarchyService.getTenant(tenantId);
        
        if (tenant == null) {
            return Response.status(Response.Status.NOT_FOUND)
                .entity(Map.of("error", "Tenant not found: " + tenantId))
                .build();
        }
        
        return Response.ok(tenantToMap(tenant)).build();
    }
    
    /**
     * Get tenant hierarchy path from root
     */
    @GET
    @Path("/{tenantId}/path")
    public Response getTenantPath(@PathParam("tenantId") String tenantId) {
        List<Tenant> path = tenantHierarchyService.getPathFromRoot(tenantId);
        
        if (path == null || path.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND)
                .entity(Map.of("error", "Tenant not found: " + tenantId))
                .build();
        }
        
        return Response.ok(path.stream().map(this::tenantToMap).toList()).build();
    }
    
    /**
     * Get tenant ancestors
     */
    @GET
    @Path("/{tenantId}/ancestors")
    public Response getTenantAncestors(@PathParam("tenantId") String tenantId) {
        List<Tenant> ancestors = tenantHierarchyService.getAncestors(tenantId);
        
        if (ancestors == null) {
            return Response.status(Response.Status.NOT_FOUND)
                .entity(Map.of("error", "Tenant not found: " + tenantId))
                .build();
        }
        
        return Response.ok(ancestors.stream().map(this::tenantToMap).toList()).build();
    }
    
    /**
     * Get tenant descendants
     */
    @GET
    @Path("/{tenantId}/descendants")
    public Response getTenantDescendants(@PathParam("tenantId") String tenantId) {
        List<Tenant> descendants = tenantHierarchyService.getDescendants(tenantId);
        
        if (descendants == null) {
            return Response.status(Response.Status.NOT_FOUND)
                .entity(Map.of("error", "Tenant not found: " + tenantId))
                .build();
        }
        
        return Response.ok(descendants.stream().map(this::tenantToMap).toList()).build();
    }
    
    /**
     * Get tenants by level
     */
    @GET
    @Path("/level/{level}")
    public Response getTenantsByLevel(@PathParam("level") String level) {
        try {
            Tenant.TenantLevel tenantLevel = Tenant.TenantLevel.valueOf(level);
            List<Tenant> tenants = tenantHierarchyService.getTenantsByLevel(tenantLevel);
            
            if (tenants == null) {
                return Response.ok(List.of()).build();
            }
            
            return Response.ok(tenants.stream().map(this::tenantToMap).toList()).build();
            
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(Map.of("error", "Invalid tenant level: " + level))
                .build();
        }
    }
    
    /**
     * Get hierarchy statistics
     */
    @GET
    @Path("/stats")
    public Response getHierarchyStats() {
        Map<String, Object> stats = new HashMap<>();
        
        stats.put("total_tenants", tenantHierarchyService.getTenantCount());
        
        // Count by level
        Map<String, Integer> levelCounts = new HashMap<>();
        for (Tenant.TenantLevel level : Tenant.TenantLevel.values()) {
            List<Tenant> tenantsAtLevel = tenantHierarchyService.getTenantsByLevel(level);
            if (tenantsAtLevel != null) {
                levelCounts.put(level.name(), tenantsAtLevel.size());
            }
        }
        stats.put("tenants_by_level", levelCounts);
        
        return Response.ok(stats).build();
    }
    
    /**
     * Update tenant status
     */
    @PUT
    @Path("/{tenantId}/status")
    public Response updateTenantStatus(
            @PathParam("tenantId") String tenantId,
            Map<String, String> request) {
        
        String statusStr = request.get("status");
        if (statusStr == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(Map.of("error", "Status is required"))
                .build();
        }
        
        try {
            Tenant.TenantStatus status = Tenant.TenantStatus.valueOf(statusStr);
            
            if (!tenantHierarchyService.tenantExists(tenantId)) {
                return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", "Tenant not found: " + tenantId))
                    .build();
            }
            
            tenantHierarchyService.updateTenantStatus(tenantId, status);
            
            return Response.ok(Map.of(
                "message", "Status updated successfully",
                "tenantId", tenantId,
                "newStatus", status.name()
            )).build();
            
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(Map.of("error", "Invalid status: " + statusStr))
                .build();
        }
    }
    
    /**
     * Convert tenant to map for JSON response
     */
    private Map<String, Object> tenantToMap(Tenant tenant) {
        Map<String, Object> map = new HashMap<>();
        map.put("tenantId", tenant.getTenantId());
        map.put("tenantName", tenant.getTenantName());
        map.put("level", tenant.getLevel().name());
        map.put("parentTenantId", tenant.getParentTenantId());
        map.put("status", tenant.getStatus().name());
        map.put("properties", tenant.getProperties());
        map.put("childCount", tenant.getChildTenantIds().size());
        return map;
    }
}