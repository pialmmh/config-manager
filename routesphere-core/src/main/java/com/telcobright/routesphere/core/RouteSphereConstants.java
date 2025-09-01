package com.telcobright.routesphere.core;

/**
 * Central constants for the RouteSphere multi-module project
 */
public class RouteSphereConstants {
    
    // Project Information
    public static final String PROJECT_NAME = "RouteSphere";
    public static final String PROJECT_VERSION = "1.0-SNAPSHOT";
    public static final String ORGANIZATION = "TelcoBright";
    
    // Module Names
    public static final String MODULE_STATEMACHINE = "statemachine";
    public static final String MODULE_SCHEDULER = "infinite-scheduler";
    public static final String MODULE_PARTITIONED_REPO = "partitioned-repo";
    public static final String MODULE_CHRONICLE_CACHE = "chronicle-db-cache";
    public static final String MODULE_RTC_MANAGER = "rtc-manager";
    
    // Common Configuration Keys
    public static final String CONFIG_PREFIX = "routesphere";
    public static final String DB_CONFIG_PREFIX = CONFIG_PREFIX + ".database";
    public static final String CACHE_CONFIG_PREFIX = CONFIG_PREFIX + ".cache";
    public static final String SCHEDULER_CONFIG_PREFIX = CONFIG_PREFIX + ".scheduler";
    
    // Database Settings
    public static final int DEFAULT_CONNECTION_POOL_SIZE = 10;
    public static final int DEFAULT_CONNECTION_TIMEOUT_MS = 30000;
    public static final int DEFAULT_IDLE_TIMEOUT_MS = 600000;
    
    // Cache Settings
    public static final int DEFAULT_CACHE_SIZE = 1000;
    public static final long DEFAULT_CACHE_TTL_MS = 3600000; // 1 hour
    
    // Scheduler Settings
    public static final int DEFAULT_THREAD_POOL_SIZE = 10;
    public static final long DEFAULT_SCHEDULER_INTERVAL_MS = 60000; // 1 minute
    
    // Common Status Codes
    public static final int STATUS_SUCCESS = 0;
    public static final int STATUS_ERROR = 1;
    public static final int STATUS_WARNING = 2;
    public static final int STATUS_PENDING = 3;
    
    private RouteSphereConstants() {
        // Private constructor to prevent instantiation
    }
}