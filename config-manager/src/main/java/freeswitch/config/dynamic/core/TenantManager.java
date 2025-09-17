package freeswitch.config.dynamic.core;

import freeswitch.config.dynamic.GlobalTenantRegistry;
import com.telcobright.rtc.domainmodel.nonentity.Tenant;
import com.telcobright.rtc.domainmodel.nonentity.DynamicContext;
import freeswitch.service.database.DynamicDatabaseService;

import java.util.*;

public class TenantManager {
    private final DynamicDatabaseService databaseService;
    private final DataLoader dataLoader;
    private final AllCacheLoader cacheLoader;
    private final GlobalTenantRegistry registry;

    public TenantManager(DynamicDatabaseService databaseService,
                         DataLoader dataLoader,
                         AllCacheLoader cacheLoader,
                         GlobalTenantRegistry globalTenantRegistry) {
        this.databaseService = databaseService;
        this.dataLoader = dataLoader;
        this.cacheLoader = cacheLoader;
        this.registry = globalTenantRegistry;
    }

    public Tenant buildTenantTree(String rootDbName) {
        Tenant root = new Tenant(rootDbName);
        root.setContext(createDynamicContext(rootDbName));

        List<String> databases = databaseService.getResellerDbs();
        Map<String, Tenant> tenantMap = new HashMap<>();
        tenantMap.put(rootDbName, root);

        databases.stream()
                .sorted(Comparator.comparingInt(db -> db.split("_").length))
                .forEach(db -> {
                    String[] parts = db.split("_");
                    Tenant tenant = new Tenant(db);
                    tenant.setContext(createDynamicContext(db));

                    if (parts.length == 2) {
                        root.addChild(db, tenant);
                        tenant.setParent(root.getDbName());
                    } else {
                        String parentDb = String.join("_", Arrays.copyOf(parts, parts.length - 1));
                        Tenant parent = tenantMap.get(parentDb);
                        if (parent != null) {
                            parent.addChild(db, tenant);
                            tenant.setParent(parentDb);
                        }
                    }
                    tenantMap.put(db, tenant);
                });

        return root;
    }

    private DynamicContext createDynamicContext(String dbName) {
        // Switch to the target database and load the context data
        databaseService.switchDatabase(dbName);
        return dataLoader.loadDynamicContext(dbName);
    }
}