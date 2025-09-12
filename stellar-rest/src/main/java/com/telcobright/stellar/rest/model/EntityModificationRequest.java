package com.telcobright.stellar.rest.model;

import java.util.List;
import java.util.Map;

/**
 * Request model for entity modifications
 */
public class EntityModificationRequest {
    private String entityName;
    private ModifyOperation operation;
    private Map<String, Object> data;
    private Map<String, Object> criteria; // For UPDATE/DELETE
    private List<EntityModificationRequest> include; // Nested modifications
    
    // Getters and setters
    public String getEntityName() {
        return entityName;
    }
    
    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }
    
    public ModifyOperation getOperation() {
        return operation;
    }
    
    public void setOperation(ModifyOperation operation) {
        this.operation = operation;
    }
    
    public Map<String, Object> getData() {
        return data;
    }
    
    public void setData(Map<String, Object> data) {
        this.data = data;
    }
    
    public Map<String, Object> getCriteria() {
        return criteria;
    }
    
    public void setCriteria(Map<String, Object> criteria) {
        this.criteria = criteria;
    }
    
    public List<EntityModificationRequest> getInclude() {
        return include;
    }
    
    public void setInclude(List<EntityModificationRequest> include) {
        this.include = include;
    }
}