package com.telcobright.routesphere.rules.api;

import java.util.Map;

/**
 * Business rule interface for pipeline processing
 */
public interface BizRule {

    /**
     * Unique identifier for this rule type
     */
    String getRuleId();

    /**
     * Execute the rule against the context
     */
    RuleResult execute(PipelineContext context, Map<String, Object> config);

    /**
     * Validate that the configuration is valid for this rule
     */
    boolean validateConfig(Map<String, Object> config);
}