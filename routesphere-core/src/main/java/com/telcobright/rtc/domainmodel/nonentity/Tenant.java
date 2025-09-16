package com.telcobright.rtc.domainmodel.nonentity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Setter
@Getter
@NoArgsConstructor
public class Tenant {
    private String dbName;
    private String parent;
    private Map<String, Tenant> children = new HashMap<>();
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private DynamicContext context;

    @JsonCreator
    public Tenant(@JsonProperty("dbName") String dbName) {
        this.dbName = dbName;
    }

    public void addChild(String childDbName, Tenant child) {
        if (this.children == null) {
            this.children = new HashMap<>();
        }
        this.children.put(childDbName, child);
    }

    /**
     * Returns the DynamicContext. If null, returns an empty immutable context.
     * This ensures the context is never null and is read-only.
     */
    public DynamicContext getContext() {
        if (context == null) {
            return new DynamicContext(); // Returns empty immutable context
        }
        return context;
    }

    /**
     * Package-private setter for Jackson deserialization only.
     * Once set, the context is immutable.
     */
    @JsonProperty("context")
    void setContext(DynamicContext context) {
        if (this.context == null) { // Only allow setting once
            this.context = context;
        }
    }
}