package com.telcobright.rtc.domainmodel.nonentity;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class Tenant {
    private final String dbName;
    private String parent;
    private final Map<String, Tenant> children = new HashMap<>();
    private TenantProfile profile;

    public Tenant(String dbName) {
        this.dbName = dbName;
    }

    public void addChild(String childDbName, Tenant child) {
        this.children.put(childDbName, child);
    }
}