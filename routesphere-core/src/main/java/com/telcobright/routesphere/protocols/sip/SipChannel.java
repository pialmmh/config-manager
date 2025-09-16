package com.telcobright.routesphere.protocols.sip;

import com.telcobright.routesphere.protocols.base.ServerChannel;
import com.telcobright.routesphere.protocols.base.ChannelConfig;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Map;

/**
 * SIP Channel implementation.
 * Creates a SIP server to handle SIP signaling.
 */
@ApplicationScoped
public class SipChannel extends ServerChannel {

    private SipServer sipServer;
    private String transport = "UDP";
    private String userAgent = "RouteSphere/1.0";
    private String realm;
    private boolean authRequired = false;
    private List<String> allowedIps;

    public SipChannel() {
        super("sip-default", "sip", new ChannelConfig());
    }

    public SipChannel(String name, ChannelConfig config) {
        super(name, "sip", config);

        // Extract SIP-specific configuration
        if (config.getConnectionConfig() != null) {
            Object trans = config.getConnectionConfig().get("transport");
            if (trans != null) {
                this.transport = trans.toString();
            }
        }

        if (config.getProtocolSpecificConfig() != null) {
            Map<String, Object> sip = (Map<String, Object>) config.getProtocolSpecificConfig().get("sip");
            if (sip != null) {
                Object ua = sip.get("user-agent");
                if (ua != null) {
                    this.userAgent = ua.toString();
                }

                Object r = sip.get("realm");
                if (r != null) {
                    this.realm = r.toString();
                }
            }

            Map<String, Object> security = (Map<String, Object>) config.getProtocolSpecificConfig().get("security");
            if (security != null) {
                Object auth = security.get("auth-required");
                if (auth != null) {
                    this.authRequired = Boolean.parseBoolean(auth.toString());
                }

                this.allowedIps = (List<String>) security.get("allowed-ips");
            }
        }
    }

    @Override
    protected void startListener() throws Exception {
        LOG.infof("Starting SIP server on %s:%d using %s transport",
            listenHost, listenPort, transport);

        sipServer = new SipServer(listenHost, listenPort, transport);
        sipServer.setUserAgent(userAgent);
        sipServer.setRealm(realm);
        sipServer.setAuthRequired(authRequired);
        sipServer.setAllowedIps(allowedIps);
        sipServer.setEventHandler(this::handleSipMessage);

        sipServer.start();

        LOG.infof("SIP server started with realm: %s, auth: %s",
            realm, authRequired);
    }

    @Override
    protected void stopListener() throws Exception {
        if (sipServer != null) {
            LOG.info("Stopping SIP server");
            sipServer.stop();
            sipServer = null;
        }
    }

    @Override
    protected void handleIncomingConnection(Object connection) {
        // Not used for SIP - handling is done through event handler
    }

    /**
     * Handle incoming SIP messages
     */
    private void handleSipMessage(SipMessage message) {
        LOG.debugf("Received SIP %s from %s",
            message.getMethod(), message.getFromAddress());

        // Validate source IP if configured
        if (allowedIps != null && !allowedIps.isEmpty()) {
            boolean allowed = allowedIps.stream()
                .anyMatch(ip -> message.getSourceIp().startsWith(ip));

            if (!allowed) {
                LOG.warnf("Rejected SIP message from unauthorized IP: %s",
                    message.getSourceIp());
                return;
            }
        }

        // Convert SIP message to pipeline event
        Map<String, Object> pipelineEvent = Map.of(
            "type", "sip",
            "method", message.getMethod(),
            "callId", message.getCallId(),
            "from", message.getFromAddress(),
            "to", message.getToAddress(),
            "timestamp", System.currentTimeMillis(),
            "data", message
        );

        // Trigger pipeline processing
        processEvent(pipelineEvent);
    }

    // Getters
    public String getTransport() {
        return transport;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public String getRealm() {
        return realm;
    }

    public boolean isAuthRequired() {
        return authRequired;
    }

    public List<String> getAllowedIps() {
        return allowedIps;
    }

    public boolean isRunning() {
        return sipServer != null && sipServer.isRunning();
    }
}