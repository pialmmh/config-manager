package com.telcobright.routesphere.tenant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Multi-level tenant model supporting reseller hierarchy
 */
public class Tenant {
    private String tenantId;
    private String tenantName;
    private TenantLevel level;
    private String parentTenantId;
    private List<String> childTenantIds;
    private Map<String, Object> attributes;
    private Map<String, String> properties;
    private TenantStatus status;
    
    public enum TenantLevel {
        ROOT(0),        // Root level tenant
        RESELLER_L1(1), // Level 1 reseller
        RESELLER_L2(2), // Level 2 reseller
        RESELLER_L3(3), // Level 3 reseller
        RESELLER_L4(4), // Level 4 reseller
        RESELLER_L5(5), // Level 5 reseller
        END_USER(10);   // End user/customer
        
        private final int level;
        
        TenantLevel(int level) {
            this.level = level;
        }
        
        public int getLevel() {
            return level;
        }
    }
    
    public enum TenantStatus {
        ACTIVE,
        SUSPENDED,
        INACTIVE,
        PENDING
    }
    
    public Tenant() {
        this.childTenantIds = new ArrayList<>();
        this.attributes = new HashMap<>();
        this.properties = new HashMap<>();
        this.status = TenantStatus.PENDING;
    }
    
    public Tenant(String tenantId, String tenantName, TenantLevel level) {
        this();
        this.tenantId = tenantId;
        this.tenantName = tenantName;
        this.level = level;
    }
    
    // Tree navigation methods
    public boolean isRoot() {
        return level == TenantLevel.ROOT;
    }
    
    public boolean isReseller() {
        return level.getLevel() >= 1 && level.getLevel() <= 5;
    }
    
    public boolean isEndUser() {
        return level == TenantLevel.END_USER;
    }
    
    public void addChildTenant(String childTenantId) {
        if (!childTenantIds.contains(childTenantId)) {
            childTenantIds.add(childTenantId);
        }
    }
    
    // Getters and setters
    public String getTenantId() {
        return tenantId;
    }
    
    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }
    
    public String getTenantName() {
        return tenantName;
    }
    
    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }
    
    public TenantLevel getLevel() {
        return level;
    }
    
    public void setLevel(TenantLevel level) {
        this.level = level;
    }
    
    public String getParentTenantId() {
        return parentTenantId;
    }
    
    public void setParentTenantId(String parentTenantId) {
        this.parentTenantId = parentTenantId;
    }
    
    public List<String> getChildTenantIds() {
        return childTenantIds;
    }
    
    public void setChildTenantIds(List<String> childTenantIds) {
        this.childTenantIds = childTenantIds;
    }
    
    public Map<String, Object> getAttributes() {
        return attributes;
    }
    
    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }
    
    public TenantStatus getStatus() {
        return status;
    }
    
    public void setStatus(TenantStatus status) {
        this.status = status;
    }
    
    public Map<String, String> getProperties() {
        return properties;
    }
    
    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }
    
    public void addProperty(String key, String value) {
        this.properties.put(key, value);
    }
    
    public String getProperty(String key) {
        return this.properties.get(key);
    }
    
    @Override
    public String toString() {
        return String.format("Tenant[%s:%s, Level:%s, Parent:%s, Children:%d]", 
            tenantId, tenantName, level, parentTenantId, childTenantIds.size());
    }
}