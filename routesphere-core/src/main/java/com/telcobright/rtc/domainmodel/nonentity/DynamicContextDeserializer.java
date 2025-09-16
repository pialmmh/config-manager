package com.telcobright.rtc.domainmodel.nonentity;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;

/**
 * Custom deserializer for DynamicContext that always returns an empty immutable instance.
 * This ensures the object remains read-only for pipeline processing.
 */
public class DynamicContextDeserializer extends JsonDeserializer<DynamicContext> {

    @Override
    public DynamicContext deserialize(JsonParser p, DeserializationContext ctxt)
            throws IOException {
        // Skip the entire JSON content
        p.skipChildren();

        // Always return an empty immutable DynamicContext
        return new DynamicContext();
    }
}