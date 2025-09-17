package com.telcobright.routesphere.pipeline.call.esl;

import io.quarkus.arc.Unremovable;
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
@Unremovable  // Prevent Quarkus from removing this bean during build-time optimization
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
            case "CHANNEL_PARK":
                handleChannelPark(channelId, eventData);
                break;
            case "CHANNEL_UNPARK":
                handleChannelUnpark(channelId, eventData);
                break;
            case "CHANNEL_BRIDGE":
                handleChannelBridge(channelId, eventData);
                break;
            case "CHANNEL_UNBRIDGE":
                handleChannelUnbridge(channelId, eventData);
                break;
            case "CHANNEL_EXECUTE":
                handleChannelExecute(channelId, eventData);
                break;
            case "CHANNEL_EXECUTE_COMPLETE":
                handleChannelExecuteComplete(channelId, eventData);
                break;
            case "CHANNEL_PROGRESS":
                handleChannelProgress(channelId, eventData);
                break;
            case "CHANNEL_PROGRESS_MEDIA":
                handleChannelProgressMedia(channelId, eventData);
                break;
            case "CHANNEL_OUTGOING":
                handleChannelOutgoing(channelId, eventData);
                break;
            case "CHANNEL_ORIGINATE":
                handleChannelOriginate(channelId, eventData);
                break;
            case "HEARTBEAT":
                // Log heartbeats at DEBUG level to reduce noise
                LOG.debugf("FreeSWITCH Heartbeat received");
                break;
            case "CUSTOM":
                handleCustomEvent(channelId, eventData);
                break;
            case "DTMF":
                handleDtmfEvent(channelId, eventData);
                break;
            case "RE_SCHEDULE":
                LOG.debugf("Re-schedule event received for channel: %s", channelId);
                break;
            case "API":
                handleApiEvent(channelId, eventData);
                break;
            default:
                // Log unknown events for discovery
                if (!eventType.equals("HEARTBEAT")) {
                    LOG.debugf("Unhandled event type: %s", eventType);
                }
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

    private void handleChannelPark(String channelId, Map<String, ?> eventData) {
        LOG.infof("⏸️ CALL PARKED - Channel: %s", channelId);

        if (eventData != null) {
            String parkingSlot = (String) eventData.get("variable_park_slot");
            String parkedBy = (String) eventData.get("variable_park_by");
            LOG.infof("   Parking Slot: %s, Parked By: %s", parkingSlot, parkedBy);
        }
    }

    private void handleChannelUnpark(String channelId, Map<String, ?> eventData) {
        LOG.infof("▶️ CALL UNPARKED - Channel: %s", channelId);

        if (eventData != null) {
            String retrievedBy = (String) eventData.get("variable_retrieved_by");
            LOG.infof("   Retrieved By: %s", retrievedBy);
        }
    }

    private void handleChannelBridge(String channelId, Map<String, ?> eventData) {
        LOG.infof("🔗 CALL BRIDGED - Channel: %s", channelId);

        if (eventData != null) {
            String otherLegUuid = (String) eventData.get("Other-Leg-Unique-ID");
            String bridgeState = (String) eventData.get("variable_bridge_channel");
            LOG.infof("   Other Leg: %s, Bridge Channel: %s",
                truncateString(otherLegUuid, 20), bridgeState);
        }
    }

    private void handleChannelUnbridge(String channelId, Map<String, ?> eventData) {
        LOG.infof("🔓 CALL UNBRIDGED - Channel: %s", channelId);

        if (eventData != null) {
            String unbridgeCause = (String) eventData.get("variable_bridge_hangup_cause");
            LOG.infof("   Unbridge Cause: %s", unbridgeCause);
        }
    }

    private void handleChannelExecute(String channelId, Map<String, ?> eventData) {
        if (eventData != null) {
            String application = (String) eventData.get("Application");
            String applicationData = (String) eventData.get("Application-Data");
            LOG.debugf("⚡ EXECUTING: %s(%s) on Channel: %s",
                application, applicationData, channelId);
        }
    }

    private void handleChannelExecuteComplete(String channelId, Map<String, ?> eventData) {
        if (eventData != null) {
            String application = (String) eventData.get("Application");
            String response = (String) eventData.get("Application-Response");
            LOG.debugf("✓ EXECUTED: %s - Response: %s, Channel: %s",
                application, response, channelId);
        }
    }

    private void handleChannelProgress(String channelId, Map<String, ?> eventData) {
        LOG.infof("📡 CALL PROGRESS - Channel: %s", channelId);

        if (eventData != null) {
            String progressIndication = (String) eventData.get("variable_progress_indication");
            LOG.infof("   Progress Indication: %s", progressIndication);
        }
    }

    private void handleChannelProgressMedia(String channelId, Map<String, ?> eventData) {
        LOG.infof("🎵 EARLY MEDIA - Channel: %s", channelId);

        if (eventData != null) {
            String codecName = (String) eventData.get("variable_rtp_use_codec_name");
            String codecRate = (String) eventData.get("variable_rtp_use_codec_rate");
            LOG.infof("   Codec: %s @ %s", codecName, codecRate);
        }
    }

    private void handleChannelOutgoing(String channelId, Map<String, ?> eventData) {
        LOG.infof("📤 OUTGOING CALL - Channel: %s", channelId);

        if (eventData != null) {
            String destinationNumber = (String) eventData.get("Caller-Destination-Number");
            String gateway = (String) eventData.get("variable_sip_gateway");
            LOG.infof("   To: %s via Gateway: %s", destinationNumber, gateway);
        }
    }

    private void handleChannelOriginate(String channelId, Map<String, ?> eventData) {
        LOG.infof("🚀 CALL ORIGINATED - Channel: %s", channelId);

        if (eventData != null) {
            String originateDisposition = (String) eventData.get("variable_originate_disposition");
            String dialedExtension = (String) eventData.get("variable_dialed_extension");
            LOG.infof("   Disposition: %s, Dialed: %s", originateDisposition, dialedExtension);
        }
    }

    private void handleDtmfEvent(String channelId, Map<String, ?> eventData) {
        if (eventData != null) {
            String dtmfDigit = (String) eventData.get("DTMF-Digit");
            String dtmfDuration = (String) eventData.get("DTMF-Duration");
            LOG.infof("☎️ DTMF RECEIVED - Digit: %s, Duration: %sms, Channel: %s",
                dtmfDigit, dtmfDuration, channelId);
        }
    }

    private void handleApiEvent(String channelId, Map<String, ?> eventData) {
        if (eventData != null) {
            String apiCommand = (String) eventData.get("API-Command");
            String apiResponse = (String) eventData.get("API-Response");
            LOG.debugf("🔌 API EVENT - Command: %s, Response: %s",
                apiCommand, truncateString(apiResponse, 100));
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