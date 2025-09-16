package com.telcobright.routesphere.pipeline.call.esl;

import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * Processes call events received from FreeSWITCH ESL.
 * This class handles incoming call events, prints them for debugging,
 * and will be gradually developed to process call flows.
 */
@ApplicationScoped
public class CallEventProcessor {

    private static final Logger LOG = Logger.getLogger(CallEventProcessor.class);
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    /**
     * Process a call event received from FreeSWITCH
     */
    public void processCallEvent(Map<String, Object> event) {
        String eventType = (String) event.get("eventName");
        String channelId = (String) event.get("channelId");
        Long timestamp = (Long) event.get("timestamp");

        // Print event header
        LOG.info("╔════════════════════════════════════════════════════════════════╗");
        LOG.infof("║ FreeSWITCH Event Received: %-35s ║", eventType != null ? eventType : "UNKNOWN");
        LOG.info("╠════════════════════════════════════════════════════════════════╣");

        // Print timestamp
        if (timestamp != null) {
            LocalDateTime eventTime = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(timestamp), ZoneId.systemDefault()
            );
            LOG.infof("║ Time: %-57s ║", eventTime.format(TIME_FORMATTER));
        }

        // Print channel ID
        if (channelId != null) {
            LOG.infof("║ Channel ID: %-51s ║", truncateString(channelId, 51));
        }

        // Print event data
        Map<String, ?> eventData = (Map<String, ?>) event.get("data");
        if (eventData != null && !eventData.isEmpty()) {
            LOG.info("║                                                                  ║");
            LOG.info("║ Event Data:                                                     ║");
            LOG.info("║ ──────────────────────────────────────────────────────────────  ║");

            eventData.forEach((key, value) -> {
                if (value != null && isImportantField(key)) {
                    String formattedKey = formatFieldName(key);
                    String formattedValue = truncateString(value.toString(), 40);
                    LOG.infof("║   %-25s : %-35s ║", formattedKey, formattedValue);
                }
            });
        }

        LOG.info("╚════════════════════════════════════════════════════════════════╝");

        // Handle specific event types
        handleSpecificEvent(eventType, channelId, eventData);
    }

    /**
     * Handle specific event types with custom logic
     */
    private void handleSpecificEvent(String eventType, String channelId, Map<String, ?> eventData) {
        if (eventType == null) {
            return;
        }

        switch (eventType) {
            case "CHANNEL_CREATE":
                handleChannelCreate(channelId, eventData);
                break;
            case "CHANNEL_ANSWER":
                handleChannelAnswer(channelId, eventData);
                break;
            case "CHANNEL_HANGUP":
                handleChannelHangup(channelId, eventData);
                break;
            case "CHANNEL_HANGUP_COMPLETE":
                handleChannelHangupComplete(channelId, eventData);
                break;
            case "HEARTBEAT":
                // Log heartbeats at DEBUG level to reduce noise
                LOG.debugf("FreeSWITCH Heartbeat received");
                break;
            case "CUSTOM":
                handleCustomEvent(channelId, eventData);
                break;
            default:
                LOG.debugf("Unhandled event type: %s", eventType);
        }
    }

    private void handleChannelCreate(String channelId, Map<String, ?> eventData) {
        LOG.infof("📞 NEW CALL INITIATED - Channel: %s", channelId);

        if (eventData != null) {
            String callerIdNumber = (String) eventData.get("Caller-Caller-ID-Number");
            String destinationNumber = (String) eventData.get("Caller-Destination-Number");
            String channelState = (String) eventData.get("Channel-State");

            LOG.infof("   From: %s → To: %s (State: %s)",
                callerIdNumber, destinationNumber, channelState);
        }
    }

    private void handleChannelAnswer(String channelId, Map<String, ?> eventData) {
        LOG.infof("✅ CALL ANSWERED - Channel: %s", channelId);

        if (eventData != null) {
            String answerState = (String) eventData.get("Answer-State");
            String channelName = (String) eventData.get("Channel-Name");
            LOG.infof("   Answer State: %s, Channel: %s", answerState, channelName);
        }
    }

    private void handleChannelHangup(String channelId, Map<String, ?> eventData) {
        LOG.infof("📴 CALL HANGUP INITIATED - Channel: %s", channelId);

        if (eventData != null) {
            String hangupCause = (String) eventData.get("Hangup-Cause");
            String channelState = (String) eventData.get("Channel-State");
            LOG.infof("   Cause: %s, State: %s", hangupCause, channelState);
        }
    }

    private void handleChannelHangupComplete(String channelId, Map<String, ?> eventData) {
        LOG.infof("🔚 CALL ENDED - Channel: %s", channelId);

        if (eventData != null) {
            String hangupCause = (String) eventData.get("Hangup-Cause");
            String duration = (String) eventData.get("variable_duration");
            String billsec = (String) eventData.get("variable_billsec");

            LOG.infof("   Duration: %ss, Billable: %ss, Cause: %s",
                duration, billsec, hangupCause);
        }
    }

    private void handleCustomEvent(String channelId, Map<String, ?> eventData) {
        if (eventData != null) {
            String subclass = (String) eventData.get("Event-Subclass");
            LOG.infof("🔧 CUSTOM EVENT - Subclass: %s, Channel: %s", subclass, channelId);
        }
    }

    /**
     * Check if a field is important enough to display
     */
    private boolean isImportantField(String fieldName) {
        // List of important fields to display
        String[] importantFields = {
            "Caller-Caller-ID-Number", "Caller-Caller-ID-Name",
            "Caller-Destination-Number", "Channel-State",
            "Channel-Name", "Call-Direction", "Answer-State",
            "Hangup-Cause", "Event-Subclass", "variable_duration",
            "variable_billsec", "variable_sip_term_status",
            "variable_sip_hangup_disposition"
        };

        for (String field : importantFields) {
            if (fieldName.equals(field)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Format field names for better readability
     */
    private String formatFieldName(String fieldName) {
        // Remove common prefixes
        String formatted = fieldName
            .replace("Caller-", "")
            .replace("Channel-", "")
            .replace("variable_", "");

        // Convert to title case
        String[] parts = formatted.split("-");
        StringBuilder result = new StringBuilder();
        for (String part : parts) {
            if (result.length() > 0) result.append(" ");
            if (part.length() > 0) {
                result.append(part.substring(0, 1).toUpperCase());
                if (part.length() > 1) {
                    result.append(part.substring(1).toLowerCase());
                }
            }
        }
        return result.toString();
    }

    /**
     * Truncate a string to fit within display width
     */
    private String truncateString(String str, int maxLength) {
        if (str == null) {
            return "";
        }
        if (str.length() <= maxLength) {
            return str;
        }
        return str.substring(0, maxLength - 3) + "...";
    }
}