package com.telcobright.routesphere.rules.processor;

import com.telcobright.routesphere.rules.api.BizRule;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Registry for auto-discovering and managing business rules
 */
@ApplicationScoped
public class RuleRegistry {

    private static final Logger LOG = Logger.getLogger(RuleRegistry.class.getName());

    private final Map<String, BizRule> rules = new HashMap<>();

    @Inject
    Instance<BizRule> ruleInstances;

    @PostConstruct
    void init() {
        for (BizRule rule : ruleInstances) {
            register(rule.getRuleId(), rule);
        }
        LOG.info("Registered " + rules.size() + " business rules: " + rules.keySet());
    }

    public void register(String ruleId, BizRule rule) {
        rules.put(ruleId, rule);
        LOG.fine("Registered rule: " + ruleId);
    }

    public BizRule getRule(String ruleId) {
        return rules.get(ruleId);
    }

    public Set<String> getAvailableRules() {
        return rules.keySet();
    }

    public boolean hasRule(String ruleId) {
        return rules.containsKey(ruleId);
    }
}