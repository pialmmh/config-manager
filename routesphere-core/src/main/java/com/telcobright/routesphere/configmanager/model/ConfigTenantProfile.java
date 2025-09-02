package com.telcobright.routesphere.configmanager.model;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Tenant Profile containing configuration data
 * Matches the TenantProfile from original ConfigManager
 */
public class ConfigTenantProfile {
    
    private String dbName;
    private Map<String, Object> allCache = new ConcurrentHashMap<>();
    private Map<String, Object> configurations = new HashMap<>();
    private DatabaseConfig databaseConfig;
    private KafkaConfig kafkaConfig;
    private RedisConfig redisConfig;
    
    public static class DatabaseConfig {
        private String url;
        private String username;
        private String password;
        private String driver;
        private int maxPoolSize = 10;
        
        // Getters and Setters
        public String getUrl() {
            return url;
        }
        
        public void setUrl(String url) {
            this.url = url;
        }
        
        public String getUsername() {
            return username;
        }
        
        public void setUsername(String username) {
            this.username = username;
        }
        
        public String getPassword() {
            return password;
        }
        
        public void setPassword(String password) {
            this.password = password;
        }
        
        public String getDriver() {
            return driver;
        }
        
        public void setDriver(String driver) {
            this.driver = driver;
        }
        
        public int getMaxPoolSize() {
            return maxPoolSize;
        }
        
        public void setMaxPoolSize(int maxPoolSize) {
            this.maxPoolSize = maxPoolSize;
        }
    }
    
    public static class KafkaConfig {
        private String bootstrapServers;
        private String groupId;
        private String securityProtocol;
        private Map<String, String> properties = new HashMap<>();
        
        // Getters and Setters
        public String getBootstrapServers() {
            return bootstrapServers;
        }
        
        public void setBootstrapServers(String bootstrapServers) {
            this.bootstrapServers = bootstrapServers;
        }
        
        public String getGroupId() {
            return groupId;
        }
        
        public void setGroupId(String groupId) {
            this.groupId = groupId;
        }
        
        public String getSecurityProtocol() {
            return securityProtocol;
        }
        
        public void setSecurityProtocol(String securityProtocol) {
            this.securityProtocol = securityProtocol;
        }
        
        public Map<String, String> getProperties() {
            return properties;
        }
        
        public void setProperties(Map<String, String> properties) {
            this.properties = properties;
        }
    }
    
    public static class RedisConfig {
        private String host;
        private int port;
        private String password;
        private int database;
        private int maxConnections = 50;
        
        // Getters and Setters
        public String getHost() {
            return host;
        }
        
        public void setHost(String host) {
            this.host = host;
        }
        
        public int getPort() {
            return port;
        }
        
        public void setPort(int port) {
            this.port = port;
        }
        
        public String getPassword() {
            return password;
        }
        
        public void setPassword(String password) {
            this.password = password;
        }
        
        public int getDatabase() {
            return database;
        }
        
        public void setDatabase(int database) {
            this.database = database;
        }
        
        public int getMaxConnections() {
            return maxConnections;
        }
        
        public void setMaxConnections(int maxConnections) {
            this.maxConnections = maxConnections;
        }
    }
    
    public ConfigTenantProfile() {
    }
    
    public ConfigTenantProfile(String dbName) {
        this.dbName = dbName;
    }
    
    public void load(boolean forceReload, String dbName) {
        this.dbName = dbName;
        // Loading logic will be implemented in service layer
    }
    
    // Cache operations
    public void putInCache(String key, Object value) {
        allCache.put(key, value);
    }
    
    public Object getFromCache(String key) {
        return allCache.get(key);
    }
    
    public void clearCache() {
        allCache.clear();
    }
    
    // Configuration operations
    public void addConfiguration(String key, Object value) {
        configurations.put(key, value);
    }
    
    public Object getConfiguration(String key) {
        return configurations.get(key);
    }
    
    // Getters and Setters
    public String getDbName() {
        return dbName;
    }
    
    public void setDbName(String dbName) {
        this.dbName = dbName;
    }
    
    public Map<String, Object> getAllCache() {
        return allCache;
    }
    
    public void setAllCache(Map<String, Object> allCache) {
        this.allCache = allCache;
    }
    
    public Map<String, Object> getConfigurations() {
        return configurations;
    }
    
    public void setConfigurations(Map<String, Object> configurations) {
        this.configurations = configurations;
    }
    
    public DatabaseConfig getDatabaseConfig() {
        return databaseConfig;
    }
    
    public void setDatabaseConfig(DatabaseConfig databaseConfig) {
        this.databaseConfig = databaseConfig;
    }
    
    public KafkaConfig getKafkaConfig() {
        return kafkaConfig;
    }
    
    public void setKafkaConfig(KafkaConfig kafkaConfig) {
        this.kafkaConfig = kafkaConfig;
    }
    
    public RedisConfig getRedisConfig() {
        return redisConfig;
    }
    
    public void setRedisConfig(RedisConfig redisConfig) {
        this.redisConfig = redisConfig;
    }
}