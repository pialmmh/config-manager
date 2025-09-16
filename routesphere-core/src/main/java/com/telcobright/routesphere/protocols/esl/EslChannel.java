package com.telcobright.routesphere.protocols.esl;

import com.telcobright.routesphere.protocols.base.ClientChannel;
import com.telcobright.routesphere.protocols.base.ChannelConfig;
import com.telcobright.routesphere.pipeline.call.CallEventProcessor;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Map;
import org.freeswitch.esl.client.IEslEventListener;
import org.freeswitch.esl.client.transport.event.EslEvent;

/**
 * ESL (Event Socket Library) Channel implementation.
 * Connects to FreeSWITCH Event Socket to receive call events.
 */
@ApplicationScoped
public class EslChannel extends ClientChannel implements IEslEventListener {

    @Inject
    CallEventProcessor callEventProcessor;

    private EslClient eslClient;
    private String password;
    private List<String> subscriptions;

    public EslChannel() {
        super("esl-default", "esl", new ChannelConfig());
    }

    public EslChannel(String name, ChannelConfig config) {
        super(name, "esl", config);

        // Extract ESL-specific configuration
        if (config.getConnectionConfig() != null) {
            this.password = (String) config.getConnectionConfig().get("password");
        }

        if (config.getProtocolSpecificConfig() != null) {
            this.subscriptions = (List<String>) config.getProtocolSpecificConfig().get("subscriptions");
        }
    }

    @Override
    protected void connect() throws Exception {
        LOG.infof("========================================");
        LOG.infof("ESL Channel: %s", name);
        LOG.infof("Connecting to FreeSWITCH ESL at %s:%d", remoteHost, remotePort);
        LOG.infof("Password: %s", password != null ? "***" : "not set");
        LOG.infof("========================================");

        // BREAKPOINT 1: Set breakpoint here to debug ESL connection
        eslClient = new EslClient(this, remoteHost, remotePort, password);
        eslClient.setEventHandler(this::handleEslEvent);

        try {
            LOG.info("Attempting ESL connection...");
            eslClient.connect();
            LOG.info("ESL connection established successfully!");
        } catch (Exception e) {
            LOG.errorf("Failed to connect to ESL: %s", e.getMessage());
            throw e;
        }

        // Subscribe to configured events
        if (subscriptions != null && !subscriptions.isEmpty()) {
            LOG.infof("Subscribing to %d events", subscriptions.size());
            for (String event : subscriptions) {
                eslClient.subscribe(event);
                LOG.infof("  ✓ Subscribed to: %s", event);
            }
        }
    }

    @Override
    protected void disconnect() throws Exception {
        if (eslClient != null) {
            LOG.info("Disconnecting from FreeSWITCH ESL");
            eslClient.disconnect();
            eslClient = null;
        }
    }

    /**
     * Handle incoming ESL events
     */
    private void handleEslEvent(com.telcobright.routesphere.protocols.esl.EslEvent event) {
        LOG.debugf("Received ESL event: %s", event.getEventName());

        // Convert ESL event to pipeline event
        Map<String, Object> pipelineEvent = Map.of(
            "type", "esl",
            "eventName", event.getEventName(),
            "channelId", event.getChannelId(),
            "timestamp", System.currentTimeMillis(),
            "data", event.getEventData()
        );

        // Send to CallEventProcessor for detailed logging and processing
        if (callEventProcessor != null) {
            callEventProcessor.processCallEvent(pipelineEvent);
        }

        // Also trigger pipeline processing
        processEvent(pipelineEvent);
    }

    // IEslEventListener implementation
    @Override
    public void eventReceived(EslEvent eslEvent) {
        String eventName = eslEvent.getEventName();

        // Skip heartbeat debug logging to reduce noise
        if (!"HEARTBEAT".equals(eventName)) {
            LOG.debugf("Received FreeSWITCH event: %s", eventName);
        }

        // Convert FreeSWITCH event directly to pipeline event format
        Map<String, Object> pipelineEvent = Map.of(
            "type", "esl",
            "eventName", eventName,
            "channelId", eslEvent.getEventHeaders().getOrDefault("Channel-Unique-ID", ""),
            "timestamp", System.currentTimeMillis(),
            "data", eslEvent.getEventHeaders()
        );

        // Send to CallEventProcessor for detailed logging and processing
        if (callEventProcessor != null) {
            callEventProcessor.processCallEvent(pipelineEvent);
        }

        // Also trigger pipeline processing
        processEvent(pipelineEvent);
    }

    @Override
    public void backgroundJobResultReceived(EslEvent eslEvent) {
        LOG.debugf("Received background job result: %s", eslEvent.getEventName());
    }

    @Override
    protected void handleConnectionLoss() {
        LOG.warn("Lost connection to FreeSWITCH ESL");
        super.handleConnectionLoss();
    }

    // Getters
    public boolean isConnected() {
        return eslClient != null && eslClient.isConnected();
    }

    public List<String> getSubscriptions() {
        return subscriptions;
    }
}