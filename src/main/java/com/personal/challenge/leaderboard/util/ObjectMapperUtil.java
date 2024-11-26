package com.personal.challenge.leaderboard.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class ObjectMapperUtil {

    private static final ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    /** Object Mapper map Input Stream to Class
     * @param <T>
     * @param src
     * @param valueType
     * @return
     * @throws IOException
     */
    public static <T> T mapInputStreamToClass(InputStream src, Class<T> valueType) throws IOException {
        return mapper.readValue(src, valueType);
    }

    /**
     * Map and concert value to class
     * @param <T>
     * @param response
     * @param result
     * @return
     * @throws IOException
     */
    public static <T> T mapStringToClass(String response, Class<T> result) throws IOException {
        return mapper.readValue(response, result);
    }

    /**
     * Convert from class to JsonString
     * @param object
     * @return
     * @throws JsonProcessingException
     */
    public static String writeValueAsString(Object object) throws JsonProcessingException {
        return mapper.writeValueAsString(object);
    }

    /**
     * Map and concert value to Nested Map/Class
     * @param <T>
     * @param jsonString
     * @param valueTypeRef
     * @return
     * @throws IOException
     */
    public static <T> T mapStringToClassByTypeReference(String jsonString, TypeReference<?> valueTypeRef) throws IOException {
        return (T) mapper.readValue(jsonString, valueTypeRef);
    }

    /**
     * Maps all properties of an object to a map, which contains all fields and their values
     * @param object The object to be used
     * @return A map which contains all fields and values
     */
    public static Map<String, Object> convertObjectToMap(Object object) {
        return mapper.convertValue(object, new TypeReference<Map<String, Object>>() {});
    }
}
