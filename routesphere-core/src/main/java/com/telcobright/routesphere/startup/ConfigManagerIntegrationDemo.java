package com.telcobright.routesphere.startup;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.telcobright.routesphere.tenant.TenantHierarchy;
import com.telcobright.routesphere.tenant.TenantMapper;
import com.telcobright.routesphere.tenant.dto.ConfigManagerTenant;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.EntityUtils;

/**
 * Demo class to test ConfigManager integration
 * Run this to test the API call to ConfigManager
 */
public class ConfigManagerIntegrationDemo {
    
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println(" ConfigManager Integration Demo");
        System.out.println("========================================\n");
        
        String configManagerUrl = System.getenv().getOrDefault(
            "CONFIG_MANAGER_URL", "http://localhost:8080");
        String endpoint = "/get-tenant-root";
        
        System.out.println("ConfigManager URL: " + configManagerUrl);
        System.out.println("Endpoint: " + endpoint);
        System.out.println("\nAttempting to fetch tenant hierarchy...\n");
        
        try {
            ConfigManagerTenant rootTenant = fetchTenantHierarchy(configManagerUrl + endpoint);
            
            if (rootTenant != null) {
                System.out.println("✓ Successfully fetched tenant hierarchy!");
                System.out.println("Root tenant DB name: " + rootTenant.getDbName());
                System.out.println("Has parent: " + (rootTenant.getParent() != null));
                System.out.println("Number of children: " + 
                    (rootTenant.getChildren() != null ? rootTenant.getChildren().size() : 0));
                System.out.println("Has profile: " + (rootTenant.getProfile() != null));
                
                // Convert to RouteSphere hierarchy
                System.out.println("\n--- Converting to RouteSphere Hierarchy ---");
                TenantHierarchy hierarchy = TenantMapper.mapToTenantHierarchy(rootTenant);
                
                System.out.println("Total tenants in hierarchy: " + hierarchy.getTenantCount());
                
                // Print the hierarchy
                System.out.println("\n--- Tenant Hierarchy Structure ---");
                hierarchy.printHierarchy();
                
                // Print statistics by level
                System.out.println("\n--- Statistics by Level ---");
                printLevelStatistics(hierarchy);
                
            } else {
                System.out.println("✗ No tenant data received from ConfigManager");
            }
            
        } catch (Exception e) {
            System.err.println("✗ Error fetching tenant hierarchy: " + e.getMessage());
            e.printStackTrace();
            
            System.out.println("\n--- Using Mock Data for Demo ---");
            demonstrateMockData();
        }
    }
    
    private static ConfigManagerTenant fetchTenantHierarchy(String apiUrl) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(apiUrl);
            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setHeader("Accept", "application/json");
            
            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                int statusCode = response.getCode();
                
                if (statusCode == 200) {
                    String responseBody = EntityUtils.toString(response.getEntity());
                    return objectMapper.readValue(responseBody, ConfigManagerTenant.class);
                } else {
                    System.err.println("API returned status code: " + statusCode);
                    return null;
                }
            }
        }
    }
    
    private static void printLevelStatistics(TenantHierarchy hierarchy) {
        for (com.telcobright.routesphere.tenant.Tenant.TenantLevel level : 
             com.telcobright.routesphere.tenant.Tenant.TenantLevel.values()) {
            var tenantsAtLevel = hierarchy.getTenantsByLevel(level);
            if (tenantsAtLevel != null && !tenantsAtLevel.isEmpty()) {
                System.out.printf("  %-20s: %d tenants%n",
                    TenantMapper.getTenantTypeName(level),
                    tenantsAtLevel.size());
            }
        }
    }
    
    private static void demonstrateMockData() {
        // Create mock ConfigManagerTenant structure
        ConfigManagerTenant root = new ConfigManagerTenant("telcobright_root");
        
        // Add Level 1 children
        ConfigManagerTenant reseller1 = new ConfigManagerTenant("reseller_premium");
        reseller1.setParent("telcobright_root");
        root.addChild("reseller_premium", reseller1);
        
        ConfigManagerTenant reseller2 = new ConfigManagerTenant("reseller_standard");
        reseller2.setParent("telcobright_root");
        root.addChild("reseller_standard", reseller2);
        
        // Add Level 2 children
        ConfigManagerTenant subReseller1 = new ConfigManagerTenant("sub_reseller_north");
        subReseller1.setParent("reseller_premium");
        reseller1.addChild("sub_reseller_north", subReseller1);
        
        // Add end users
        ConfigManagerTenant endUser1 = new ConfigManagerTenant("customer_abc");
        endUser1.setParent("sub_reseller_north");
        subReseller1.addChild("customer_abc", endUser1);
        
        ConfigManagerTenant endUser2 = new ConfigManagerTenant("customer_xyz");
        endUser2.setParent("reseller_standard");
        reseller2.addChild("customer_xyz", endUser2);
        
        // Convert to hierarchy
        TenantHierarchy hierarchy = TenantMapper.mapToTenantHierarchy(root);
        
        System.out.println("Mock hierarchy created with " + hierarchy.getTenantCount() + " tenants");
        hierarchy.printHierarchy();
    }
}