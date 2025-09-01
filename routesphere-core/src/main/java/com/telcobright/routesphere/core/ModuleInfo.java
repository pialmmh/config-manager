package com.telcobright.routesphere.core;

/**
 * Information about RouteSphere modules
 */
public class ModuleInfo {
    
    private final String name;
    private final String version;
    private final String description;
    private final ModuleStatus status;
    
    public enum ModuleStatus {
        ACTIVE,
        INACTIVE,
        ERROR,
        LOADING,
        UNKNOWN
    }
    
    public ModuleInfo(String name, String version, String description, ModuleStatus status) {
        this.name = name;
        this.version = version;
        this.description = description;
        this.status = status;
    }
    
    public String getName() {
        return name;
    }
    
    public String getVersion() {
        return version;
    }
    
    public String getDescription() {
        return description;
    }
    
    public ModuleStatus getStatus() {
        return status;
    }
    
    @Override
    public String toString() {
        return String.format("Module[%s v%s - %s (%s)]", 
            name, version, description, status);
    }
    
    public static class Builder {
        private String name;
        private String version = "1.0.0";
        private String description = "";
        private ModuleStatus status = ModuleStatus.UNKNOWN;
        
        public Builder name(String name) {
            this.name = name;
            return this;
        }
        
        public Builder version(String version) {
            this.version = version;
            return this;
        }
        
        public Builder description(String description) {
            this.description = description;
            return this;
        }
        
        public Builder status(ModuleStatus status) {
            this.status = status;
            return this;
        }
        
        public ModuleInfo build() {
            if (name == null || name.isEmpty()) {
                throw new IllegalArgumentException("Module name is required");
            }
            return new ModuleInfo(name, version, description, status);
        }
    }
}