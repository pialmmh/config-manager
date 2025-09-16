package com.telcobright.routesphere.protocols;

import com.telcobright.routesphere.protocols.base.ChannelConfig;
import com.telcobright.routesphere.protocols.base.ChannelConfigLoader;
import io.quarkus.runtime.StartupEvent;
import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import java.util.List;
import java.util.Map;

/**
 * Test runner to demonstrate multiple channel instances being loaded
 */
@ApplicationScoped
public class ChannelTestRunner {

    private static final Logger LOG = Logger.getLogger(ChannelTestRunner.class);

    @Inject
    ChannelConfigLoader configLoader;

    /**
     * Run on startup with low priority (after other services)
     */
    void onStart(@Observes @Priority(20) StartupEvent event) {
        LOG.info("╔════════════════════════════════════════╗");
        LOG.info("║    Channel Configuration Test          ║");
        LOG.info("╚════════════════════════════════════════╝");

        demonstrateMultipleChannelInstances();
    }

    private void demonstrateMultipleChannelInstances() {
        try {
            Map<String, List<ChannelConfig>> allConfigs = configLoader.loadAllChannelConfigs();

            for (Map.Entry<String, List<ChannelConfig>> tenantEntry : allConfigs.entrySet()) {
                String tenant = tenantEntry.getKey();
                List<ChannelConfig> channels = tenantEntry.getValue();

                LOG.info("═══════════════════════════════════════");
                LOG.infof("Tenant: %s", tenant);
                LOG.infof("Total channels configured: %d", channels.size());
                LOG.info("───────────────────────────────────────");

                // Group channels by protocol
                Map<String, Integer> protocolCounts = new java.util.HashMap<>();

                for (ChannelConfig channel : channels) {
                    String protocol = channel.getProtocol();
                    protocolCounts.merge(protocol, 1, Integer::sum);

                    LOG.infof("  ✓ %s channel: %s [%s] %s",
                        protocol.toUpperCase(),
                        channel.getName(),
                        channel.getMode(),
                        channel.isEnabled() ? "ENABLED" : "DISABLED"
                    );

                    // Show connection details
                    if (channel.getConnectionConfig() != null) {
                        Map<String, Object> conn = channel.getConnectionConfig();
                        String host = (String) conn.get("host");
                        Object port = conn.get("port");
                        LOG.infof("      → %s:%s", host, port);
                    }

                    // Show pipeline
                    if (channel.getPipelineName() != null) {
                        LOG.infof("      → Pipeline: %s", channel.getPipelineName());
                    }
                }

                // Show summary by protocol
                LOG.info("───────────────────────────────────────");
                LOG.info("Summary by protocol:");
                for (Map.Entry<String, Integer> entry : protocolCounts.entrySet()) {
                    LOG.infof("  • %s: %d instances",
                        entry.getKey().toUpperCase(), entry.getValue());
                }
            }

            LOG.info("═══════════════════════════════════════");

        } catch (Exception e) {
            LOG.errorf("Error demonstrating channels: %s", e.getMessage());
        }
    }
}