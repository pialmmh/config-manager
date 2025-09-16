package com.telcobright.routesphere.protocols.sip;

import org.jboss.logging.Logger;
import java.util.List;
import java.util.function.Consumer;

/**
 * SIP Server for handling SIP signaling.
 * This is a placeholder implementation - actual SIP stack integration needed.
 */
public class SipServer {

    private static final Logger LOG = Logger.getLogger(SipServer.class);

    private String host;
    private int port;
    private String transport;
    private String userAgent;
    private String realm;
    private boolean authRequired;
    private List<String> allowedIps;
    private boolean running = false;
    private Consumer<SipMessage> eventHandler;

    public SipServer(String host, int port, String transport) {
        this.host = host;
        this.port = port;
        this.transport = transport;
    }

    public void start() throws Exception {
        // TODO: Implement actual SIP server using JAIN-SIP or similar
        LOG.infof("Starting SIP server on %s:%d (%s)", host, port, transport);
        running = true;
    }

    public void stop() {
        if (running) {
            LOG.info("Stopping SIP server");
            // TODO: Implement actual SIP server shutdown
            running = false;
        }
    }

    public void setEventHandler(Consumer<SipMessage> handler) {
        this.eventHandler = handler;
    }

    // Setters for configuration
    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public void setRealm(String realm) {
        this.realm = realm;
    }

    public void setAuthRequired(boolean authRequired) {
        this.authRequired = authRequired;
    }

    public void setAllowedIps(List<String> allowedIps) {
        this.allowedIps = allowedIps;
    }

    // Getters
    public boolean isRunning() {
        return running;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getTransport() {
        return transport;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public String getRealm() {
        return realm;
    }
}