package com.netfliz.netfliz.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netfliz.netfliz.constant.StringPools;
import jakarta.validation.ValidationException;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Component
public class JsonUtils {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static JsonNode parse(String jsonString) {
        if (Strings.isBlank(jsonString)) {
            return null;
        }

        try {
            return objectMapper.readTree(jsonString);
        } catch (Exception e) {
            throw new ValidationException("Json parse error: Config is invalid");
        }
    }

    public static JsonNode parse(List<String> listString) {
        if (CollectionUtils.isEmpty(listString)) {
            return null;
        }

        try {
            return objectMapper.valueToTree(listString);
        } catch (Exception e) {
            throw new ValidationException("Json parse error: Config is invalid");
        }
    }

    public static String serialize(JsonNode jsonNode) {
        if (Objects.isNull(jsonNode)) {
            return StringPools.BLANK;
        }

        try {
            return objectMapper.writeValueAsString(jsonNode);
        } catch (Exception e) {
            throw new ValidationException("Json serialize error: Config is invalid");
        }
    }

    public static <T> List<T> parseList(String jsonString, Class<T> clazz) {
        try {
            if (Strings.isBlank(jsonString)) {
                return Collections.emptyList();
            }
            return objectMapper.readValue(jsonString, objectMapper.getTypeFactory().constructCollectionType(List.class, clazz));
        } catch (Exception e) {
            throw new ValidationException("Json parse error: Config is invalid");
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
