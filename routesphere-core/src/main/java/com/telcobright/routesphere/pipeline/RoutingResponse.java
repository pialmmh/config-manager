package com.telcobright.routesphere.pipeline;

import java.util.HashMap;
import java.util.Map;

/**
 * Universal routing response
 */
public class RoutingResponse {
    private String responseId;
    private ResponseType type;
    private int statusCode;
    private String statusMessage;
    private Map<String, Object> headers;
    private Object payload;
    private String stateMachineId;  // If a state machine was created
    private String flowId;          // If a flow was initiated
    
    public enum ResponseType {
        DIRECT_RESPONSE,    // Direct response like REST
        STATE_MACHINE,      // State machine created
        FLOW_INITIATED,     // Flow initiated
        FLOW,               // Flow execution
        FORWARDED,          // Request forwarded
        ROUTE,              // Direct route
        PROXY,              // Proxy to upstream
        REDIRECT,           // Redirect response
        REJECTED,           // Request rejected
        ERROR,              // Error occurred
        SUCCESS,            // Success response
        TIMEOUT             // Timeout occurred
    }
    
    public RoutingResponse() {
        this.responseId = java.util.UUID.randomUUID().toString();
        this.headers = new HashMap<>();
        this.type = ResponseType.DIRECT_RESPONSE;
        this.statusCode = 200;
    }
    
    // Builder-style methods for fluent API
    public RoutingResponse withType(ResponseType type) {
        this.type = type;
        return this;
    }
    
    public RoutingResponse withStatus(int code, String message) {
        this.statusCode = code;
        this.statusMessage = message;
        return this;
    }
    
    public RoutingResponse withPayload(Object payload) {
        this.payload = payload;
        return this;
    }
    
    public RoutingResponse withStateMachine(String stateMachineId) {
        this.type = ResponseType.STATE_MACHINE;
        this.stateMachineId = stateMachineId;
        return this;
    }
    
    public RoutingResponse withFlow(String flowId) {
        this.type = ResponseType.FLOW_INITIATED;
        this.flowId = flowId;
        return this;
    }
    
    // Getters and setters
    public String getResponseId() {
        return responseId;
    }
    
    public void setResponseId(String responseId) {
        this.responseId = responseId;
    }
    
    public ResponseType getType() {
        return type;
    }
    
    public void setType(ResponseType type) {
        this.type = type;
    }
    
    public int getStatusCode() {
        return statusCode;
    }
    
    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }
    
    public String getStatusMessage() {
        return statusMessage;
    }
    
    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
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
    
    public String getStateMachineId() {
        return stateMachineId;
    }
    
    public void setStateMachineId(String stateMachineId) {
        this.stateMachineId = stateMachineId;
    }
    
    public String getFlowId() {
        return flowId;
    }
    
    public void setFlowId(String flowId) {
        this.flowId = flowId;
    }
    
    public RoutingResponse addHeader(String key, Object value) {
        this.headers.put(key, value);
        return this;
    }
}