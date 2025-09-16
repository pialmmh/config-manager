package com.telcobright.routesphere.protocols.http;

import com.telcobright.routesphere.protocols.base.ServerChannel;
import com.telcobright.routesphere.protocols.base.ChannelConfig;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Map;

/**
 * HTTP Channel implementation.
 * Creates an HTTP server to receive REST API requests.
 */
@ApplicationScoped
public class HttpChannel extends ServerChannel {

    @Inject
    Vertx vertx;

    private HttpServer httpServer;
    private String contextPath = "/api";
    private List<String> allowedMethods;
    private List<String> endpoints;

    public HttpChannel() {
        super("http-default", "http", new ChannelConfig());
    }

    public HttpChannel(String name, ChannelConfig config) {
        super(name, "http", config);

        // Extract HTTP-specific configuration
        if (config.getConnectionConfig() != null) {
            Object path = config.getConnectionConfig().get("context-path");
            if (path != null) {
                this.contextPath = path.toString();
            }
        }

        if (config.getProtocolSpecificConfig() != null) {
            Map<String, Object> http = (Map<String, Object>) config.getProtocolSpecificConfig().get("http");
            if (http != null) {
                this.allowedMethods = (List<String>) http.get("methods");
                this.endpoints = (List<String>) http.get("endpoints");
            }
        }
    }

    @Override
    protected void startListener() throws Exception {
        LOG.infof("Starting HTTP server on %s:%d with context path %s",
            listenHost, listenPort, contextPath);

        Router router = Router.router(vertx);

        // Configure routes based on configuration
        if (endpoints != null) {
            for (String endpoint : endpoints) {
                String fullPath = contextPath + endpoint;

                // Add handlers for configured methods
                if (allowedMethods != null) {
                    for (String method : allowedMethods) {
                        switch (method.toUpperCase()) {
                            case "GET":
                                router.get(fullPath).handler(ctx -> handleRequest(ctx, "GET", endpoint));
                                break;
                            case "POST":
                                router.post(fullPath).handler(ctx -> handleRequest(ctx, "POST", endpoint));
                                break;
                            case "PUT":
                                router.put(fullPath).handler(ctx -> handleRequest(ctx, "PUT", endpoint));
                                break;
                            case "DELETE":
                                router.delete(fullPath).handler(ctx -> handleRequest(ctx, "DELETE", endpoint));
                                break;
                        }
                    }
                }

                LOG.debugf("Configured endpoint: %s for methods %s", fullPath, allowedMethods);
            }
        }

        // Create and start HTTP server
        httpServer = vertx.createHttpServer();
        httpServer.requestHandler(router)
            .listen(listenPort, listenHost)
            .onSuccess(server -> {
                LOG.infof("HTTP server started successfully on port %d", server.actualPort());
            })
            .onFailure(cause -> {
                LOG.errorf("Failed to start HTTP server: %s", cause.getMessage());
            });
    }

    @Override
    protected void stopListener() throws Exception {
        if (httpServer != null) {
            LOG.info("Stopping HTTP server");
            httpServer.close();
            httpServer = null;
        }
    }

    @Override
    protected void handleIncomingConnection(Object connection) {
        // Not used for HTTP - handling is done in route handlers
    }

    /**
     * Handle incoming HTTP request
     */
    private void handleRequest(io.vertx.ext.web.RoutingContext context, String method, String endpoint) {
        LOG.debugf("Received %s request on %s", method, endpoint);

        // Convert HTTP request to pipeline event
        HttpEvent event = HttpEvent.builder()
            .method(method)
            .path(endpoint)
            .headers(context.request().headers().entries())
            .queryParams(context.queryParams().entries())
            .body(context.body() != null ? context.body().toString() : null)
            .remoteAddress(context.request().remoteAddress().toString())
            .build();

        Map<String, Object> pipelineEvent = Map.of(
            "type", "http",
            "method", method,
            "path", endpoint,
            "timestamp", System.currentTimeMillis(),
            "data", event
        );

        // Process event through pipeline
        processEvent(pipelineEvent);

        // Send response
        // TODO: Get response from pipeline processing
        context.response()
            .setStatusCode(200)
            .putHeader("Content-Type", "application/json")
            .end("{\"status\":\"received\"}");
    }

    // Getters
    public String getContextPath() {
        return contextPath;
    }

    public List<String> getAllowedMethods() {
        return allowedMethods;
    }

    public List<String> getEndpoints() {
        return endpoints;
    }

    public boolean isRunning() {
        return httpServer != null;
    }
}