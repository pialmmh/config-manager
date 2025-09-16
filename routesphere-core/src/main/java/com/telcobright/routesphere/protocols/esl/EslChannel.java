package com.telcobright.routesphere.protocols.esl;

import com.telcobright.routesphere.protocols.base.ClientChannel;
import com.telcobright.routesphere.protocols.base.ChannelConfig;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Map;

/**
 * ESL (Event Socket Library) Channel implementation.
 * Connects to FreeSWITCH Event Socket to receive call events.
 */
@ApplicationScoped
public class EslChannel extends ClientChannel {

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
        LOG.infof("Connecting to FreeSWITCH ESL at %s:%d", remoteHost, remotePort);

        eslClient = new EslClient(remoteHost, remotePort, password);
        eslClient.setEventHandler(this::handleEslEvent);
        eslClient.connect();

        // Subscribe to configured events
        if (subscriptions != null && !subscriptions.isEmpty()) {
            for (String event : subscriptions) {
                eslClient.subscribe(event);
                LOG.debugf("Subscribed to ESL event: %s", event);
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
    private void handleEslEvent(EslEvent event) {
        LOG.debugf("Received ESL event: %s", event.getEventName());

        // Convert ESL event to pipeline event
        Map<String, Object> pipelineEvent = Map.of(
            "type", "esl",
            "eventName", event.getEventName(),
            "channelId", event.getChannelId(),
            "timestamp", System.currentTimeMillis(),
            "data", event.getEventData()
        );

        // Trigger pipeline processing
        processEvent(pipelineEvent);
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