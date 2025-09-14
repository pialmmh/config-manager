package com.telcobright.routesphere.configmanager.resource;

import com.telcobright.rtc.domainmodel.nonentity.Tenant;
import com.telcobright.routesphere.configmanager.model.GlobalTenantRegistry;
import com.telcobright.routesphere.configmanager.service.ConfigManagerService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;

/**
 * REST endpoints for configuration management
 * Provides API for fetching tenant configuration
 */
@Path("/api/config")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ConfigManagerResource {
    
    private static final Logger LOG = Logger.getLogger(ConfigManagerResource.class);
    
    @Inject
    ConfigManagerService configManagerService;
    
    /**
     * Get root tenant with full hierarchy
     * Endpoint: POST /api/config/get-tenant-root
     * Matches original ConfigManager endpoint
     */
    @POST
    @Path("/get-tenant-root")
    public Response getTenantRoot() {
        try {
            Tenant rootTenant = configManagerService.getRootTenant();
            if (rootTenant != null) {
                LOG.infof("Returning root tenant: %s", rootTenant.getDbName());
                return Response.ok(rootTenant).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"Root tenant not found\"}")
                    .build();
            }
        } catch (Exception e) {
            LOG.errorf("Error fetching root tenant: %s", e.getMessage());
            return Response.serverError()
                .entity("{\"error\": \"" + e.getMessage() + "\"}")
                .build();
        }
    }
    
    /**
     * Get global tenant registry
     * Endpoint: POST /api/config/get-global-tenant-registry
     * Matches original ConfigManager endpoint
     */
    @POST
    @Path("/get-global-tenant-registry")
    public Response getGlobalTenantRegistry() {
        try {
            GlobalTenantRegistry registry = configManagerService.getRegistry();
            LOG.infof("Returning registry with %d tenants", registry.getTenantCount());
            return Response.ok(registry).build();
        } catch (Exception e) {
            LOG.errorf("Error fetching registry: %s", e.getMessage());
            return Response.serverError()
                .entity("{\"error\": \"" + e.getMessage() + "\"}")
                .build();
        }
    }
    
    /**
     * Get specific tenant by database name
     * Endpoint: GET /api/config/tenant/{dbName}
     */
    @GET
    @Path("/tenant/{dbName}")
    public Response getTenantByDbName(@PathParam("dbName") String dbName) {
        try {
            Tenant tenant = configManagerService.getTenantByDbName(dbName);
            if (tenant != null) {
                LOG.infof("Returning tenant: %s", dbName);
                return Response.ok(tenant).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"Tenant not found: " + dbName + "\"}")
                    .build();
            }
        } catch (Exception e) {
            LOG.errorf("Error fetching tenant %s: %s", dbName, e.getMessage());
            return Response.serverError()
                .entity("{\"error\": \"" + e.getMessage() + "\"}")
                .build();
        }
    }
    
    /**
     * Reload configuration manually
     * Endpoint: POST /api/config/reload
     */
    @POST
    @Path("/reload")
    public Response reloadConfiguration() {
        try {
            LOG.info("Manual configuration reload requested");
            configManagerService.reloadConfiguration();
            return Response.ok("{\"status\": \"Configuration reloaded successfully\"}").build();
        } catch (Exception e) {
            LOG.errorf("Error reloading configuration: %s", e.getMessage());
            return Response.serverError()
                .entity("{\"error\": \"" + e.getMessage() + "\"}")
                .build();
        }
    }
    
    /**
     * Health check endpoint
     * Endpoint: GET /api/config/health
     */
    @GET
    @Path("/health")
    public Response health() {
        try {
            Tenant root = configManagerService.getRootTenant();
            GlobalTenantRegistry registry = configManagerService.getRegistry();
            
            return Response.ok()
                .entity("{" +
                    "\"status\": \"UP\"," +
                    "\"rootTenant\": \"" + (root != null ? root.getDbName() : "none") + "\"," +
                    "\"totalTenants\": " + registry.getTenantCount() +
                    "}")
                .build();
        } catch (Exception e) {
            return Response.serverError()
                .entity("{\"status\": \"DOWN\", \"error\": \"" + e.getMessage() + "\"}")
                .build();
        }
    }
    
    /**
     * Get tenant hierarchy statistics
     * Endpoint: GET /api/config/stats
     */
    @GET
    @Path("/stats")
    public Response getStatistics() {
        try {
            GlobalTenantRegistry registry = configManagerService.getRegistry();
            Tenant root = configManagerService.getRootTenant();
            
            int rootCount = 0;
            int resellerCount = 0;
            int endUserCount = 0;
            
            for (Tenant tenant : registry.getAllTenants().values()) {
                // Determine tenant type based on parent hierarchy
                if (tenant.getParent() == null) {
                    rootCount++;
                } else {
                    // Count hierarchy depth to determine if reseller or end user
                    int depth = getHierarchyDepth(tenant, registry);
                    if (depth <= 5) {
                        resellerCount++;
                    } else {
                        endUserCount++;
                    }
                }
            }
            
            return Response.ok()
                .entity("{" +
                    "\"totalTenants\": " + registry.getTenantCount() + "," +
                    "\"rootTenants\": " + rootCount + "," +
                    "\"resellers\": " + resellerCount + "," +
                    "\"endUsers\": " + endUserCount + "," +
                    "\"activeTenant\": \"" + (root != null ? root.getDbName() : "none") + "\"" +
                    "}")
                .build();

        } catch (Exception e) {
            LOG.errorf("Error fetching statistics: %s", e.getMessage());
            return Response.serverError()
                .entity("{\"error\": \"" + e.getMessage() + "\"}")
                .build();
        }
    }

    /**
     * Helper method to calculate tenant hierarchy depth
     */
    private int getHierarchyDepth(Tenant tenant, GlobalTenantRegistry registry) {
        int depth = 0;
        String parent = tenant.getParent();
        while (parent != null && depth < 10) { // Prevent infinite loops
            Tenant parentTenant = registry.getTenantByDbName(parent);
            if (parentTenant == null) break;
            parent = parentTenant.getParent();
            depth++;
        }
        return depth;
    }
}