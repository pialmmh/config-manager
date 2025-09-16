package com.telcobright.routesphere.protocols.esl;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents an event received from FreeSWITCH ESL.
 */
public class EslEvent {

    private String eventName;
    private String channelId;
    private String callerId;
    private String destination;
    private long timestamp;
    private Map<String, String> eventData = new HashMap<>();

    public EslEvent(String eventName) {
        this.eventName = eventName;
        this.timestamp = System.currentTimeMillis();
    }

    // Builder pattern for easy event creation
    public static Builder builder(String eventName) {
        return new Builder(eventName);
    }

    public static class Builder {
        private EslEvent event;

        public Builder(String eventName) {
            this.event = new EslEvent(eventName);
        }

        public Builder channelId(String channelId) {
            event.channelId = channelId;
            return this;
        }

        public Builder callerId(String callerId) {
            event.callerId = callerId;
            return this;
        }

        public Builder destination(String destination) {
            event.destination = destination;
            return this;
        }

        public Builder data(String key, String value) {
            event.eventData.put(key, value);
            return this;
        }

        public Builder data(Map<String, String> data) {
            event.eventData.putAll(data);
            return this;
        }

        public EslEvent build() {
            return event;
        }
    }

    // Getters and setters
    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getCallerId() {
        return callerId;
    }

    public void setCallerId(String callerId) {
        this.callerId = callerId;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public Map<String, String> getEventData() {
        return eventData;
    }

    public void setEventData(Map<String, String> eventData) {
        this.eventData = eventData;
    }

    @Override
    public String toString() {
        return "EslEvent{" +
                "eventName='" + eventName + '\'' +
                ", channelId='" + channelId + '\'' +
                ", callerId='" + callerId + '\'' +
                ", destination='" + destination + '\'' +
                ", timestamp=" + timestamp +
                ", dataCount=" + eventData.size() +
                '}';
    }
}