package com.telcobright.routesphere.utils;

import com.telcobright.routesphere.startup.TenantHierarchyInitializer;
import io.quarkus.runtime.QuarkusApplication;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.concurrent.atomic.AtomicInteger;

@ApplicationScoped
public class Starter implements QuarkusApplication {

    private final TenantHierarchyInitializer tenantHierarchyInitializer;

    public Starter(TenantHierarchyInitializer tenantHierarchyInitializer) {
        this.tenantHierarchyInitializer = tenantHierarchyInitializer;
    }

    @Override
    public int run(String... args) throws Exception {
       tenantHierarchyInitializer.onStart();
        return 0;
    }
}
