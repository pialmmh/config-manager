package com.telcobright.routesphere.tenant.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import java.util.Map;

/**
 * DTO class for DynamicContext from ConfigManager
 * Only includes fields that might be useful for RouteSphere
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DynamicContextDTO {
    // Key fields that might be useful for routing
    private Map<Integer, Object> partners;
    private Map<String, Object> prefixWisePartners;
    private Map<String, Object> sipAccountWisePartners;
    private Map<String, Object> didNumbVsPartners;
    private Map<Integer, Object> ratePlans;
    private List<Object> rateAssignsCustomer;
    private List<Object> rateAssignsSupplier;
    
    // Default constructor for JSON deserialization
    public DynamicContextDTO() {
    }
    
    // Getters and Setters
    public Map<Integer, Object> getPartners() {
        return partners;
    }
    
    public void setPartners(Map<Integer, Object> partners) {
        this.partners = partners;
    }
    
    public Map<String, Object> getPrefixWisePartners() {
        return prefixWisePartners;
    }
    
    public void setPrefixWisePartners(Map<String, Object> prefixWisePartners) {
        this.prefixWisePartners = prefixWisePartners;
    }
    
    public Map<String, Object> getSipAccountWisePartners() {
        return sipAccountWisePartners;
    }
    
    public void setSipAccountWisePartners(Map<String, Object> sipAccountWisePartners) {
        this.sipAccountWisePartners = sipAccountWisePartners;
    }
    
    public Map<String, Object> getDidNumbVsPartners() {
        return didNumbVsPartners;
    }
    
    public void setDidNumbVsPartners(Map<String, Object> didNumbVsPartners) {
        this.didNumbVsPartners = didNumbVsPartners;
    }
    
    public Map<Integer, Object> getRatePlans() {
        return ratePlans;
    }
    
    public void setRatePlans(Map<Integer, Object> ratePlans) {
        this.ratePlans = ratePlans;
    }
    
    public List<Object> getRateAssignsCustomer() {
        return rateAssignsCustomer;
    }
    
    public void setRateAssignsCustomer(List<Object> rateAssignsCustomer) {
        this.rateAssignsCustomer = rateAssignsCustomer;
    }
    
    public List<Object> getRateAssignsSupplier() {
        return rateAssignsSupplier;
    }
    
    public void setRateAssignsSupplier(List<Object> rateAssignsSupplier) {
        this.rateAssignsSupplier = rateAssignsSupplier;
    }
    
    @Override
    public String toString() {
        return "DynamicContextDTO{" +
            "partnersCount=" + (partners != null ? partners.size() : 0) +
            ", prefixWisePartnersCount=" + (prefixWisePartners != null ? prefixWisePartners.size() : 0) +
            ", ratePlansCount=" + (ratePlans != null ? ratePlans.size() : 0) +
            '}';
    }
}