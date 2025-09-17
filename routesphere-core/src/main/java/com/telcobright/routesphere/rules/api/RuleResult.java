package com.telcobright.routesphere.rules.api;

import java.util.Map;

/**
 * Result of business rule execution
 */
public class RuleResult {

    public enum ResultType {
        CONTINUE,
        ABORT
    }

    private final ResultType type;
    private final String reason;
    private final Map<String, Object> metadata;

    private RuleResult(ResultType type, String reason, Map<String, Object> metadata) {
        this.type = type;
        this.reason = reason;
        this.metadata = metadata;
    }

    public static RuleResult continueExecution() {
        return new RuleResult(ResultType.CONTINUE, null, null);
    }

    public static RuleResult abort(String reason) {
        return new RuleResult(ResultType.ABORT, reason, null);
    }

    public static RuleResult continueWithData(Map<String, Object> data) {
        return new RuleResult(ResultType.CONTINUE, null, data);
    }

    public boolean isAbort() {
        return type == ResultType.ABORT;
    }

    public ResultType getType() {
        return type;
    }

    public String getReason() {
        return reason;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }
}