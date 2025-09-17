package com.telcobright.routesphere.rules.processor;

import com.telcobright.routesphere.rules.api.BizRule;
import com.telcobright.routesphere.rules.api.PipelineContext;
import com.telcobright.routesphere.rules.api.RuleResult;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Processes business rules hierarchically across tenant levels
 */
@ApplicationScoped
public class HierarchicalRuleProcessor {

    private static final Logger LOG = Logger.getLogger(HierarchicalRuleProcessor.class.getName());

    @Inject
    RuleRegistry ruleRegistry;

    public PipelineResult process(PipelineContext context, List<TenantLevel> hierarchy) {
        LOG.info("Processing rules for tenant hierarchy with " + hierarchy.size() + " levels");

        for (TenantLevel level : hierarchy) {
            LOG.fine("Processing level " + level.getLevel() + " - " + level.getTenantId());

            for (RuleDefinition ruleDef : level.getRules()) {
                BizRule rule = ruleRegistry.getRule(ruleDef.getRuleId());
                if (rule == null) {
                    LOG.warning("Rule not found: " + ruleDef.getRuleId());
                    continue;
                }

                RuleResult result = rule.execute(context, ruleDef.getConfig());

                if (result.isAbort()) {
                    LOG.info("Rule aborted at level " + level.getLevel() + ", rule: " + ruleDef.getRuleId() +
                            ", reason: " + result.getReason());
                    return PipelineResult.abort(level.getLevel(), level.getTenantId(), result.getReason());
                }
            }
        }

        return PipelineResult.success(context);
    }

    /**
     * Represents a tenant at a specific level in hierarchy
     */
    public static class TenantLevel {
        private final int level;
        private final String tenantId;
        private final List<RuleDefinition> rules;

        public TenantLevel(int level, String tenantId, List<RuleDefinition> rules) {
            this.level = level;
            this.tenantId = tenantId;
            this.rules = rules;
        }

        public int getLevel() { return level; }
        public String getTenantId() { return tenantId; }
        public List<RuleDefinition> getRules() { return rules; }
    }

    /**
     * Represents a rule with its configuration
     */
    public static class RuleDefinition {
        private final String ruleId;
        private final Map<String, Object> config;

        public RuleDefinition(String ruleId, Map<String, Object> config) {
            this.ruleId = ruleId;
            this.config = config;
        }

        public String getRuleId() { return ruleId; }
        public Map<String, Object> getConfig() { return config; }
    }

    /**
     * Result of pipeline processing
     */
    public static class PipelineResult {
        private final boolean success;
        private final String abortReason;
        private final int abortLevel;
        private final String abortTenant;
        private final PipelineContext context;

        private PipelineResult(boolean success, String abortReason, int abortLevel, String abortTenant, PipelineContext context) {
            this.success = success;
            this.abortReason = abortReason;
            this.abortLevel = abortLevel;
            this.abortTenant = abortTenant;
            this.context = context;
        }

        public static PipelineResult success(PipelineContext context) {
            return new PipelineResult(true, null, -1, null, context);
        }

        public static PipelineResult abort(int level, String tenantId, String reason) {
            return new PipelineResult(false, reason, level, tenantId, null);
        }

        public boolean isSuccess() { return success; }
        public String getAbortReason() { return abortReason; }
        public int getAbortLevel() { return abortLevel; }
        public String getAbortTenant() { return abortTenant; }
        public PipelineContext getContext() { return context; }
    }
}