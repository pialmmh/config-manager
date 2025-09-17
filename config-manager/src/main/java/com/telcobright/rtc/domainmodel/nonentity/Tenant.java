package com.telcobright.rtc.domainmodel.nonentity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
public class Tenant {
    private String dbName;
    private String parent;
    private Map<String, Tenant> children = new HashMap<>();
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
}