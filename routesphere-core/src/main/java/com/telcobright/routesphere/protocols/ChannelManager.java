package com.telcobright.routesphere.protocols;

import com.telcobright.routesphere.protocols.base.AbstractChannel;
import com.telcobright.routesphere.protocols.base.ChannelConfig;
import com.telcobright.routesphere.protocols.base.ChannelConfigLoader;
import com.telcobright.routesphere.protocols.esl.EslChannel;
import com.telcobright.routesphere.protocols.http.HttpChannel;
import com.telcobright.routesphere.protocols.kafka.KafkaChannel;
import com.telcobright.routesphere.protocols.sip.SipChannel;
import io.quarkus.runtime.StartupEvent;
import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages all channel instances across all tenants and protocols.
 * Loads channel configurations and creates appropriate channel instances.
 */
@ApplicationScoped
public class ChannelManager {

    private static final Logger LOG = Logger.getLogger(ChannelManager.class);

    @Inject
    ChannelConfigLoader configLoader;

    // Map of tenant -> list of channels
    private final Map<String, List<AbstractChannel>> tenantChannels = new ConcurrentHashMap<>();

    // Map of channel name -> channel instance for quick lookup
    private final Map<String, AbstractChannel> channelRegistry = new ConcurrentHashMap<>();

    /**
     * Initialize channels on application startup
     * Priority 10 - runs after other services are initialized
     */
    void onStart(@Observes @Priority(10) StartupEvent event) {
        LOG.info("╔════════════════════════════════════════╗");
        LOG.info("║      Channel Manager Starting          ║");
        LOG.info("╚════════════════════════════════════════╝");

        loadAndStartChannels();
    }

    /**
     * Load channel configurations and start all enabled channels
     */
    public void loadAndStartChannels() {
        try {
            // Load all channel configurations
            Map<String, List<ChannelConfig>> configs = configLoader.loadAllChannelConfigs();

            LOG.infof("Loading channels for %d active tenants", configs.size());

            // Create and start channels for each tenant
            for (Map.Entry<String, List<ChannelConfig>> entry : configs.entrySet()) {
                String tenant = entry.getKey();
                List<ChannelConfig> channelConfigs = entry.getValue();

                LOG.infof("Creating %d channels for tenant: %s", channelConfigs.size(), tenant);

                List<AbstractChannel> channels = new ArrayList<>();

                for (ChannelConfig config : channelConfigs) {
                    try {
                        AbstractChannel channel = createChannel(config);
                        if (channel != null) {
                            channels.add(channel);
                            channelRegistry.put(config.getName(), channel);

                            if (config.isEnabled()) {
                                channel.initialize();
                                LOG.infof("Started %s channel: %s",
                                    config.getProtocol(), config.getName());
                            } else {
                                LOG.debugf("Channel %s is disabled", config.getName());
                            }
                        }
                    } catch (Exception e) {
                        LOG.errorf("Failed to create channel %s: %s",
                            config.getName(), e.getMessage());
                    }
                }

                tenantChannels.put(tenant, channels);
            }

            // Log summary
            int totalChannels = channelRegistry.size();
            long runningChannels = channelRegistry.values().stream()
                .filter(ch -> ch.getStatus() == AbstractChannel.ChannelStatus.RUNNING)
                .count();

            LOG.info("╔═══════════════════════════════════════╗");
            LOG.infof("║ Channels: %d total, %d running        ║", totalChannels, runningChannels);
            LOG.info("╚═══════════════════════════════════════╝");

        } catch (Exception e) {
            LOG.errorf("Error loading channels: %s", e.getMessage());
        }
    }

    /**
     * Create a channel instance based on configuration
     */
    private AbstractChannel createChannel(ChannelConfig config) {
        String protocol = config.getProtocol();
        String name = config.getName();

        LOG.debugf("Creating %s channel: %s", protocol, name);

        switch (protocol.toLowerCase()) {
            case "esl":
                return new EslChannel(name, config);

            case "kafka":
                return new KafkaChannel(name, config);

            case "http":
                return new HttpChannel(name, config);

            case "sip":
                return new SipChannel(name, config);

            default:
                LOG.warnf("Unknown protocol: %s for channel %s", protocol, name);
                return null;
        }
    }

    /**
     * Shutdown all channels
     */
    public void shutdownAllChannels() {
        LOG.info("Shutting down all channels");

        for (AbstractChannel channel : channelRegistry.values()) {
            try {
                channel.shutdown();
            } catch (Exception e) {
                LOG.errorf("Error shutting down channel %s: %s",
                    channel.getName(), e.getMessage());
            }
        }

        channelRegistry.clear();
        tenantChannels.clear();
    }

    /**
     * Get all channels for a tenant
     */
    public List<AbstractChannel> getTenantChannels(String tenant) {
        return tenantChannels.getOrDefault(tenant, new ArrayList<>());
    }

    /**
     * Get a specific channel by name
     */
    public AbstractChannel getChannel(String name) {
        return channelRegistry.get(name);
    }

    /**
     * Get channel status report
     */
    public Map<String, Object> getStatusReport() {
        Map<String, Object> report = new HashMap<>();

        report.put("totalChannels", channelRegistry.size());
        report.put("tenantCount", tenantChannels.size());

        Map<String, Integer> protocolCounts = new HashMap<>();
        Map<String, Integer> statusCounts = new HashMap<>();

        for (AbstractChannel channel : channelRegistry.values()) {
            // Count by protocol
            protocolCounts.merge(channel.getProtocol(), 1, Integer::sum);

            // Count by status
            statusCounts.merge(channel.getStatus().toString(), 1, Integer::sum);
        }

        report.put("protocolCounts", protocolCounts);
        report.put("statusCounts", statusCounts);

        // Add tenant breakdown
        Map<String, Map<String, Object>> tenantStats = new HashMap<>();
        for (Map.Entry<String, List<AbstractChannel>> entry : tenantChannels.entrySet()) {
            Map<String, Object> stats = new HashMap<>();
            stats.put("channelCount", entry.getValue().size());
            stats.put("runningCount", entry.getValue().stream()
                .filter(ch -> ch.getStatus() == AbstractChannel.ChannelStatus.RUNNING)
                .count());

            tenantStats.put(entry.getKey(), stats);
        }
        report.put("tenantStats", tenantStats);

        return report;
    }

    /**
     * Reload channels (useful for configuration changes)
     */
    public void reloadChannels() {
        LOG.info("Reloading all channels");

        // Shutdown existing channels
        shutdownAllChannels();

        // Load and start new channels
        loadAndStartChannels();
    }
}