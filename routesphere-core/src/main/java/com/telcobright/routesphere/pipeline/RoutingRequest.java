package com.telcobright.routesphere.pipeline;

import com.telcobright.routesphere.protocols.Protocol;
import java.util.HashMap;
import java.util.Map;

/**
 * Universal routing request that can represent any protocol request
 */
public class RoutingRequest {
    private String requestId;
    private Protocol protocol;
    private String sourceAddress;
    private String destinationAddress;
    private String tenantId;
    private Map<String, Object> headers;
    private Object payload;
    private long timestamp;
    private String profileName;  // Socket profile that received this request
    
    public RoutingRequest() {
        this.requestId = java.util.UUID.randomUUID().toString();
        this.headers = new HashMap<>();
        this.timestamp = System.currentTimeMillis();
    }
    
    public RoutingRequest(Protocol protocol, String sourceAddress, String destinationAddress) {
        this();
        this.protocol = protocol;
        this.sourceAddress = sourceAddress;
        this.destinationAddress = destinationAddress;
    }
    
    // Getters and setters
    public String getRequestId() {
        return requestId;
    }
    
    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
    
    public Protocol getProtocol() {
        return protocol;
    }
    
    public void setProtocol(Protocol protocol) {
        this.protocol = protocol;
    }
    
    public String getSourceAddress() {
        return sourceAddress;
    }
    
    public void setSourceAddress(String sourceAddress) {
        this.sourceAddress = sourceAddress;
    }
    
    public String getDestinationAddress() {
        return destinationAddress;
    }
    
    public void setDestinationAddress(String destinationAddress) {
        this.destinationAddress = destinationAddress;
    }
    
    public String getTenantId() {
        return tenantId;
    }
    
    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }
    
    public Map<String, Object> getHeaders() {
        return headers;
    }
    
    public void setHeaders(Map<String, Object> headers) {
        this.headers = headers;
    }
    
    public Object getPayload() {
        return payload;
    }
    
    public void setPayload(Object payload) {
        this.payload = payload;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    
    public String getProfileName() {
        return profileName;
    }
    
    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }
    
    public void addHeader(String key, Object value) {
        this.headers.put(key, value);
    }
    
    @Override
    public String toString() {
        return String.format("RoutingRequest[%s: %s from %s to %s, tenant:%s]", 
            requestId, protocol, sourceAddress, destinationAddress, tenantId);
    }
}