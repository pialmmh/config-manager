package com.telcobright.routesphere.protocols.base;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Loads channel configurations from the file system.
 * Scans tenant/profile/channels directories for all YAML files.
 */
@ApplicationScoped
public class ChannelConfigLoader {

    private static final Logger LOG = Logger.getLogger(ChannelConfigLoader.class);
    private static final String CONFIG_BASE_PATH = "config/tenants";
    private static final String CHANNELS_DIR = "channels";

    private final ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());

    /**
     * Load all channel configurations for active tenants
     */
    public Map<String, List<ChannelConfig>> loadAllChannelConfigs() {
        Map<String, List<ChannelConfig>> tenantChannels = new HashMap<>();

        try {
            // Load the main tenants configuration
            TenantsConfig tenantsConfig = loadTenantsConfig();

            // Load channels for each active tenant
            for (TenantConfig tenant : tenantsConfig.getTenants()) {
                if (tenant.isActive()) {
                    LOG.infof("Loading channels for active tenant: %s (profile: %s)", tenant.getName(), tenant.getProfile());
                    List<ChannelConfig> channels = loadTenantChannels(tenant.getName(), tenant.getProfile());
                    if (!channels.isEmpty()) {
                        tenantChannels.put(tenant.getName(), channels);
                        LOG.infof("Loaded %d channels for tenant %s", channels.size(), tenant.getName());
                    }
                } else {
                    LOG.infof("Skipping inactive tenant: %s", tenant.getName());
                }
            }
        } catch (Exception e) {
            LOG.errorf("Error loading channel configurations: %s", e.getMessage());
        }

        return tenantChannels;
    }

    /**
     * Load all channel configurations for a specific tenant and profile
     */
    public List<ChannelConfig> loadTenantChannels(String tenantName, String profile) {
        List<ChannelConfig> channels = new ArrayList<>();

        // Build the path to the channels directory
        String channelsPath = String.format("%s/%s/%s/%s",
                CONFIG_BASE_PATH, tenantName, profile, CHANNELS_DIR);

        try {
            // Get the resource path
            Path basePath = getResourcePath(channelsPath);
            if (basePath == null || !Files.exists(basePath)) {
                LOG.warnf("Channels directory not found: %s", channelsPath);
                return channels;
            }

            // Scan all protocol subdirectories
            try (Stream<Path> protocolDirs = Files.list(basePath)) {
                protocolDirs.filter(Files::isDirectory)
                        .forEach(protocolDir -> {
                            String protocol = protocolDir.getFileName().toString();
                            List<ChannelConfig> protocolChannels = loadProtocolChannels(protocolDir, protocol);
                            channels.addAll(protocolChannels);
                        });
            }
        } catch (IOException e) {
            LOG.errorf("Error loading channels for tenant %s profile %s: %s",
                    tenantName, profile, e.getMessage());
        }

        return channels;
    }

    /**
     * Load all channel configurations from a protocol directory
     */
    private List<ChannelConfig> loadProtocolChannels(Path protocolDir, String protocol) {
        List<ChannelConfig> channels = new ArrayList<>();

        try (Stream<Path> files = Files.list(protocolDir)) {
            files.filter(path -> path.toString().endsWith(".yml") || path.toString().endsWith(".yaml"))
                    .forEach(file -> {
                        try {
                            ChannelConfig config = loadChannelConfig(file);
                            if (config != null) {
                                // Set the protocol if not specified in config
                                if (config.getProtocol() == null) {
                                    config.setProtocol(protocol);
                                }
                                channels.add(config);
                                LOG.debugf("Loaded channel config: %s (enabled: %s)", file.getFileName(), config.isEnabled());
                            }
                        } catch (Exception e) {
                            LOG.errorf("Error loading channel config from %s: %s",
                                    file.getFileName(), e.getMessage());
                        }
                    });
        } catch (IOException e) {
            LOG.errorf("Error scanning protocol directory %s: %s",
                    protocolDir.getFileName(), e.getMessage());
        }

        return channels;
    }

    /**
     * Load a single channel configuration from a YAML file
     */
    private ChannelConfig loadChannelConfig(Path configFile) throws IOException {
        Map<String, Object> yamlData = yamlMapper.readValue(configFile.toFile(), Map.class);

        // Extract the channel configuration
        Map<String, Object> channelData = (Map<String, Object>) yamlData.get("channel");
        if (channelData == null) {
            LOG.warnf("No 'channel' section found in %s", configFile.getFileName());
            return null;
        }

        ChannelConfig config = new ChannelConfig();

        // Map basic fields
        config.setName((String) channelData.get("name"));
        config.setMode((String) channelData.get("mode"));
        config.setProtocol((String) channelData.get("protocol"));

        Object enabled = channelData.get("enabled");
        if (enabled != null) {
            config.setEnabled(Boolean.parseBoolean(enabled.toString()));
        }

        // Map pipeline configuration
        Map<String, Object> pipeline = (Map<String, Object>) channelData.get("pipeline");
        if (pipeline != null) {
            config.setPipelineName((String) pipeline.get("name"));
            Object async = pipeline.get("async");
            if (async != null) {
                config.setAsync(Boolean.parseBoolean(async.toString()));
            }
        }

        // Map connection configuration
        Map<String, Object> connection = (Map<String, Object>) channelData.get("connection");
        if (connection == null) {
            connection = (Map<String, Object>) channelData.get("listener");
        }
        if (connection != null) {
            config.setConnectionConfig(connection);
        }

        // Store protocol-specific configuration
        Map<String, Object> protocolConfig = new HashMap<>(channelData);
        protocolConfig.remove("name");
        protocolConfig.remove("mode");
        protocolConfig.remove("protocol");
        protocolConfig.remove("enabled");
        protocolConfig.remove("pipeline");
        protocolConfig.remove("connection");
        protocolConfig.remove("listener");
        config.setProtocolSpecificConfig(protocolConfig);

        return config;
    }

    /**
     * Load the main tenants configuration
     */
    private TenantsConfig loadTenantsConfig() throws IOException {
        Path tenantsFile = getResourcePath(CONFIG_BASE_PATH + "/tenants.yml");
        if (tenantsFile == null || !Files.exists(tenantsFile)) {
            LOG.warn("tenants.yml not found, using default configuration");
            return new TenantsConfig();
        }

        return yamlMapper.readValue(tenantsFile.toFile(), TenantsConfig.class);
    }

    /**
     * Get the actual file system path for a resource path
     */
    private Path getResourcePath(String resourcePath) {
        try {
            // First try to get it from the classpath
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            java.net.URL resource = classLoader.getResource(resourcePath);

            if (resource != null) {
                return Paths.get(resource.toURI());
            }

            // If not found in classpath, try the file system directly
            Path path = Paths.get("src/main/resources/" + resourcePath);
            if (Files.exists(path)) {
                return path;
            }
        } catch (Exception e) {
            LOG.debugf("Could not resolve resource path %s: %s", resourcePath, e.getMessage());
        }

        return null;
    }

    /**
     * Inner class for tenants configuration
     */
    public static class TenantsConfig {
        private List<TenantConfig> tenants = new ArrayList<>();

        public List<TenantConfig> getTenants() {
            return tenants;
        }

        public void setTenants(List<TenantConfig> tenants) {
            this.tenants = tenants;
        }
    }

    /**
     * Inner class for tenant configuration
     */
    public static class TenantConfig {
        private String name;
        private boolean active;
        private String profile;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public boolean isActive() {
            return active;
        }

        public void setActive(boolean active) {
            this.active = active;
        }

        public String getProfile() {
            return profile;
        }

        public void setProfile(String profile) {
            this.profile = profile;
        }
    }
}