package com.telcobright.routesphere;

import com.telcobright.routesphere.config.RouteSphereConfig;
import com.telcobright.routesphere.core.ModuleInfo;
import com.telcobright.routesphere.core.RouteSphereConstants;
import com.telcobright.routesphere.utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Main application class for RouteSphere
 */
public class RouteSphereApplication {
    
    private static final List<ModuleInfo> modules = new ArrayList<>();
    
    static {
        // Register all modules
        modules.add(new ModuleInfo.Builder()
            .name(RouteSphereConstants.MODULE_STATEMACHINE)
            .version("1.0-SNAPSHOT")
            .description("High-performance state machine library for telecom and RTC applications")
            .status(ModuleInfo.ModuleStatus.ACTIVE)
            .build());
            
        modules.add(new ModuleInfo.Builder()
            .name(RouteSphereConstants.MODULE_SCHEDULER)
            .version("1.0.0")
            .description("Infinite scheduler for distributed job processing")
            .status(ModuleInfo.ModuleStatus.ACTIVE)
            .build());
            
        modules.add(new ModuleInfo.Builder()
            .name(RouteSphereConstants.MODULE_PARTITIONED_REPO)
            .version("1.0.0")
            .description("Generic sharding-aware repository framework")
            .status(ModuleInfo.ModuleStatus.ACTIVE)
            .build());
            
        modules.add(new ModuleInfo.Builder()
            .name(RouteSphereConstants.MODULE_CHRONICLE_CACHE)
            .version("1.0-SNAPSHOT")
            .description("High-performance cache using Chronicle Queue")
            .status(ModuleInfo.ModuleStatus.ACTIVE)
            .build());
            
        modules.add(new ModuleInfo.Builder()
            .name(RouteSphereConstants.MODULE_RTC_MANAGER)
            .version("1.0-SNAPSHOT")
            .description("Spring Boot/Quarkus RTC management applications")
            .status(ModuleInfo.ModuleStatus.ACTIVE)
            .build());
    }
    
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println(" " + RouteSphereConstants.PROJECT_NAME + " v" + RouteSphereConstants.PROJECT_VERSION);
        System.out.println(" " + RouteSphereConstants.ORGANIZATION);
        System.out.println("========================================");
        System.out.println();
        
        // Load configuration
        RouteSphereConfig config = RouteSphereConfig.getInstance();
        System.out.println("Configuration loaded at: " + CommonUtils.getCurrentTimestamp());
        System.out.println();
        
        // Display module information
        System.out.println("Available Modules:");
        System.out.println("------------------");
        for (ModuleInfo module : modules) {
            System.out.printf("  • %-25s v%-12s %s%n", 
                module.getName(), 
                module.getVersion(), 
                module.getStatus());
            System.out.printf("    %s%n", module.getDescription());
            System.out.println();
        }
        
        // Display configuration
        System.out.println("Configuration:");
        System.out.println("--------------");
        System.out.println("  Database Pool Size: " + 
            config.getInt("routesphere.database.pool.size", 
                RouteSphereConstants.DEFAULT_CONNECTION_POOL_SIZE));
        System.out.println("  Cache Size: " + 
            config.getInt("routesphere.cache.size", 
                RouteSphereConstants.DEFAULT_CACHE_SIZE));
        System.out.println("  Scheduler Threads: " + 
            config.getInt("routesphere.scheduler.threads", 
                RouteSphereConstants.DEFAULT_THREAD_POOL_SIZE));
        System.out.println();
        
        System.out.println("Unique ID Example: " + CommonUtils.generateUniqueId("SESSION"));
        System.out.println();
        
        System.out.println("========================================");
        System.out.println("RouteSphere is ready!");
        System.out.println("Use individual modules or Spring Boot/Quarkus applications as needed.");
        System.out.println("========================================");
    }
    
    /**
     * Get list of all registered modules
     */
    public static List<ModuleInfo> getModules() {
        return new ArrayList<>(modules);
    }
    
    /**
     * Get module by name
     */
    public static ModuleInfo getModule(String name) {
        return modules.stream()
            .filter(m -> m.getName().equals(name))
            .findFirst()
            .orElse(null);
    }
}