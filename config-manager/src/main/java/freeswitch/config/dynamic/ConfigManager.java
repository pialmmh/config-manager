package freeswitch.config.dynamic;

import freeswitch.config.dynamic.core.AllCacheLoader;
import freeswitch.config.dynamic.core.DataLoader;
import freeswitch.config.dynamic.core.TenantManager;
import freeswitch.service.database.DynamicDatabaseService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.telcobright.rtc.domainmodel.nonentity.Tenant;

import java.util.concurrent.atomic.AtomicReference;

@Component
public class ConfigManager {

    private final AtomicReference<Tenant> rootTenant;
    private final TenantManager tenantManager;

    @Value("${admin.db}")
    private final String adminDb;

    public ConfigManager(DynamicDatabaseService databaseService,
                         DataLoader dataLoader,
                         AllCacheLoader cacheLoader,
                         @Value("${admin.db}") String adminDb,
                         GlobalTenantRegistry registry) {
        this.adminDb = adminDb;
        this.tenantManager = new TenantManager(databaseService, dataLoader, cacheLoader, registry);
        this.rootTenant = new AtomicReference<>();
    }
    @PostConstruct
    public void init() {
        loadConfigurations();
    }

    public Tenant getRootTenant() {
        return rootTenant.get();
    }

    public synchronized void loadConfigurations() {
        // Rebuild the complete tenant tree with fresh profiles
        Tenant newRoot = tenantManager.buildTenantTree(adminDb);
        rootTenant.set(newRoot);
    }
}
