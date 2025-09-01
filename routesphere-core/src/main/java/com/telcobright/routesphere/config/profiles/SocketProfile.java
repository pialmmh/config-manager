package com.telcobright.routesphere.config.profiles;

import com.telcobright.routesphere.protocols.Protocol;
import java.util.HashMap;
import java.util.Map;

/**
 * Socket configuration profile for network bindings
 * Similar to FreeSWITCH profile configs (public, private, etc.)
 */
public class SocketProfile {
    private String profileName;
    private Protocol protocol;
    private String bindAddress;
    private int bindPort;
    private String defaultTenantId;
    private String defaultTenantName;
    private boolean enabled;
    private Map<String, Object> extendedProperties;
    
    public SocketProfile() {
        this.extendedProperties = new HashMap<>();
    }
    
    public SocketProfile(String profileName, Protocol protocol, String bindAddress, int bindPort) {
        this();
        this.profileName = profileName;
        this.protocol = protocol;
        this.bindAddress = bindAddress;
        this.bindPort = bindPort;
        this.enabled = true;
    }
    
    // Getters and setters
    public String getProfileName() {
        return profileName;
    }
    
    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }
    
    public Protocol getProtocol() {
        return protocol;
    }
    
    public void setProtocol(Protocol protocol) {
        this.protocol = protocol;
    }
    
    public String getBindAddress() {
        return bindAddress;
    }
    
    public void setBindAddress(String bindAddress) {
        this.bindAddress = bindAddress;
    }
    
    public int getBindPort() {
        return bindPort;
    }
    
    public void setBindPort(int bindPort) {
        this.bindPort = bindPort;
    }
    
    public String getDefaultTenantId() {
        return defaultTenantId;
    }
    
    public void setDefaultTenantId(String defaultTenantId) {
        this.defaultTenantId = defaultTenantId;
    }
    
    public String getDefaultTenantName() {
        return defaultTenantName;
    }
    
    public void setDefaultTenantName(String defaultTenantName) {
        this.defaultTenantName = defaultTenantName;
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    public Map<String, Object> getExtendedProperties() {
        return extendedProperties;
    }
    
    public void setExtendedProperties(Map<String, Object> extendedProperties) {
        this.extendedProperties = extendedProperties;
    }
    
    public void addExtendedProperty(String key, Object value) {
        this.extendedProperties.put(key, value);
    }
    
    @Override
    public String toString() {
        return String.format("SocketProfile[%s: %s://%s:%d -> tenant:%s]", 
            profileName, protocol.getName(), bindAddress, bindPort, defaultTenantId);
    }
}