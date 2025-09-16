package com.telcobright.routesphere.protocols.http;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents an HTTP request event.
 */
public class HttpEvent {

    private String method;
    private String path;
    private String body;
    private String remoteAddress;
    private long timestamp;
    private List<Map.Entry<String, String>> headers = new ArrayList<>();
    private List<Map.Entry<String, String>> queryParams = new ArrayList<>();

    public HttpEvent() {
        this.timestamp = System.currentTimeMillis();
    }

    // Builder pattern for easy event creation
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private HttpEvent event;

        public Builder() {
            this.event = new HttpEvent();
        }

        public Builder method(String method) {
            event.method = method;
            return this;
        }

        public Builder path(String path) {
            event.path = path;
            return this;
        }

        public Builder body(String body) {
            event.body = body;
            return this;
        }

        public Builder remoteAddress(String remoteAddress) {
            event.remoteAddress = remoteAddress;
            return this;
        }

        public Builder headers(List<Map.Entry<String, String>> headers) {
            if (headers != null) {
                event.headers.addAll(headers);
            }
            return this;
        }

        public Builder queryParams(List<Map.Entry<String, String>> queryParams) {
            if (queryParams != null) {
                event.queryParams.addAll(queryParams);
            }
            return this;
        }

        public HttpEvent build() {
            return event;
        }
    }

    // Convert headers to map for easier access
    public Map<String, String> getHeadersAsMap() {
        Map<String, String> map = new HashMap<>();
        for (Map.Entry<String, String> entry : headers) {
            map.put(entry.getKey(), entry.getValue());
        }
        return map;
    }

    // Convert query params to map for easier access
    public Map<String, String> getQueryParamsAsMap() {
        Map<String, String> map = new HashMap<>();
        for (Map.Entry<String, String> entry : queryParams) {
            map.put(entry.getKey(), entry.getValue());
        }
        return map;
    }

    // Getters and setters
    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getRemoteAddress() {
        return remoteAddress;
    }

    public void setRemoteAddress(String remoteAddress) {
        this.remoteAddress = remoteAddress;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public List<Map.Entry<String, String>> getHeaders() {
        return headers;
    }

    public void setHeaders(List<Map.Entry<String, String>> headers) {
        this.headers = headers;
    }

    public List<Map.Entry<String, String>> getQueryParams() {
        return queryParams;
    }

    public void setQueryParams(List<Map.Entry<String, String>> queryParams) {
        this.queryParams = queryParams;
    }

    @Override
    public String toString() {
        return "HttpEvent{" +
                "method='" + method + '\'' +
                ", path='" + path + '\'' +
                ", remoteAddress='" + remoteAddress + '\'' +
                ", timestamp=" + timestamp +
                ", hasBody=" + (body != null) +
                ", headerCount=" + headers.size() +
                ", queryParamCount=" + queryParams.size() +
                '}';
    }
}