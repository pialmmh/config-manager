package com.telcobright.routesphere;

import com.telcobright.routesphere.config.RouteSphereConfigLoader;
import com.telcobright.routesphere.config.profiles.SocketProfile;
import com.telcobright.routesphere.pipeline.*;
import com.telcobright.routesphere.pipeline.processors.*;
import com.telcobright.routesphere.protocols.Protocol;
import com.telcobright.routesphere.tenant.TenantHierarchy;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

/**
 * Main RouteSphere Universal Router
 * Multi-tenant, multi-level router for all protocols
 */
public class RouteSphereRouter {
    private final RouteSphereConfigLoader configLoader;
    private final Map<String, RoutingPipeline> protocolPipelines;
    private boolean started = false;
    
    public RouteSphereRouter() {
        this.configLoader = new RouteSphereConfigLoader();
        this.protocolPipelines = new ConcurrentHashMap<>();
    }
    
    /**
     * Start the router with configuration
     */
    public void start(String configFile) throws IOException {
        if (started) {
            System.out.println("Router already started");
            return;
        }
        
        System.out.println("\n========================================");
        System.out.println(" RouteSphere Universal Router Starting");
        System.out.println("========================================\n");
        
        // Load configuration
        configLoader.initialize(configFile);
        
        // Print configuration summary
        configLoader.printConfigurationSummary();
        
        // Initialize pipelines for each protocol
        initializePipelines();
        
        // Start socket listeners (in real implementation)
        startSocketListeners();
        
        started = true;
        
        System.out.println("\n========================================");
        System.out.println(" RouteSphere Router Started Successfully");
        System.out.println("========================================\n");
    }
    
    /**
     * Initialize processing pipelines for each protocol
     */
    private void initializePipelines() {
        TenantHierarchy tenantHierarchy = configLoader.getTenantHierarchy();
        
        // Create pipelines for each protocol
        for (Protocol protocol : Protocol.values()) {
            RoutingPipeline pipeline = new RoutingPipeline(tenantHierarchy);
            
            // Add processors
            pipeline.addProcessor(new TenantIdentificationProcessor(tenantHierarchy));
            pipeline.addProcessor(new AdmissionProcessor());
            pipeline.addProcessor(new BusinessRulesProcessor());
            pipeline.addProcessor(new ActionExecutionProcessor());
            
            protocolPipelines.put(protocol.getName(), pipeline);
            System.out.println("Initialized pipeline for protocol: " + protocol.getName());
        }
    }
    
    /**
     * Start socket listeners for each profile
     */
    private void startSocketListeners() {
        for (SocketProfile profile : configLoader.getAllSocketProfiles()) {
            if (profile.isEnabled()) {
                System.out.println("Starting listener for profile: " + profile);
                // In real implementation, start actual socket listeners here
                // Based on protocol type, start appropriate listener
                // e.g., HTTP server, SIP stack, ESL client, etc.
            }
        }
    }
    
    /**
     * Route a request through the appropriate pipeline
     */
    public RoutingResponse route(RoutingRequest request) {
        if (!started) {
            return new RoutingResponse()
                .withType(RoutingResponse.ResponseType.ERROR)
                .withStatus(503, "Router not started");
        }
        
        // Get pipeline for protocol
        RoutingPipeline pipeline = protocolPipelines.get(request.getProtocol().getName());
        
        if (pipeline == null) {
            return new RoutingResponse()
                .withType(RoutingResponse.ResponseType.ERROR)
                .withStatus(501, "Protocol not supported: " + request.getProtocol());
        }
        
        // Process through pipeline
        return pipeline.process(request);
    }
    
    /**
     * Stop the router
     */
    public void stop() {
        if (!started) {
            return;
        }
        
        System.out.println("\nStopping RouteSphere Router...");
        
        // Stop socket listeners
        // Clean up resources
        
        started = false;
        System.out.println("RouteSphere Router stopped");
    }
    
    /**
     * Example main method for demonstration
     */
    public static void main(String[] args) {
        RouteSphereRouter router = new RouteSphereRouter();
        
        try {
            // Start router
            router.start("routesphere.properties");
            
            // Example: Create and route a SIP request
            System.out.println("\n=== Example SIP Request Routing ===");
            RoutingRequest sipRequest = new RoutingRequest(
                Protocol.SIP_UDP, 
                "192.168.1.100:5060", 
                "192.168.1.200:5060"
            );
            sipRequest.setTenantId("customer1");
            sipRequest.addHeader("Call-ID", "12345@example.com");
            sipRequest.addHeader("From", "sip:alice@example.com");
            sipRequest.addHeader("To", "sip:bob@example.com");
            
            RoutingResponse sipResponse = router.route(sipRequest);
            System.out.println("Response: " + sipResponse.getType() + 
                " - " + sipResponse.getStatusMessage());
            
            // Example: Create and route an HTTP request
            System.out.println("\n=== Example HTTP Request Routing ===");
            RoutingRequest httpRequest = new RoutingRequest(
                Protocol.HTTP,
                "client.example.com",
                "api.routesphere.com"
            );
            httpRequest.setTenantId("reseller1");
            httpRequest.addHeader("Content-Type", "application/json");
            httpRequest.setPayload("{\"action\": \"create_flow\"}");
            
            RoutingResponse httpResponse = router.route(httpRequest);
            System.out.println("Response: " + httpResponse.getType() + 
                " - " + httpResponse.getStatusMessage());
            
        } catch (IOException e) {
            System.err.println("Failed to start router: " + e.getMessage());
        }
    }
}