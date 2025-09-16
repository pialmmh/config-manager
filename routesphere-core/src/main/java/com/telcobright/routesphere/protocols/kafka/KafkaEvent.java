package com.telcobright.routesphere.protocols.kafka;

/**
 * Represents an event received from Kafka.
 */
public class KafkaEvent {

    private String topic;
    private int partition;
    private long offset;
    private String key;
    private String value;
    private long timestamp;
    private String eventType;

    public KafkaEvent(String topic, int partition, long offset) {
        this.topic = topic;
        this.partition = partition;
        this.offset = offset;
        this.timestamp = System.currentTimeMillis();
    }

    // Builder pattern for easy event creation
    public static Builder builder(String topic, int partition, long offset) {
        return new Builder(topic, partition, offset);
    }

    public static class Builder {
        private KafkaEvent event;

        public Builder(String topic, int partition, long offset) {
            this.event = new KafkaEvent(topic, partition, offset);
        }

        public Builder key(String key) {
            event.key = key;
            return this;
        }

        public Builder value(String value) {
            event.value = value;
            return this;
        }

        public Builder eventType(String eventType) {
            event.eventType = eventType;
            return this;
        }

        public Builder timestamp(long timestamp) {
            event.timestamp = timestamp;
            return this;
        }

        public KafkaEvent build() {
            return event;
        }
    }

    // Getters and setters
    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public int getPartition() {
        return partition;
    }

    public void setPartition(int partition) {
        this.partition = partition;
    }

    public long getOffset() {
        return offset;
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    @Override
    public String toString() {
        return "KafkaEvent{" +
                "topic='" + topic + '\'' +
                ", partition=" + partition +
                ", offset=" + offset +
                ", key='" + key + '\'' +
                ", eventType='" + eventType + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}