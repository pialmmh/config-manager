package com.telcobright.routesphere.protocols.base;

import io.quarkus.runtime.Startup;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.jboss.logging.Logger;

/**
 * Abstract base class for all channels (communication pathways).
 * Channels are entry points for events that trigger pipeline processing.
 *
 * Two types of channels:
 * - ServerChannel: Listens on IP:Port for incoming connections
 * - ClientChannel: Connects to external servers to receive events
 */
public abstract class AbstractChannel {

    protected static final Logger LOG = Logger.getLogger(AbstractChannel.class);

    protected String name;
    protected String protocol;
    protected boolean enabled;
    protected ChannelConfig config;
    protected ChannelStatus status = ChannelStatus.STOPPED;

    /**
     * Channel operating modes
     */
    public enum ChannelMode {
        SERVER,  // Listens on IP:Port
        CLIENT   // Connects to external server
    }

    /**
     * Channel status
     */
    public enum ChannelStatus {
        STOPPED,
        STARTING,
        RUNNING,
        STOPPING,
        ERROR
    }

    protected AbstractChannel(String name, String protocol, ChannelConfig config) {
        this.name = name;
        this.protocol = protocol;
        this.config = config;
        this.enabled = config.isEnabled();
    }

    /**
     * Initialize the channel
     */
    @PostConstruct
    public void initialize() {
        if (enabled) {
            LOG.infof("Initializing %s channel: %s", protocol, name);
            try {
                status = ChannelStatus.STARTING;
                doInitialize();
                status = ChannelStatus.RUNNING;
                LOG.infof("Successfully initialized %s channel: %s", protocol, name);
            } catch (Exception e) {
                status = ChannelStatus.ERROR;
                LOG.errorf("Failed to initialize %s channel %s: %s", protocol, name, e.getMessage());
            }
        } else {
            LOG.debugf("Channel %s is disabled", name);
        }
    }

    /**
     * Shutdown the channel
     */
    @PreDestroy
    public void shutdown() {
        if (status == ChannelStatus.RUNNING) {
            LOG.infof("Shutting down %s channel: %s", protocol, name);
            try {
                status = ChannelStatus.STOPPING;
                doShutdown();
                status = ChannelStatus.STOPPED;
                LOG.infof("Successfully shut down %s channel: %s", protocol, name);
            } catch (Exception e) {
                LOG.errorf("Error shutting down %s channel %s: %s", protocol, name, e.getMessage());
            }
        }
    }

    /**
     * Process an incoming event and trigger pipeline
     */
    protected void processEvent(Object event) {
        LOG.debugf("Channel %s received event: %s", name, event.getClass().getSimpleName());

        // TODO: Trigger pipeline processing
        // This will be implemented when pipeline framework is ready
        String pipelineName = config.getPipelineName();
        if (pipelineName != null) {
            LOG.debugf("Triggering pipeline: %s", pipelineName);
            // pipelineService.execute(pipelineName, event);
        }
    }

    /**
     * Get the channel mode (SERVER or CLIENT)
     */
    public abstract ChannelMode getMode();

    /**
     * Channel-specific initialization
     */
    protected abstract void doInitialize() throws Exception;

    /**
     * Channel-specific shutdown
     */
    protected abstract void doShutdown() throws Exception;

    // Getters
    public String getName() {
        return name;
    }

    public String getProtocol() {
        return protocol;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public ChannelStatus getStatus() {
        return status;
    }

    public ChannelConfig getConfig() {
        return config;
    }
}