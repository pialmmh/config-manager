package com.telcobright.routesphere.config.deployment;

import io.smallrye.config.WithName;
import java.util.Map;
import java.util.Optional;

/**
 * Socket Profile Configuration for protocol instances
 * Similar to FreeSWITCH profiles (external/internal)
 * Each protocol can have multiple socket instances
 */
public interface SocketProfileConfig {
    
    /**
     * Socket profile name (e.g., "external", "internal", "public", "private")
     */
    String name();
    
    /**
     * Protocol type (SIP, HTTP, HTTPS, SMS, ESL, DIAMETER, etc.)
     */
    String protocol();
    
    /**
     * Whether this socket is enabled
     */
    @WithName("enabled")
    Optional<Boolean> isEnabled();
    
    /**
     * Network binding configuration
     */
    NetworkConfig network();
    
    /**
     * Security configuration
     */
    Optional<SecurityConfig> security();
    
    /**
     * Protocol-specific settings
     */
    Map<String, String> settings();
    
    /**
     * Socket metadata
     */
    Map<String, String> metadata();
    
    /**
     * Network Configuration
     */
    interface NetworkConfig {
        
        /**
         * Bind address (IP or hostname)
         */
        @WithName("bind-address")
        String bindAddress();
        
        /**
         * Bind port
         */
        @WithName("bind-port")
        Integer bindPort();
        
        /**
         * External/Public IP (for NAT scenarios)
         */
        @WithName("external-ip")
        Optional<String> externalIp();
        
        /**
         * External/Public port (for NAT scenarios)
         */
        @WithName("external-port")
        Optional<Integer> externalPort();
        
        /**
         * Transport protocol (UDP, TCP, TLS, WS, WSS)
         */
        Optional<String> transport();
        
        /**
         * Network interface to bind to
         */
        Optional<String> iface();
        
        /**
         * IP version (4, 6, or both)
         */
        @WithName("ip-version")
        Optional<String> ipVersion();
        
        /**
         * Connection limits
         */
        Optional<ConnectionLimits> limits();
        
        interface ConnectionLimits {
            @WithName("max-connections")
            Optional<Integer> maxConnections();
            
            @WithName("max-connections-per-ip")
            Optional<Integer> maxConnectionsPerIp();
            
            @WithName("connection-timeout")
            Optional<String> connectionTimeout();
            
            @WithName("idle-timeout")
            Optional<String> idleTimeout();
            
            @WithName("rate-limit")
            Optional<Integer> rateLimit();
        }
    }
    
    /**
     * Security Configuration
     */
    interface SecurityConfig {
        
        /**
         * TLS/SSL configuration
         */
        Optional<TlsConfig> tls();
        
        /**
         * Authentication configuration
         */
        Optional<AuthConfig> auth();
        
        /**
         * Access control configuration
         */
        Optional<AccessControl> access();
        
        interface TlsConfig {
            @WithName("enabled")
            Optional<Boolean> enabled();
            
            @WithName("cert-file")
            Optional<String> certFile();
            
            @WithName("key-file")
            Optional<String> keyFile();
            
            @WithName("ca-file")
            Optional<String> caFile();
            
            @WithName("verify-client")
            Optional<Boolean> verifyClient();
            
            @WithName("protocols")
            Optional<String> protocols();
            
            @WithName("ciphers")
            Optional<String> ciphers();
        }
        
        interface AuthConfig {
            @WithName("type")
            Optional<String> authType();
            
            @WithName("realm")
            Optional<String> realm();
            
            @WithName("require-auth")
            Optional<Boolean> requireAuth();
            
            @WithName("auth-methods")
            Optional<String> authMethods();
            
            @WithName("nonce-ttl")
            Optional<String> nonceTtl();
        }
        
        interface AccessControl {
            @WithName("allow-ips")
            Optional<String> allowIps();
            
            @WithName("deny-ips")
            Optional<String> denyIps();
            
            @WithName("allow-networks")
            Optional<String> allowNetworks();
            
            @WithName("deny-networks")
            Optional<String> denyNetworks();
            
            @WithName("require-registration")
            Optional<Boolean> requireRegistration();
        }
    }
}