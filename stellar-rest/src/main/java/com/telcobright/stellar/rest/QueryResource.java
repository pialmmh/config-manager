package com.telcobright.stellar.rest;

import com.telcobright.stellar.exec.Runner;
import com.telcobright.stellar.json.QueryParser;
import com.telcobright.stellar.model.QueryNode;
import com.telcobright.stellar.result.FlatRow;
import com.telcobright.stellar.schema.SampleSchemas;
import com.telcobright.stellar.schema.NorthwindSchema;
import com.telcobright.stellar.sql.MysqlQueryBuilder;
import com.telcobright.stellar.sql.SqlPlan;
import com.telcobright.stellar.rest.model.EntityModificationRequest;
import com.telcobright.stellar.rest.service.EntityModificationService;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Logger;
import java.io.StringWriter;
import java.io.PrintWriter;

@Path("/api")
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class QueryResource {
    
    private static final Logger LOG = Logger.getLogger(QueryResource.class.getName());
    
    @Inject
    DataSource dataSource;
    
    @Inject
    EntityModificationService modificationService;
    
    private final MysqlQueryBuilder queryBuilder;
    
    public QueryResource() {
        // Initialize with Northwind schema
        this.queryBuilder = new MysqlQueryBuilder(NorthwindSchema.northwind());
    }
    
    /**
     * Main query endpoint that accepts JSON queries from the frontend
     */
    @POST
    @Path("/query")
    public Response executeQuery(Map<String, Object> jsonQuery) {
        LOG.info("Received query: " + jsonQuery);
        
        try {
            // Parse JSON to QueryNode
            QueryNode query = QueryParser.parse(jsonQuery);
            LOG.info("Parsed query for kind: " + query.kind);
            
            // Build SQL
            SqlPlan plan = queryBuilder.build(query);
            LOG.info("Generated SQL: " + plan.sql());
            LOG.info("Parameters: " + plan.params());
            
            // Execute query
            Runner runner = new Runner(dataSource);
            List<FlatRow> rows = runner.execute(plan);
            
            // Convert FlatRow results to plain Maps for JSON serialization
            List<Map<String, Object>> results = new ArrayList<>();
            for (FlatRow row : rows) {
                results.add(row.col());
            }
            
            LOG.info("Query returned " + results.size() + " rows");
            
            // Return results
            return Response.ok(Map.of(
                "success", true,
                "data", results,
                "count", results.size()
            )).build();
            
        } catch (IllegalArgumentException e) {
            LOG.warning("Bad request: " + e.getMessage());
            String stackTrace = getStackTraceAsString(e);
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(Map.of(
                    "success", false,
                    "error", e.getMessage(),
                    "exception", e.getClass().getName(),
                    "stackTrace", stackTrace
                ))
                .build();
                
        } catch (SQLException e) {
            LOG.severe("Database error: " + e.getMessage());
            String stackTrace = getStackTraceAsString(e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of(
                    "success", false,
                    "error", "Database error: " + e.getMessage(),
                    "sqlState", e.getSQLState(),
                    "errorCode", e.getErrorCode(),
                    "exception", e.getClass().getName(),
                    "stackTrace", stackTrace
                ))
                .build();
                
        } catch (Exception e) {
            LOG.severe("Unexpected error: " + e.getMessage());
            e.printStackTrace();
            String stackTrace = getStackTraceAsString(e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of(
                    "success", false,
                    "error", "Internal server error: " + e.getMessage(),
                    "exception", e.getClass().getName(),
                    "stackTrace", stackTrace
                ))
                .build();
        }
    }
    
    /**
     * Health check endpoint
     */
    @GET
    @Path("/health")
    public Response health() {
        return Response.ok(Map.of(
            "status", "healthy",
            "service", "stellar-rest",
            "timestamp", new Date()
        )).build();
    }
    
    /**
     * Endpoint to fetch specific entity by kind (for compatibility with frontend)
     */
    @POST
    @Path("/{kind}")
    public Response executeQueryByKind(@PathParam("kind") String kind, Map<String, Object> jsonQuery) {
        // Add the kind to the query if not present
        if (!jsonQuery.containsKey("kind")) {
            jsonQuery.put("kind", kind);
        }
        return executeQuery(jsonQuery);
    }
    
    /**
     * Generic entity modification endpoint with lazy hierarchy building and caching
     * Supports INSERT, UPDATE, DELETE operations with nested entities in a single transaction
     */
    @POST
    @Path("/modify")
    public Response modifyEntity(EntityModificationRequest request) {
        LOG.info("Received modification request for entity: " + request.getEntityName() + 
                 " with operation: " + request.getOperation());
        
        try {
            // Process modification with lazy hierarchy building
            Map<String, Object> result = modificationService.processModification(request);
            
            LOG.info("Modification completed successfully");
            return Response.ok(result).build();
            
        } catch (IllegalArgumentException e) {
            LOG.warning("Bad modification request: " + e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(Map.of(
                    "success", false,
                    "error", e.getMessage(),
                    "exception", e.getClass().getName()
                ))
                .build();
                
        } catch (SQLException e) {
            LOG.severe("Database error during modification: " + e.getMessage());
            String stackTrace = getStackTraceAsString(e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of(
                    "success", false,
                    "error", "Database error: " + e.getMessage(),
                    "sqlState", e.getSQLState() != null ? e.getSQLState() : "",
                    "errorCode", e.getErrorCode(),
                    "exception", e.getClass().getName(),
                    "stackTrace", stackTrace
                ))
                .build();
                
        } catch (Exception e) {
            LOG.severe("Unexpected error during modification: " + e.getMessage());
            e.printStackTrace();
            String stackTrace = getStackTraceAsString(e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of(
                    "success", false,
                    "error", "Internal server error: " + e.getMessage(),
                    "exception", e.getClass().getName(),
                    "stackTrace", stackTrace
                ))
                .build();
        }
    }
    
    /**
     * Get cache statistics for entity hierarchies
     */
    @GET
    @Path("/cache/stats")
    public Response getCacheStatistics() {
        Map<String, Object> stats = modificationService.getCacheStatistics();
        return Response.ok(stats).build();
    }
    
    /**
     * Clear entity hierarchy cache (for testing/maintenance)
     */
    @DELETE
    @Path("/cache/clear")
    public Response clearCache() {
        modificationService.clearCache();
        return Response.ok(Map.of(
            "success", true,
            "message", "Entity hierarchy cache cleared"
        )).build();
    }
    
    /**
     * Helper method to get full stack trace as string
     */
    private String getStackTraceAsString(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }
}