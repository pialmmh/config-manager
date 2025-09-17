package com.telcobright.routesphere.rules.api;

import java.util.HashMap;
import java.util.Map;

/**
 * Context that flows through the pipeline
 */
public class PipelineContext {

    private final String tenantId;
    private final Map<String, Object> data = new HashMap<>();
    private final Map<String, Object> metadata = new HashMap<>();

    public PipelineContext(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setData(String key, Object value) {
        data.put(key, value);
    }

    public <T> T getData(String key) {
        return (T) data.get(key);
    }

    public Map<String, Object> getAllData() {
        return new HashMap<>(data);
    }

    public void setMetadata(String key, Object value) {
        metadata.put(key, value);
    }

    public <T> T getMetadata(String key) {
        return (T) metadata.get(key);
    }

    public String getHeader(String name) {
        Map<String, String> headers = getData("headers");
        return headers != null ? headers.get(name) : null;
    }
}