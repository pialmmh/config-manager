package com.telcobright.rtc.domainmodel.nonentity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import freeswitch.config.dynamic.GlobalTenantRegistry;
import freeswitch.config.dynamic.core.AllCacheLoader;
import freeswitch.config.dynamic.core.DataLoader;
import freeswitch.service.database.DynamicDatabaseService;
import lombok.Getter;
import org.springframework.stereotype.Component;

@Component
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
        databaseService.switchDatabase(databaseName);
        this.context = new DynamicContext(dataLoader); // Immutable creation
        
        //load globalTenantRegistry using the context
        loadGlobalTenantRegistry(this.context);
    }

    private void loadGlobalTenantRegistry(DynamicContext context) {
        // Load partners
        context.getPartners().values().forEach(partner -> {
            globalTenantRegistry.getPartnerIds().put(partner.getIdPartner(), partner.getPartnerName());
            globalTenantRegistry.getPartnerNames().put(
                    partner.getPartnerName().toLowerCase(),
                    partner.getIdPartner()
            );
        });
        //route wise partners
        globalTenantRegistry.setRouteWisePartners(context);
        //sip account wise partners;
        globalTenantRegistry.setSipAccountWisePartners(context);
        dataLoader.getRoutes().forEach(route -> {
            globalTenantRegistry.getRouteIps().put(route.getField5(), route.getIdroute());
        });
        dataLoader.getRetailPartners().forEach(retailPartner -> {
            globalTenantRegistry.getRetailPartnerUsernames().put(retailPartner.getUserName(), retailPartner.getId());
        });
    }

    private void loadDatabaseCache(String databaseName) {
        databaseService.switchDatabase(databaseName);
        this.cache = new AllCache(cacheLoader); // Immutable creation
    }

    /**
     * Clears the loaded profile data
     */
    public void clear() {
        this.context = null;
        this.cache = null;
    }
}
