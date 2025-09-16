package com.telcobright.routesphere.protocols.kafka;

import com.telcobright.routesphere.protocols.base.ClientChannel;
import com.telcobright.routesphere.protocols.base.ChannelConfig;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Kafka Channel implementation.
 * Connects to Kafka broker and consumes events from configured topics.
 */
@ApplicationScoped
public class KafkaChannel extends ClientChannel {

    private KafkaConsumerClient kafkaConsumer;
    private String bootstrapServers;
    private String groupId;
    private List<String> topics;
    private int pollTimeout = 1000;
    private int maxPollRecords = 100;
    private ExecutorService pollExecutor;
    private volatile boolean polling = false;

    public KafkaChannel() {
        super("kafka-default", "kafka", new ChannelConfig());
    }

    public KafkaChannel(String name, ChannelConfig config) {
        super(name, "kafka", config);

        // Extract Kafka-specific configuration
        if (config.getConnectionConfig() != null) {
            Map<String, Object> conn = config.getConnectionConfig();
            this.bootstrapServers = (String) conn.get("bootstrap-servers");
            this.groupId = (String) conn.get("group-id");
        }

        if (config.getProtocolSpecificConfig() != null) {
            Map<String, Object> consumer = (Map<String, Object>) config.getProtocolSpecificConfig().get("consumer");
            if (consumer != null) {
                this.topics = (List<String>) consumer.get("topics");

                Object timeout = consumer.get("poll-timeout");
                if (timeout != null) {
                    this.pollTimeout = timeout instanceof Integer ? (Integer) timeout : Integer.parseInt(timeout.toString());
                }

                Object maxRecords = consumer.get("max-poll-records");
                if (maxRecords != null) {
                    this.maxPollRecords = maxRecords instanceof Integer ? (Integer) maxRecords : Integer.parseInt(maxRecords.toString());
                }
            }
        }
    }

    @Override
    protected void connect() throws Exception {
        LOG.infof("Connecting to Kafka broker at %s", bootstrapServers);

        kafkaConsumer = new KafkaConsumerClient(bootstrapServers, groupId, topics);
        kafkaConsumer.setEventHandler(this::handleKafkaEvent);
        kafkaConsumer.connect();

        LOG.infof("Subscribed to Kafka topics: %s", topics);
    }

    @Override
    protected void disconnect() throws Exception {
        if (kafkaConsumer != null) {
            LOG.info("Disconnecting from Kafka broker");
            kafkaConsumer.disconnect();
            kafkaConsumer = null;
        }
    }

    @Override
    protected boolean shouldStartPolling() {
        return true; // Kafka requires polling
    }

    @Override
    protected void startPolling() {
        if (kafkaConsumer != null && !polling) {
            LOG.info("Starting Kafka consumer polling");
            polling = true;
            pollExecutor = Executors.newSingleThreadExecutor(r -> {
                Thread t = new Thread(r, "kafka-poll-" + name);
                t.setDaemon(true);
                return t;
            });

            pollExecutor.submit(this::pollLoop);
        }
    }

    @Override
    protected void stopPolling() {
        if (polling) {
            LOG.info("Stopping Kafka consumer polling");
            polling = false;
            if (pollExecutor != null) {
                pollExecutor.shutdown();
                pollExecutor = null;
            }
        }
    }

    /**
     * Main polling loop for Kafka consumer
     */
    private void pollLoop() {
        while (polling && status == ChannelStatus.RUNNING) {
            try {
                kafkaConsumer.poll(pollTimeout);
            } catch (Exception e) {
                LOG.errorf("Error polling Kafka: %s", e.getMessage());
                if (status == ChannelStatus.RUNNING) {
                    handleConnectionLoss();
                }
                break;
            }
        }
    }

    /**
     * Handle incoming Kafka events
     */
    private void handleKafkaEvent(KafkaEvent event) {
        LOG.debugf("Received Kafka event from topic %s partition %d",
            event.getTopic(), event.getPartition());

        // Convert Kafka event to pipeline event
        Map<String, Object> pipelineEvent = Map.of(
            "type", "kafka",
            "topic", event.getTopic(),
            "partition", event.getPartition(),
            "offset", event.getOffset(),
            "key", event.getKey() != null ? event.getKey() : "",
            "value", event.getValue(),
            "timestamp", event.getTimestamp()
        );

        // Trigger pipeline processing
        processEvent(pipelineEvent);
    }

    // Getters
    public boolean isConnected() {
        return kafkaConsumer != null && kafkaConsumer.isConnected();
    }

    public List<String> getTopics() {
        return topics;
    }

    public String getGroupId() {
        return groupId;
    }
}