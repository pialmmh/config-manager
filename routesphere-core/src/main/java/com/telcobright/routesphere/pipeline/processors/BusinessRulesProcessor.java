package com.telcobright.routesphere.pipeline.processors;

import com.telcobright.routesphere.pipeline.PipelineProcessor;
import com.telcobright.routesphere.pipeline.RoutingContext;
import com.telcobright.routesphere.pipeline.RoutingResponse;
import com.telcobright.routesphere.tenant.Tenant;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Process business rules for routing decisions
 * Accumulates rules from tenant hierarchy
 */
public class BusinessRulesProcessor implements PipelineProcessor {
    
    @Override
    public boolean process(RoutingContext context) {
        context.moveToStage(RoutingContext.PipelineStage.BUSINESS_RULES);
        
        // Process business rules from tenant hierarchy
        List<BusinessRule> applicableRules = gatherBusinessRules(context);
        
        System.out.println("Processing " + applicableRules.size() + 
            " business rules for tenant: " + context.getCurrentTenant().getTenantName());
        
        // Apply rules in order
        for (BusinessRule rule : applicableRules) {
            if (!applyRule(context, rule)) {
                // Rule failed - stop processing
                context.getResponse()
                    .withType(RoutingResponse.ResponseType.REJECTED)
                    .withStatus(403, "Business rule violation: " + rule.getName());
                return false;
            }
        }
        
        // Store results for routing decision
        context.setBusinessRuleOutput("rules_applied", applicableRules.size());
        context.setBusinessRuleOutput("routing_preference", determineRoutingPreference(context));
        
        return true; // Continue processing
    }
    
    private List<BusinessRule> gatherBusinessRules(RoutingContext context) {
        List<BusinessRule> rules = new ArrayList<>();
        
        // In real implementation, would load from database/config
        // For demo, create sample rules
        
        // Root level rules
        rules.add(new BusinessRule("global-rate-limit", 1000, BusinessRule.RuleType.RATE_LIMIT));
        
        // Reseller level rules
        if (context.getAttribute("tenant.RESELLER_L1.id") != null) {
            rules.add(new BusinessRule("reseller-quota", 5000, BusinessRule.RuleType.QUOTA));
        }
        
        // End user rules
        if (context.getCurrentTenant().getLevel() == Tenant.TenantLevel.END_USER) {
            rules.add(new BusinessRule("user-concurrent-calls", 10, BusinessRule.RuleType.CONCURRENT_LIMIT));
        }
        
        return rules;
    }
    
    private boolean applyRule(RoutingContext context, BusinessRule rule) {
        // Simple rule application logic
        switch (rule.getType()) {
            case RATE_LIMIT:
                // Check rate limit
                return checkRateLimit(context, rule.getValue());
                
            case QUOTA:
                // Check quota
                return checkQuota(context, rule.getValue());
                
            case CONCURRENT_LIMIT:
                // Check concurrent sessions
                return checkConcurrentLimit(context, rule.getValue());
                
            case ROUTING_RULE:
                // Apply routing preference
                context.setBusinessRuleOutput("preferred_route", rule.getName());
                return true;
                
            default:
                return true;
        }
    }
    
    private boolean checkRateLimit(RoutingContext context, int limit) {
        // In real implementation, check against rate counter
        // For demo, always pass
        return true;
    }
    
    private boolean checkQuota(RoutingContext context, int quota) {
        // In real implementation, check against usage counter
        // For demo, always pass
        return true;
    }
    
    private boolean checkConcurrentLimit(RoutingContext context, int limit) {
        // In real implementation, check active sessions
        // For demo, always pass
        return true;
    }
    
    private String determineRoutingPreference(RoutingContext context) {
        // Determine routing based on protocol and tenant preferences
        switch (context.getRequest().getProtocol()) {
            case SIP_UDP:
            case SIP_TCP:
            case SIP_TLS:
                return "sip-routing-engine";
            case HTTP:
            case HTTPS:
                return "http-balancer";
            case ESL:
                return "freeswitch-cluster";
            default:
                return "default-router";
        }
    }
    
    @Override
    public String getName() {
        return "BusinessRules";
    }
    
    @Override
    public int getOrder() {
        return 300;
    }
    
    /**
     * Internal business rule representation
     */
    private static class BusinessRule {
        enum RuleType {
            RATE_LIMIT,
            QUOTA,
            CONCURRENT_LIMIT,
            ROUTING_RULE,
            BLACKLIST,
            WHITELIST
        }
        
        private final String name;
        private final int value;
        private final RuleType type;
        
        public BusinessRule(String name, int value, RuleType type) {
            this.name = name;
            this.value = value;
            this.type = type;
        }
        
        public String getName() { return name; }
        public int getValue() { return value; }
        public RuleType getType() { return type; }
    }
}