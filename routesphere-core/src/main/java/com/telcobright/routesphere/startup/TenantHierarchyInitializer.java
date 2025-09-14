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

    private final ObjectMapper objectMapper = new ObjectMapper();
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

                if (statusCode == 200) {
                    String responseBody = EntityUtils.toString(response.getEntity());
                    // Parse JSON response directly to Tenant from rtc.domainmodel.nonentity
                    return objectMapper.readValue(responseBody, Tenant.class);
                } else {
                    System.err.println("ConfigManager API returned status: " + statusCode);
                    return null;
                }
            } catch (Exception e) {
                throw new IOException("Failed to fetch from ConfigManager: " + e.getMessage(), e);
            }
        }
    }

    public Tenant getRootTenant() {
        return rootTenant;
    }
}