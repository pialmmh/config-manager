package com.telcobright.routesphere.protocols.base;

/**
 * Base class for client-mode channels.
 * These channels connect to external servers and receive/poll events.
 *
 * Examples:
 * - ESL client connecting to FreeSWITCH
 * - Kafka consumer connecting to Kafka broker
 * - AMQP client connecting to RabbitMQ
 * - WebSocket client
 */
public abstract class ClientChannel extends AbstractChannel {

    protected String remoteHost;
    protected int remotePort;
    protected boolean autoReconnect = true;
    protected int reconnectDelay = 5000; // milliseconds

    protected ClientChannel(String name, String protocol, ChannelConfig config) {
        super(name, protocol, config);

        // Extract client-specific configuration
        if (config.getConnectionConfig() != null) {
            this.remoteHost = (String) config.getConnectionConfig().get("host");
            Object port = config.getConnectionConfig().get("port");
            if (port != null) {
                this.remotePort = port instanceof Integer ? (Integer) port : Integer.parseInt(port.toString());
            }

            Object reconnect = config.getConnectionConfig().get("reconnect");
            if (reconnect != null) {
                this.autoReconnect = Boolean.parseBoolean(reconnect.toString());
            }

            Object delay = config.getConnectionConfig().get("reconnect-delay");
            if (delay != null) {
                this.reconnectDelay = delay instanceof Integer ? (Integer) delay : Integer.parseInt(delay.toString());
            }
        }
    }

    @Override
    public ChannelMode getMode() {
        return ChannelMode.CLIENT;
    }

    @Override
    protected void doInitialize() throws Exception {
        if (remoteHost != null) {
            LOG.infof("Connecting %s client to %s:%d", protocol, remoteHost, remotePort);
        } else {
            LOG.infof("Initializing %s client", protocol);
        }
        connect();
        if (shouldStartPolling()) {
            startPolling();
        }
    }

    @Override
    protected void doShutdown() throws Exception {
        LOG.infof("Disconnecting %s client", protocol);
        stopPolling();
        disconnect();
    }

    /**
     * Connect to the external server
     */
    protected abstract void connect() throws Exception;

    /**
     * Disconnect from the external server
     */
    protected abstract void disconnect() throws Exception;

    /**
     * Check if this client needs to poll for events
     * (e.g., Kafka consumer polling)
     */
    protected boolean shouldStartPolling() {
        return false; // Override in implementations that need polling
    }

    /**
     * Start polling for events (if applicable)
     */
    protected void startPolling() {
        // Override in implementations that need polling
    }

    /**
     * Stop polling for events
     */
    protected void stopPolling() {
        // Override in implementations that need polling
    }

    /**
     * Handle connection loss and attempt reconnection if configured
     */
    protected void handleConnectionLoss() {
        if (autoReconnect && status == ChannelStatus.RUNNING) {
            LOG.warnf("%s client %s lost connection, attempting reconnect in %d ms",
                    protocol, name, reconnectDelay);

            // TODO: Implement reconnection logic with exponential backoff
            scheduleReconnect();
        } else {
            status = ChannelStatus.ERROR;
            LOG.errorf("%s client %s lost connection and auto-reconnect is disabled",
                    protocol, name);
        }
    }

    /**
     * Schedule a reconnection attempt
     */
    protected void scheduleReconnect() {
        // TODO: Use Quarkus scheduler or executor service
        // This will be implemented with proper async handling
    }

    // Getters
    public String getRemoteHost() {
        return remoteHost;
    }

    public int getRemotePort() {
        return remotePort;
    }

    public boolean isAutoReconnect() {
        return autoReconnect;
    }

    public int getReconnectDelay() {
        return reconnectDelay;
    }
}