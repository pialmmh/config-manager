package com.telcobright.routesphere.protocols.base;

/**
 * Base class for server-mode channels.
 * These channels listen on an IP:Port for incoming connections/requests.
 *
 * Examples:
 * - REST API listening on HTTP port
 * - SIP server listening on UDP/TCP port
 * - WebSocket server
 */
public abstract class ServerChannel extends AbstractChannel {

    protected String listenHost;
    protected int listenPort;

    protected ServerChannel(String name, String protocol, ChannelConfig config) {
        super(name, protocol, config);

        // Extract server-specific configuration
        if (config.getConnectionConfig() != null) {
            this.listenHost = (String) config.getConnectionConfig().get("host");
            Object port = config.getConnectionConfig().get("port");
            this.listenPort = port instanceof Integer ? (Integer) port : Integer.parseInt(port.toString());
        }
    }

    @Override
    public ChannelMode getMode() {
        return ChannelMode.SERVER;
    }

    @Override
    protected void doInitialize() throws Exception {
        LOG.infof("Starting %s server on %s:%d", protocol, listenHost, listenPort);
        startListener();
    }

    @Override
    protected void doShutdown() throws Exception {
        LOG.infof("Stopping %s server on %s:%d", protocol, listenHost, listenPort);
        stopListener();
    }

    /**
     * Start listening on the configured host:port
     */
    protected abstract void startListener() throws Exception;

    /**
     * Stop the listener
     */
    protected abstract void stopListener() throws Exception;

    /**
     * Handle an incoming connection/request
     */
    protected abstract void handleIncomingConnection(Object connection);

    // Getters
    public String getListenHost() {
        return listenHost;
    }

    public int getListenPort() {
        return listenPort;
    }
}