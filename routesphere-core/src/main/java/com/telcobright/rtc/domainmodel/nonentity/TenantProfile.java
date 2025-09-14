package com.telcobright.rtc.domainmodel.nonentity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.telcobright.rtc.freeswitch.AllCacheLoader;
import com.telcobright.rtc.freeswitch.DataLoader;
import com.telcobright.rtc.service.DynamicDatabaseService;
import com.telcobright.rtc.GlobalTenantRegistry;
import lombok.Getter;

public class TenantProfile {
    @JsonIgnore
    private final DynamicDatabaseService databaseService;
    @JsonIgnore
    private final DataLoader dataLoader;
    @JsonIgnore
    private final AllCacheLoader cacheLoader;
    @JsonIgnore
    private final GlobalTenantRegistry globalTenantRegistry;

    @Getter
    private DynamicContext context;
    @Getter
    private AllCache cache;

    public TenantProfile(DynamicDatabaseService databaseService,
                         DataLoader dataLoader,
                         AllCacheLoader cacheLoader,
                         GlobalTenantRegistry globalTenantRegistry) {
        this.databaseService = databaseService;
        this.dataLoader = dataLoader;
        this.cacheLoader = cacheLoader;
        this.globalTenantRegistry = globalTenantRegistry;
    }

    public void load(boolean isReload, String databaseName) {
        clear();
        loadDatabaseContext(databaseName);
        loadDatabaseCache(databaseName);
    }

    private void loadDatabaseContext(String databaseName) {
        // Stub implementation - actual logic would go here
        this.context = new DynamicContext();
    }

    private void loadDatabaseCache(String databaseName) {
        // Stub implementation - actual logic would go here
        this.cache = new AllCache();
    }

    /**
     * Clears the loaded profile data
     */
    public void clear() {
        this.context = null;
        this.cache = null;
    }
}