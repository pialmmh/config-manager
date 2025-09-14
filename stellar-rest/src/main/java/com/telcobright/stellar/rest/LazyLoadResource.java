package com.telcobright.stellar.rest;

import com.telcobright.stellar.exec.Runner;
import com.telcobright.stellar.json.QueryParser;
import com.telcobright.stellar.model.QueryNode;
import com.telcobright.stellar.result.FlatRow;
import com.telcobright.stellar.schema.NorthwindSchema;
import com.telcobright.stellar.sql.MysqlQueryBuilder;
import com.telcobright.stellar.sql.SqlPlan;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.UUID;

/**
 * Resource for handling lazy loading of query nodes
 */
@Path("/api/lazy")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class LazyLoadResource {
    
    @Inject
    DataSource dataSource;
    
    private final MysqlQueryBuilder queryBuilder;
    
    // Store lazy load metadata for later fetching
    private static final ConcurrentHashMap<String, LazyLoadMetadata> lazyLoadStore = new ConcurrentHashMap<>();
    
    public LazyLoadResource() {
        this.queryBuilder = new MysqlQueryBuilder(NorthwindSchema.northwind());
    }
    
    /**
     * Execute a query and return results with lazy load placeholders
     */
    @POST
    @Path("/query")
    public Response executeQueryWithLazy(Map<String, Object> jsonQuery) {
        try {
            // Parse the query
            QueryNode query = QueryParser.parse(jsonQuery);
            
            // Process lazy nodes and generate keys
            Map<String, Object> lazyPlaceholders = new HashMap<>();
            processLazyNodes(query, lazyPlaceholders, null);
            
            // Build and execute SQL (lazy nodes are skipped)
            SqlPlan plan = queryBuilder.build(query);
            Runner runner = new Runner(dataSource);
            List<FlatRow> rows = runner.execute(plan);
            
            // Convert results
            List<Map<String, Object>> results = new ArrayList<>();
            for (FlatRow row : rows) {
                results.add(row.col());
            }
            
            // Return results with lazy load info
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", results);
            response.put("lazyLoaders", lazyPlaceholders);
            
            return Response.ok(response).build();
            
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(error).build();
        }
    }
    
    /**
     * Load lazy data for a specific key
     */
    @POST
    @Path("/load/{key}")
    public Response loadLazyData(@PathParam("key") String key, Map<String, Object> parentIds) {
        try {
            LazyLoadMetadata metadata = lazyLoadStore.get(key);
            if (metadata == null) {
                return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", "Lazy load key not found")).build();
            }
            
            // Build query with parent constraints
            Map<String, Object> queryJson = metadata.queryTemplate;
            
            // Add parent ID constraints to the criteria
            if (parentIds != null && !parentIds.isEmpty()) {
                Map<String, Object> criteria = (Map<String, Object>) queryJson.computeIfAbsent("criteria", k -> new HashMap<>());
                criteria.putAll(parentIds);
            }
            
            // Parse and execute the query
            QueryNode query = QueryParser.parse(queryJson);
            SqlPlan plan = queryBuilder.build(query);
            Runner runner = new Runner(dataSource);
            List<FlatRow> rows = runner.execute(plan);
            
            // Convert results
            List<Map<String, Object>> results = new ArrayList<>();
            for (FlatRow row : rows) {
                results.add(row.col());
            }
            
            return Response.ok(Map.of(
                "success", true,
                "data", results,
                "lazyKey", key
            )).build();
            
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of("error", e.getMessage())).build();
        }
    }
    
    /**
     * Clear lazy load cache (for maintenance)
     */
    @DELETE
    @Path("/cache/clear")
    public Response clearLazyCache() {
        lazyLoadStore.clear();
        return Response.ok(Map.of("success", true, "message", "Lazy load cache cleared")).build();
    }
    
    /**
     * Get lazy cache statistics
     */
    @GET
    @Path("/cache/stats")
    public Response getLazyCacheStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalKeys", lazyLoadStore.size());
        stats.put("keys", lazyLoadStore.keySet());
        return Response.ok(stats).build();
    }
    
    private void processLazyNodes(QueryNode node, Map<String, Object> lazyPlaceholders, String parentPath) {
        if (node.include == null || node.include.isEmpty()) {
            return;
        }
        
        String currentPath = parentPath == null ? node.kind.name() : parentPath + "." + node.kind.name();
        
        for (QueryNode child : node.include) {
            if (child.lazy) {
                // Generate unique key for this lazy node
                String lazyKey = UUID.randomUUID().toString();
                
                // Store metadata for later fetching
                LazyLoadMetadata metadata = new LazyLoadMetadata();
                metadata.parentKind = node.kind.name();
                metadata.childKind = child.kind.name();
                metadata.queryTemplate = convertNodeToJson(child);
                metadata.path = currentPath + "." + child.kind.name();
                
                lazyLoadStore.put(lazyKey, metadata);
                
                // Add placeholder info
                Map<String, Object> placeholder = new HashMap<>();
                placeholder.put("key", lazyKey);
                placeholder.put("kind", child.kind.name());
                placeholder.put("path", metadata.path);
                placeholder.put("loadUrl", "/api/lazy/load/" + lazyKey);
                
                lazyPlaceholders.put(metadata.path, placeholder);
            } else {
                // Process non-lazy children recursively
                processLazyNodes(child, lazyPlaceholders, currentPath);
            }
        }
    }
    
    private Map<String, Object> convertNodeToJson(QueryNode node) {
        Map<String, Object> json = new HashMap<>();
        json.put("kind", node.kind.name());
        
        if (node.criteria != null) {
            Map<String, Object> criteriaMap = new HashMap<>();
            for (Map.Entry<String, List<Object>> entry : node.criteria.fields().entrySet()) {
                criteriaMap.put(entry.getKey(), entry.getValue());
            }
            json.put("criteria", criteriaMap);
        }
        
        if (node.page != null) {
            json.put("page", Map.of(
                "limit", node.page.limit(),
                "offset", node.page.offset()
            ));
        }
        
        if (node.include != null && !node.include.isEmpty()) {
            List<Map<String, Object>> includeList = new ArrayList<>();
            for (QueryNode child : node.include) {
                includeList.add(convertNodeToJson(child));
            }
            json.put("include", includeList);
        }
        
        return json;
    }
    
    private static class LazyLoadMetadata {
        String parentKind;
        String childKind;
        Map<String, Object> queryTemplate;
        String path;
    }
}