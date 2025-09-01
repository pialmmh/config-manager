package com.telcobright.routesphere.tenant.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import java.util.Map;

/**
 * DTO class for AllCache from ConfigManager
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AllCacheDTO {
    private Map<Long, List<Object>> partnerIdWisePackageAccounts;
    
    // Default constructor for JSON deserialization
    public AllCacheDTO() {
    }
    
    // Getters and Setters
    public Map<Long, List<Object>> getPartnerIdWisePackageAccounts() {
        return partnerIdWisePackageAccounts;
    }
    
    public void setPartnerIdWisePackageAccounts(Map<Long, List<Object>> partnerIdWisePackageAccounts) {
        this.partnerIdWisePackageAccounts = partnerIdWisePackageAccounts;
    }
    
    @Override
    public String toString() {
        return "AllCacheDTO{" +
            "packageAccountsCount=" + (partnerIdWisePackageAccounts != null ? partnerIdWisePackageAccounts.size() : 0) +
            '}';
    }
}