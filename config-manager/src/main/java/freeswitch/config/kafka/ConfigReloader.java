package freeswitch.config.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import freeswitch.config.AppConfig;
import freeswitch.config.dynamic.ConfigManager;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import java.util.Set;


@Service
public class ConfigReloader {


    private final ConfigManager configManager;
    private final ConfigUpdateNotifier configUpdateNotifier;
    private final AppConfig appConfig;

    public ConfigReloader(ConfigManager configManager, ConfigUpdateNotifier configUpdateNotifier, AppConfig appConfig) {
        this.configManager = configManager;
        this.configUpdateNotifier = configUpdateNotifier;
        this.appConfig = appConfig;
    }


    @KafkaListener(topics = "all-mysql-changes", groupId = "configGroup")
    public void listenDbChanges(ConsumerRecord<String, String> record, Acknowledgment acknowledgment) {

        Set<String> excludedTables = appConfig.configReloadExclusionTables;

        try {
            String tableName = getTableNameFromRecord(record);
            if(tableName.startsWith("sip_capture") ||
                    tableName.startsWith("rtcp_capture") ||
                    tableName.startsWith("report_capture") ||
                    tableName.startsWith("logs_capture"))
                return;

            if (tableName != null && !excludedTables.contains(tableName)) {
                //System.out.println("Reloading configurations for table: " + tableName);
                configManager.loadConfigurations();
                configUpdateNotifier.publish();
            }

        } catch (Exception e) {
            e.printStackTrace(); // Use proper logging in production
        }
        acknowledgment.acknowledge();
    }

    private String getTableNameFromRecord(ConsumerRecord<String, String> record) throws JsonProcessingException {
        String tableName = null;
        ObjectMapper objectMapper = new ObjectMapper();
        if (record.value() == null) {
            // DELETE event: extract table from key
            JsonNode keyNode = objectMapper.readTree(record.key());
            String fullIdentifier = keyNode.path("payload").path("__dbz__physicalTableIdentifier").asText();
            tableName = extractTableName(fullIdentifier);
            //System.out.println("DELETE event for table: " + tableName);
        } else {
            // INSERT/UPDATE event: extract table from value
            JsonNode valueNode = objectMapper.readTree(record.value());
            tableName = valueNode.path("payload").path("source").path("table").asText();
            //System.out.println("INSERT/UPDATE event for table: " + tableName);
        }
        return tableName;
    }

    private String extractTableName(String fullIdentifier) {
        String[] parts = fullIdentifier.split("\\.");
        return parts.length == 3 ? parts[2] : fullIdentifier;
    }



}
