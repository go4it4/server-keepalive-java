package com.coding.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;

public final class JsonUtil {

    private JsonUtil() {
        throw new UnsupportedOperationException(this + "cannot be instantiated");
    }

    public static final ObjectMapper OBJECT_MAPPER = JsonMapper.builder()
            .configure(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN, true)
            .build();

    public static JsonNode parseObject(String json) {
        if (json == null || json.isEmpty()) {
            return null;
        }
        try {
            return OBJECT_MAPPER.readValue(json, JsonNode.class);
        } catch (Exception e) {
            return null;
        }
    }

    public static String toJsonString(Object value) {
        if (value == null) {
            return null;
        }
        try {
            return OBJECT_MAPPER.writeValueAsString(value);
        } catch (Exception e) {
            return null;
        }
    }

    public static String getText(JsonNode node, String fieldName) {
        if (node == null) {
            return null;
        }
        JsonNode jsonNode;
        try {
            jsonNode = node.get(fieldName);
        } catch (Exception e) {
            return null;
        }
        return toText(jsonNode);
    }

    private static String toText(JsonNode node) {
        if (node != null) {
            return node.asText();
        }
        return null;
    }

    public static long getLong(JsonNode node, String fieldName) {
        if (node == null) {
            return 0L;
        }
        JsonNode jsonNode;
        try {
            jsonNode = node.get(fieldName);
        } catch (Exception e) {
            return 0L;
        }

        return toLong(jsonNode);
    }

    private static long toLong(JsonNode node) {
        if (node != null) {
            return node.asLong();
        }
        return 0L;
    }

}
