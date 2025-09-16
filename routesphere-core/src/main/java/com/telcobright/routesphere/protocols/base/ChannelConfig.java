package com.telcobright.routesphere.protocols.base;

import java.util.Map;

/**
 * Base configuration for all channels
 */
public class ChannelConfig {

    private String name;
    private String mode;  // "server" or "client"
    private String protocol;
    private boolean enabled = true;
    private String pipelineName;
    private boolean async = true;
    private Map<String, Object> connectionConfig;
    private Map<String, Object> protocolSpecificConfig;

    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getPipelineName() {
        return pipelineName;
    }

    public void setPipelineName(String pipelineName) {
        this.pipelineName = pipelineName;
    }

    public boolean isAsync() {
        return async;
    }

    public void setAsync(boolean async) {
        this.async = async;
    }

    public Map<String, Object> getConnectionConfig() {
        return connectionConfig;
    }

    public void setConnectionConfig(Map<String, Object> connectionConfig) {
        this.connectionConfig = connectionConfig;
    }

    public Map<String, Object> getProtocolSpecificConfig() {
        return protocolSpecificConfig;
    }

    public void setProtocolSpecificConfig(Map<String, Object> protocolSpecificConfig) {
        this.protocolSpecificConfig = protocolSpecificConfig;
    }

    @Override
    public String toString() {
        return "ChannelConfig{" +
                "name='" + name + '\'' +
                ", mode='" + mode + '\'' +
                ", protocol='" + protocol + '\'' +
                ", enabled=" + enabled +
                ", pipelineName='" + pipelineName + '\'' +
                ", async=" + async +
                '}';
    }
}