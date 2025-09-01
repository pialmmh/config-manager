package com.telcobright.routesphere.protocols;

/**
 * Base protocol interface for all supported protocols in RouteSphere
 */
public enum Protocol {
    HTTP("http", 80),
    HTTPS("https", 443),
    SIP_UDP("sip-udp", 5060),
    SIP_TCP("sip-tcp", 5060),
    SIP_TLS("sip-tls", 5061),
    SMS("sms", 0),  // SMS doesn't have a standard port
    ESL("esl", 8021);  // FreeSWITCH Event Socket Layer
    
    private final String name;
    private final int defaultPort;
    
    Protocol(String name, int defaultPort) {
        this.name = name;
        this.defaultPort = defaultPort;
    }
    
    public String getName() {
        return name;
    }
    
    public int getDefaultPort() {
        return defaultPort;
    }
}