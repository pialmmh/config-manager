package com.telcobright.routesphere.protocols.sip;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a SIP message.
 */
public class SipMessage {

    private String method; // INVITE, REGISTER, BYE, etc.
    private String callId;
    private String fromAddress;
    private String toAddress;
    private String viaHeader;
    private String contact;
    private String sourceIp;
    private int sourcePort;
    private long timestamp;
    private Map<String, String> headers = new HashMap<>();
    private String body; // SDP or other content

    public SipMessage(String method, String callId) {
        this.method = method;
        this.callId = callId;
        this.timestamp = System.currentTimeMillis();
    }

    // Builder pattern for easy message creation
    public static Builder builder(String method, String callId) {
        return new Builder(method, callId);
    }

    public static class Builder {
        private SipMessage message;

        public Builder(String method, String callId) {
            this.message = new SipMessage(method, callId);
        }

        public Builder from(String fromAddress) {
            message.fromAddress = fromAddress;
            return this;
        }

        public Builder to(String toAddress) {
            message.toAddress = toAddress;
            return this;
        }

        public Builder via(String viaHeader) {
            message.viaHeader = viaHeader;
            return this;
        }

        public Builder contact(String contact) {
            message.contact = contact;
            return this;
        }

        public Builder sourceIp(String sourceIp) {
            message.sourceIp = sourceIp;
            return this;
        }

        public Builder sourcePort(int sourcePort) {
            message.sourcePort = sourcePort;
            return this;
        }

        public Builder header(String name, String value) {
            message.headers.put(name, value);
            return this;
        }

        public Builder headers(Map<String, String> headers) {
            message.headers.putAll(headers);
            return this;
        }

        public Builder body(String body) {
            message.body = body;
            return this;
        }

        public SipMessage build() {
            return message;
        }
    }

    // Getters and setters
    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getCallId() {
        return callId;
    }

    public void setCallId(String callId) {
        this.callId = callId;
    }

    public String getFromAddress() {
        return fromAddress;
    }

    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }

    public String getToAddress() {
        return toAddress;
    }

    public void setToAddress(String toAddress) {
        this.toAddress = toAddress;
    }

    public String getViaHeader() {
        return viaHeader;
    }

    public void setViaHeader(String viaHeader) {
        this.viaHeader = viaHeader;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getSourceIp() {
        return sourceIp;
    }

    public void setSourceIp(String sourceIp) {
        this.sourceIp = sourceIp;
    }

    public int getSourcePort() {
        return sourcePort;
    }

    public void setSourcePort(int sourcePort) {
        this.sourcePort = sourcePort;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "SipMessage{" +
                "method='" + method + '\'' +
                ", callId='" + callId + '\'' +
                ", from='" + fromAddress + '\'' +
                ", to='" + toAddress + '\'' +
                ", sourceIp='" + sourceIp + '\'' +
                ", timestamp=" + timestamp +
                ", hasBody=" + (body != null) +
                '}';
    }
}