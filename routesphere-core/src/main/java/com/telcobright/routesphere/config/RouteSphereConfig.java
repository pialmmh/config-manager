package com.telcobright.routesphere.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Central configuration manager for RouteSphere
 */
public class RouteSphereConfig {
    
    private static final String DEFAULT_CONFIG_FILE = "routesphere.properties";
    private static RouteSphereConfig instance;
    private final Properties properties;
    private final ConcurrentHashMap<String, String> overrides;
    
    private RouteSphereConfig() {
        this.properties = new Properties();
        this.overrides = new ConcurrentHashMap<>();
        loadDefaultConfiguration();
    }
    
    public static synchronized RouteSphereConfig getInstance() {
        if (instance == null) {
            instance = new RouteSphereConfig();
        }
        return instance;
    }
    
    private void loadDefaultConfiguration() {
        try (InputStream input = getClass().getClassLoader()
                .getResourceAsStream(DEFAULT_CONFIG_FILE)) {
            if (input != null) {
                properties.load(input);
            }
        } catch (IOException e) {
            System.err.println("Could not load default configuration: " + e.getMessage());
        }
        
        // Load system properties as overrides
        System.getProperties().forEach((key, value) -> {
            if (key.toString().startsWith("routesphere.")) {
                overrides.put(key.toString(), value.toString());
            }
        });
    }
    
    /**
     * Get configuration value
     */
    public String get(String key) {
        return get(key, null);
    }
    
    /**
     * Get configuration value with default
     */
    public String get(String key, String defaultValue) {
        // Check overrides first
        String value = overrides.get(key);
        if (value != null) {
            return value;
        }
        
        // Then check properties
        value = properties.getProperty(key);
        if (value != null) {
            return value;
        }
        
        // Finally return default
        return defaultValue;
    }
    
    /**
     * Get configuration as integer
     */
    public int getInt(String key, int defaultValue) {
        String value = get(key);
        if (value != null) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                System.err.println("Invalid integer value for " + key + ": " + value);
            }
        }
        return defaultValue;
    }
    
    /**
     * Get configuration as long
     */
    public long getLong(String key, long defaultValue) {
        String value = get(key);
        if (value != null) {
            try {
                return Long.parseLong(value);
            } catch (NumberFormatException e) {
                System.err.println("Invalid long value for " + key + ": " + value);
            }
        }
        return defaultValue;
    }
    
    /**
     * Get configuration as boolean
     */
    public boolean getBoolean(String key, boolean defaultValue) {
        String value = get(key);
        if (value != null) {
            return Boolean.parseBoolean(value);
        }
        return defaultValue;
    }
    
    /**
     * Set configuration override
     */
    public void set(String key, String value) {
        if (value != null) {
            overrides.put(key, value);
        } else {
            overrides.remove(key);
        }
    }
    
    /**
     * Load additional properties file
     */
    public void loadProperties(String filename) throws IOException {
        try (InputStream input = getClass().getClassLoader()
                .getResourceAsStream(filename)) {
            if (input != null) {
                Properties additionalProps = new Properties();
                additionalProps.load(input);
                additionalProps.forEach((key, value) -> 
                    properties.setProperty(key.toString(), value.toString())
                );
            } else {
                throw new IOException("Properties file not found: " + filename);
            }
        }
    }
    
    /**
     * Get all configuration keys with a specific prefix
     */
    public Properties getPropertiesWithPrefix(String prefix) {
        Properties filtered = new Properties();
        
        // Check properties
        properties.forEach((key, value) -> {
            if (key.toString().startsWith(prefix)) {
                filtered.setProperty(key.toString(), value.toString());
            }
        });
        
        // Check overrides
        overrides.forEach((key, value) -> {
            if (key.startsWith(prefix)) {
                filtered.setProperty(key, value);
            }
        });
        
        return filtered;
    }
}