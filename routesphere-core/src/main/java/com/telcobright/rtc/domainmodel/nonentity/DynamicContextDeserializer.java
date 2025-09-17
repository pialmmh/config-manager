package com.telcobright.rtc.domainmodel.nonentity;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.telcobright.rtc.domainmodel.mysqlentity.*;
import com.telcobright.rtc.domainmodel.mysqlentity.sms.Campaign;
import com.telcobright.rtc.domainmodel.mysqlentity.sms.SmsQueue;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.*;

/**
 * Custom deserializer for DynamicContext that properly deserializes all fields.
 * Uses reflection to set final fields since DynamicContext is immutable.
 */
public class DynamicContextDeserializer extends JsonDeserializer<DynamicContext> {

    @Override
    public DynamicContext deserialize(JsonParser p, DeserializationContext ctxt)
            throws IOException {

        ObjectMapper mapper = (ObjectMapper) p.getCodec();
        JsonNode node = mapper.readTree(p);

        // Create empty DynamicContext instance
        DynamicContext context = new DynamicContext();

        try {
            // Use reflection to set the final fields
            setFieldValue(context, "callSourceMap", deserializeMap(node, "callSourceMap", mapper, new TypeReference<Map<Integer, CallSrc>>() {}));
            setFieldValue(context, "partners", deserializeMap(node, "partners", mapper, new TypeReference<Map<Integer, Partner>>() {}));
            setFieldValue(context, "prefixWisePartners", deserializeMap(node, "prefixWisePartners", mapper, new TypeReference<Map<String, Partner>>() {}));
            setFieldValue(context, "prefixWisePartnerPrefixes", deserializeMap(node, "prefixWisePartnerPrefixes", mapper, new TypeReference<Map<String, PartnerPrefix>>() {}));
            setFieldValue(context, "dppWiseDialplanMapping", deserializeMap(node, "dppWiseDialplanMapping", mapper, new TypeReference<Map<Integer, List<DialplanMapping>>>() {}));
            setFieldValue(context, "routeWisePartners", deserializeMap(node, "routeWisePartners", mapper, new TypeReference<Map<String, Partner>>() {}));
            setFieldValue(context, "callerIdWisePartners", deserializeMap(node, "callerIdWisePartners", mapper, new TypeReference<Map<String, Partner>>() {}));
            setFieldValue(context, "prefixVsPartners", deserializeMap(node, "prefixVsPartners", mapper, new TypeReference<Map<String, Partner>>() {}));
            setFieldValue(context, "sipAccountWisePartners", deserializeMap(node, "sipAccountWisePartners", mapper, new TypeReference<Map<String, Partner>>() {}));
            setFieldValue(context, "partnerWiseDidNumbers", deserializeMap(node, "partnerWiseDidNumbers", mapper, new TypeReference<Map<Integer, List<String>>>() {}));
            setFieldValue(context, "prefixWiseDialplanPrefixes", deserializeMap(node, "prefixWiseDialplanPrefixes", mapper, new TypeReference<Map<String, DialplanPrefix>>() {}));
            setFieldValue(context, "idVsDialplan", deserializeMap(node, "idVsDialplan", mapper, new TypeReference<Map<Integer, Dialplan>>() {}));
            setFieldValue(context, "rPartnerVsDidAssignments", deserializeMap(node, "rPartnerVsDidAssignments", mapper, new TypeReference<Map<String, List<DidAssignment>>>() {}));
            setFieldValue(context, "didNumbVsPartners", deserializeMap(node, "didNumbVsPartners", mapper, new TypeReference<Map<String, Partner>>() {}));
            setFieldValue(context, "didNumVsRpartners", deserializeMap(node, "didNumVsRpartners", mapper, new TypeReference<Map<String, List<RetailPartner>>>() {}));
            setFieldValue(context, "partnerWiseRatePlans", deserializeMap(node, "partnerWiseRatePlans", mapper, new TypeReference<Map<String, List<RatePlan>>>() {}));
            setFieldValue(context, "ratePlans", deserializeMap(node, "ratePlans", mapper, new TypeReference<Map<Integer, RatePlan>>() {}));
            setFieldValue(context, "ratePlanWiseTodaysRates", deserializeMap(node, "ratePlanWiseTodaysRates", mapper, new TypeReference<Map<Integer, Map<String, Rate>>>() {}));
            setFieldValue(context, "rateAssignsCustomer", deserializeList(node, "rateAssignsCustomer", mapper, new TypeReference<List<RateAssign>>() {}));
            setFieldValue(context, "rateAssignsSupplier", deserializeList(node, "rateAssignsSupplier", mapper, new TypeReference<List<RateAssign>>() {}));
            setFieldValue(context, "partnerVsRoutes", deserializeMap(node, "partnerVsRoutes", mapper, new TypeReference<Map<Integer, List<Route>>>() {}));
            setFieldValue(context, "campaigns", deserializeMap(node, "campaigns", mapper, new TypeReference<Map<Integer, Campaign>>() {}));
            setFieldValue(context, "smsQueue", deserializeMap(node, "smsQueue", mapper, new TypeReference<Map<Integer, SmsQueue>>() {}));
            setFieldValue(context, "enumJobStatus", deserializeMap(node, "enumJobStatus", mapper, new TypeReference<Map<String, Integer>>() {}));

        } catch (Exception e) {
            System.err.println("Error deserializing DynamicContext: " + e.getMessage());
            e.printStackTrace();
            // Return context with whatever fields were successfully set
        }

        return context;
    }

    private <T> T deserializeMap(JsonNode node, String fieldName, ObjectMapper mapper, TypeReference<T> typeRef) {
        if (node.has(fieldName) && !node.get(fieldName).isNull()) {
            try {
                return mapper.convertValue(node.get(fieldName), typeRef);
            } catch (Exception e) {
                System.err.println("Error deserializing field '" + fieldName + "': " + e.getMessage());
                // Return appropriate empty collection
                if (typeRef.getType().toString().contains("Map")) {
                    return (T) Collections.emptyMap();
                }
                return null;
            }
        }
        // Return appropriate empty collection if field is missing
        if (typeRef.getType().toString().contains("Map")) {
            return (T) Collections.emptyMap();
        }
        return null;
    }

    private <T> T deserializeList(JsonNode node, String fieldName, ObjectMapper mapper, TypeReference<T> typeRef) {
        if (node.has(fieldName) && !node.get(fieldName).isNull()) {
            try {
                return mapper.convertValue(node.get(fieldName), typeRef);
            } catch (Exception e) {
                System.err.println("Error deserializing field '" + fieldName + "': " + e.getMessage());
                return (T) Collections.emptyList();
            }
        }
        return (T) Collections.emptyList();
    }

    private void setFieldValue(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}