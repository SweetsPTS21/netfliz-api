package com.netfliz.netfliz.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ValidationException;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Component;

@Component
public class JsonUtils {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static JsonNode parse(String jsonString) {
        if (Strings.isBlank(jsonString)) {
            throw new ValidationException("Json parse error: Config is empty");
        }

        try {
            return objectMapper.readTree(jsonString);
        } catch (Exception e) {
            throw new ValidationException("Json parse error: Config is invalid");
        }
    }

    public static String serialize(JsonNode jsonNode) {
        try {
            return objectMapper.writeValueAsString(jsonNode);
        } catch (Exception e) {
            throw new ValidationException("Json serialize error: Config is invalid");
        }
    }

    public static boolean validJson(String jsonString) {
        try {
            objectMapper.readTree(jsonString);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
