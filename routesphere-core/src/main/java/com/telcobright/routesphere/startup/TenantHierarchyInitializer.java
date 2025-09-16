package com.telcobright.routesphere.startup;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.telcobright.rtc.domainmodel.nonentity.Tenant;
import com.telcobright.routesphere.config.GlobalConfigService;
import com.telcobright.routesphere.config.deployment.DeploymentConfigService;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.EntityUtils;

import java.io.IOException;

/**
 * Simple initializer to fetch Tenant from ConfigManager API
 */
@ApplicationScoped
public class TenantHierarchyInitializer {

    @Inject
    GlobalConfigService globalConfig;

    @Inject
    DeploymentConfigService deploymentConfig;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .findAndRegisterModules(); // Register JSR310 module for Java 8 date/time
    private Tenant rootTenant;

    // API endpoint path
    private static final String TENANT_API_ENDPOINT = "/get-tenant-root";

    /**
     * Initialize by fetching tenant from ConfigManager
     */
    public void onStart(@Observes StartupEvent event) {
        System.out.println("\n========================================");
        System.out.println(" Fetching Tenant from ConfigManager");
        System.out.println("========================================\n");

        String activeProfile = globalConfig.getActiveProfile();

        if (!"mock".equals(activeProfile)) {
            try {
                rootTenant = loadFromConfigManager();
                if (rootTenant != null) {
                    System.out.println("Successfully received Tenant: " + rootTenant.getDbName());
                }
            } catch (Exception e) {
                System.err.println("Failed to load from ConfigManager: " + e.getMessage());
            }
        } else {
            System.out.println("Mock profile - skipping ConfigManager fetch");
        }
    }

    /**
     * Load tenant from ConfigManager API
     */
    private Tenant loadFromConfigManager() throws IOException {
        String configManagerUrl = deploymentConfig.getConfigManagerUrl();
        String apiUrl = configManagerUrl + TENANT_API_ENDPOINT;

        System.out.println("Loading tenant from: " + apiUrl);

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(apiUrl);
            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setHeader("Accept", "application/json");

            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                int statusCode = response.getCode();
                System.out.println("API Response Status Code: " + statusCode);

                if (statusCode == 200) {
                    String responseBody = EntityUtils.toString(response.getEntity());
                    System.out.println("API Response Body (first 500 chars): " +
                        (responseBody.length() > 500 ? responseBody.substring(0, 500) + "..." : responseBody));

                    // Parse JSON response directly to Tenant from rtc.domainmodel.nonentity
                    Tenant tenant = objectMapper.readValue(responseBody, Tenant.class);
                    System.out.println("Successfully parsed Tenant object: " + tenant.getDbName());
                    System.out.println("Tenant has " + (tenant.getChildren() != null ? tenant.getChildren().size() : 0) + " children");
                    return tenant;
                } else {
                    System.err.println("ConfigManager API returned status: " + statusCode);
                    if (response.getEntity() != null) {
                        String errorBody = EntityUtils.toString(response.getEntity());
                        System.err.println("Error response body: " + errorBody);
                    }
                    return null;
                }
            } catch (Exception e) {
                System.err.println("Exception during API call: " + e.getMessage());
                e.printStackTrace();
                throw new IOException("Failed to fetch from ConfigManager: " + e.getMessage(), e);
            }
        }
    }

    public Tenant getRootTenant() {
        return rootTenant;
    }
}