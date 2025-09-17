package com.telcobright.routesphere.rules.common;

import com.telcobright.routesphere.rules.api.BizRule;
import com.telcobright.routesphere.rules.api.PipelineContext;
import com.telcobright.routesphere.rules.api.RuleResult;

import jakarta.enterprise.context.ApplicationScoped;
import java.math.BigDecimal;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Rule to check if tenant has sufficient credit balance
 */
@ApplicationScoped
public class CreditCheckRule implements BizRule {

    private static final Logger LOG = Logger.getLogger(CreditCheckRule.class.getName());

    @Override
    public String getRuleId() {
        return "credit_check";
    }

    @Override
    public RuleResult execute(PipelineContext context, Map<String, Object> config) {
        String tenantId = context.getTenantId();
        BigDecimal minCredit = getConfigValue(config, "minCredit", BigDecimal.ZERO);

        // In real implementation, this would query the database
        BigDecimal availableCredit = getAvailableCredit(tenantId);

        LOG.fine("Credit check for tenant " + tenantId + ": available=" + availableCredit + ", required=" + minCredit);

        if (availableCredit.compareTo(minCredit) < 0) {
            return RuleResult.abort("INSUFFICIENT_CREDIT");
        }

        context.setData("availableCredit", availableCredit);
        return RuleResult.continueWithData(Map.of("creditChecked", true));
    }

    @Override
    public boolean validateConfig(Map<String, Object> config) {
        return config != null && config.containsKey("minCredit");
    }

    private BigDecimal getAvailableCredit(String tenantId) {
        // TODO: Query from database
        return new BigDecimal("100.00");
    }

    private <T> T getConfigValue(Map<String, Object> config, String key, T defaultValue) {
        Object value = config.get(key);
        if (value == null) {
            return defaultValue;
        }
        if (value instanceof Number && defaultValue instanceof BigDecimal) {
            return (T) new BigDecimal(value.toString());
        }
        return (T) value;
    }
}