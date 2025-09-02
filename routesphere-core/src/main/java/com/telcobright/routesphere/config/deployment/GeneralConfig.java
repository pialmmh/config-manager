package com.telcobright.routesphere.config.deployment;

import io.smallrye.config.WithName;
import java.util.Map;
import java.util.Optional;

/**
 * General configuration containing database, kafka, and other settings
 * Used within each profile (dev, staging, prod, mock)
 */
public interface GeneralConfig {
    
    /**
     * Database configuration
     */
    DatabaseConfig database();
    
    /**
     * Kafka configuration
     */
    KafkaConfig kafka();
    
    /**
     * Redis configuration
     */
    Optional<RedisConfig> redis();
    
    /**
     * ConfigManager API configuration
     */
    ConfigManagerConfig configManager();
    
    /**
     * Monitoring and metrics configuration
     */
    Optional<MonitoringConfig> monitoring();
    
    /**
     * Custom properties
     */
    Map<String, String> properties();
    
    /**
     * Database Configuration
     */
    interface DatabaseConfig {
        
        /**
         * Database type (mysql, postgresql, oracle)
         */
        @WithName("type")
        Optional<String> dbType();
        
        /**
         * JDBC URL
         */
        String url();
        
        /**
         * Database host
         */
        Optional<String> host();
        
        /**
         * Database port
         */
        Optional<Integer> port();
        
        /**
         * Database name
         */
        @WithName("name")
        Optional<String> databaseName();
        
        /**
         * Username
         */
        String username();
        
        /**
         * Password
         */
        String password();
        
        /**
         * Connection pool configuration
         */
        Optional<PoolConfig> pool();
        
        /**
         * Additional JDBC properties
         */
        Map<String, String> properties();
        
        interface PoolConfig {
            @WithName("min-size")
            Optional<Integer> minSize();
            
            @WithName("max-size")
            Optional<Integer> maxSize();
            
            @WithName("initial-size")
            Optional<Integer> initialSize();
            
            @WithName("max-lifetime")
            Optional<String> maxLifetime();
            
            @WithName("idle-timeout")
            Optional<String> idleTimeout();
        }
    }
    
    /**
     * Kafka Configuration
     */
    interface KafkaConfig {
        
        /**
         * Bootstrap servers
         */
        @WithName("bootstrap-servers")
        String bootstrapServers();
        
        /**
         * Consumer group ID
         */
        @WithName("group-id")
        Optional<String> groupId();
        
        /**
         * Topics configuration
         */
        TopicsConfig topics();
        
        /**
         * Producer configuration
         */
        Optional<ProducerConfig> producer();
        
        /**
         * Consumer configuration
         */
        Optional<ConsumerConfig> consumer();
        
        /**
         * Security configuration
         */
        Optional<SecurityConfig> security();
        
        interface TopicsConfig {
            @WithName("cdr")
            Optional<String> cdrTopic();
            
            @WithName("events")
            Optional<String> eventsTopic();
            
            @WithName("billing")
            Optional<String> billingTopic();
            
            @WithName("routing")
            Optional<String> routingTopic();
            
            Map<String, String> custom();
        }
        
        interface ProducerConfig {
            @WithName("acks")
            Optional<String> acks();
            
            @WithName("retries")
            Optional<Integer> retries();
            
            @WithName("batch-size")
            Optional<Integer> batchSize();
            
            @WithName("linger-ms")
            Optional<Integer> lingerMs();
            
            @WithName("compression-type")
            Optional<String> compressionType();
        }
        
        interface ConsumerConfig {
            @WithName("auto-offset-reset")
            Optional<String> autoOffsetReset();
            
            @WithName("enable-auto-commit")
            Optional<Boolean> enableAutoCommit();
            
            @WithName("max-poll-records")
            Optional<Integer> maxPollRecords();
            
            @WithName("session-timeout-ms")
            Optional<Integer> sessionTimeoutMs();
        }
        
        interface SecurityConfig {
            @WithName("protocol")
            Optional<String> securityProtocol();
            
            @WithName("sasl-mechanism")
            Optional<String> saslMechanism();
            
            @WithName("sasl-jaas-config")
            Optional<String> saslJaasConfig();
        }
    }
    
    /**
     * Redis Configuration
     */
    interface RedisConfig {
        
        /**
         * Redis host
         */
        String host();
        
        /**
         * Redis port
         */
        Optional<Integer> port();
        
        /**
         * Password
         */
        Optional<String> password();
        
        /**
         * Database number
         */
        Optional<Integer> database();
        
        /**
         * Connection timeout
         */
        @WithName("timeout")
        Optional<String> connectionTimeout();
        
        /**
         * Pool configuration
         */
        Optional<RedisPoolConfig> pool();
        
        /**
         * Cluster configuration
         */
        Optional<ClusterConfig> cluster();
        
        interface RedisPoolConfig {
            @WithName("max-total")
            Optional<Integer> maxTotal();
            
            @WithName("max-idle")
            Optional<Integer> maxIdle();
            
            @WithName("min-idle")
            Optional<Integer> minIdle();
        }
        
        interface ClusterConfig {
            @WithName("nodes")
            Optional<String> nodes();
            
            @WithName("max-redirects")
            Optional<Integer> maxRedirects();
        }
    }
    
    /**
     * ConfigManager API Configuration
     */
    interface ConfigManagerConfig {
        
        /**
         * Base URL for ConfigManager API
         */
        @WithName("base-url")
        String baseUrl();
        
        /**
         * Tenant root endpoint
         */
        @WithName("tenant-endpoint")
        Optional<String> tenantEndpoint();
        
        /**
         * API timeout
         */
        Optional<String> timeout();
        
        /**
         * Authentication configuration
         */
        Optional<AuthConfig> auth();
        
        /**
         * Retry configuration
         */
        Optional<RetryConfig> retry();
        
        interface AuthConfig {
            @WithName("type")
            Optional<String> authType();
            
            Optional<String> username();
            
            Optional<String> password();
            
            Optional<String> token();
            
            @WithName("api-key")
            Optional<String> apiKey();
        }
        
        interface RetryConfig {
            @WithName("max-attempts")
            Optional<Integer> maxAttempts();
            
            @WithName("delay")
            Optional<String> delay();
            
            @WithName("max-delay")
            Optional<String> maxDelay();
        }
    }
    
    /**
     * Monitoring Configuration
     */
    interface MonitoringConfig {
        
        /**
         * Metrics configuration
         */
        MetricsConfig metrics();
        
        /**
         * Health check configuration
         */
        HealthConfig health();
        
        /**
         * Logging configuration
         */
        LoggingConfig logging();
        
        interface MetricsConfig {
            @WithName("enabled")
            Optional<Boolean> enabled();
            
            @WithName("export-interval")
            Optional<String> exportInterval();
            
            @WithName("prometheus-endpoint")
            Optional<String> prometheusEndpoint();
        }
        
        interface HealthConfig {
            @WithName("enabled")
            Optional<Boolean> enabled();
            
            @WithName("liveness-path")
            Optional<String> livenessPath();
            
            @WithName("readiness-path")
            Optional<String> readinessPath();
        }
        
        interface LoggingConfig {
            @WithName("level")
            Optional<String> level();
            
            @WithName("format")
            Optional<String> format();
            
            @WithName("file-path")
            Optional<String> filePath();
        }
    }
}