package com.huoli.flight.recommend.util;

import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * Created by roverll on 7/19/16.
 */
public class JsonUtil {

    private static final Logger logger = LoggerFactory.getLogger(JsonUtil.class);

    public static <T> T objFJson(String json, Class<T> tClass) {
        if (StringUtils.isEmpty(json))
            return null;
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            return mapper.readValue(json, tClass);
        } catch (IOException e) {
            logger.error("read object from json error", e);
            return null;
        }
    }

    public static <T> T objFJsonByConfig(String json, Class<T> tClass) {
        if (StringUtils.isEmpty(json))
            return null;
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            mapper.configure(Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
            return mapper.readValue(json, tClass);
        } catch (IOException e) {
            logger.error("read object from json error", e);
            return null;
        }
    }

    public static <T> T collectFJsonByConfig(String json, Class<?> collectionClass, Class<?>... elementClasses) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            mapper.configure(Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
            JavaType type = mapper.getTypeFactory().constructParametricType(collectionClass, elementClasses);
            return mapper.readValue(json, type);
        } catch (IOException e) {
            logger.error("read object from json error", e);
            return null;
        }
    }

    public static String objToJson(Object object) {

        ObjectMapper om = new ObjectMapper();
        try {
            return om.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            logger.error("write object to json error", e);
            return null;
        }
    }


    public static <T> T jsonToObj(String jsonStr, TypeReference<T> typeReference) throws IOException {
        if (StringUtils.isEmpty(jsonStr))
            return null;
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        return objectMapper.readValue(jsonStr, typeReference);
    }

    public static <T> List<T> listFJson(String json, Class<T> tClass) {
        if (StringUtils.isEmpty(json))
            return Collections.emptyList();
        ObjectMapper om = new ObjectMapper();
        om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        JavaType type = om.getTypeFactory().constructCollectionType(List.class, tClass);
        try {
            return om.readValue(json, type);
        } catch (IOException e) {
            logger.error("write object to json error", e);
            return Collections.emptyList();
        }
    }


    public static JsonNode toJsonObj(String json) {
        if (StringUtils.isEmpty(json))
            return null;
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        try {
            return mapper.readTree(json);
        } catch (IOException e) {
            throw new IllegalArgumentException("read json failed", e);
        }
    }

    public static <T> T cloneObject(T t) {
        return objFJson(objToJson(t), (Class<T>) t.getClass());
    }
}
