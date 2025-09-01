package com.telcobright.routesphere.pipeline;

import com.telcobright.routesphere.tenant.Tenant;
import java.util.HashMap;
import java.util.Map;

/**
 * Context that flows through the processing pipeline
 */
public class RoutingContext {
    private RoutingRequest request;
    private RoutingResponse response;
    private Tenant currentTenant;
    private Map<String, Object> attributes;
    private PipelineStage currentStage;
    private boolean authenticated;
    private boolean authorized;
    private Map<String, Object> businessRuleOutputs;
    
    public enum PipelineStage {
        RECEIVED,
        TENANT_IDENTIFICATION,
        ADMISSION_AUTH,
        ADMISSION_AUTHZ,
        BUSINESS_RULES,
        ROUTING_DECISION,
        ACTION_EXECUTION,
        RESPONSE_GENERATION,
        COMPLETED,
        FAILED
    }
    
    public RoutingContext(RoutingRequest request) {
        this.request = request;
        this.response = new RoutingResponse();
        this.attributes = new HashMap<>();
        this.businessRuleOutputs = new HashMap<>();
        this.currentStage = PipelineStage.RECEIVED;
        this.authenticated = false;
        this.authorized = false;
    }
    
    public void addBusinessRuleOutput(String ruleName, Object output) {
        businessRuleOutputs.put(ruleName, output);
    }
    
    public void setBusinessRuleOutput(String ruleName, Object output) {
        businessRuleOutputs.put(ruleName, output);
    }
    
    public Object getBusinessRuleOutput(String ruleName) {
        return businessRuleOutputs.get(ruleName);
    }
    
    public void setAttribute(String key, Object value) {
        attributes.put(key, value);
    }
    
    public Object getAttribute(String key) {
        return attributes.get(key);
    }
    
    // Stage transition methods
    public void moveToStage(PipelineStage stage) {
        this.currentStage = stage;
    }
    
    public boolean isInStage(PipelineStage stage) {
        return this.currentStage == stage;
    }
    
    // Getters and setters
    public RoutingRequest getRequest() {
        return request;
    }
    
    public void setRequest(RoutingRequest request) {
        this.request = request;
    }
    
    public RoutingResponse getResponse() {
        return response;
    }
    
    public void setResponse(RoutingResponse response) {
        this.response = response;
    }
    
    public Tenant getCurrentTenant() {
        return currentTenant;
    }
    
    public void setCurrentTenant(Tenant currentTenant) {
        this.currentTenant = currentTenant;
    }
    
    public Map<String, Object> getAttributes() {
        return attributes;
    }
    
    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }
    
    public PipelineStage getCurrentStage() {
        return currentStage;
    }
    
    public void setCurrentStage(PipelineStage currentStage) {
        this.currentStage = currentStage;
    }
    
    public boolean isAuthenticated() {
        return authenticated;
    }
    
    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }
    
    public boolean isAuthorized() {
        return authorized;
    }
    
    public void setAuthorized(boolean authorized) {
        this.authorized = authorized;
    }
    
    public Map<String, Object> getBusinessRuleOutputs() {
        return businessRuleOutputs;
    }
    
    public void setBusinessRuleOutputs(Map<String, Object> businessRuleOutputs) {
        this.businessRuleOutputs = businessRuleOutputs;
    }
}