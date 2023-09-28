package com.github.alvinli1991.metadata.toolkit.utils;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * @author: Li Xiang
 * Date: 2022/4/13
 * Time: 3:02 PM
 */
@Slf4j
public class BaseJacksonUtils {

    public static final TypeReference<Map<String, Object>> STR_OBJ_MAP_TYPE_REFERENCE = new TypeReference<Map<String, Object>>() {
    };

    public static final TypeReference<Map<String, String>> STR_STR_MAP_TYPE_REFERENCE = new TypeReference<Map<String, String>>() {
    };


    @Nullable
    public static <T> T parse(ObjectMapper objectMapper, String content, Class<T> valueType) {
        try {
            T result = objectMapper.readValue(content, valueType);
            return result;
        } catch (Exception e) {
            log.error("Json parse failed ", e);
            return null;
        }
    }

    public static <T> Optional<T> parseOpt(ObjectMapper objectMapper, String content, Class<T> valueType) {
        return Optional.ofNullable(parse(objectMapper, content, valueType));
    }

    @Nullable
    public static String toJson(ObjectMapper objectMapper, Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (Exception e) {
            log.error("toJson failed ", e);
            return null;
        }
    }

    public static Optional<String> toJsonOpt(ObjectMapper objectMapper, Object value) {
        return Optional.ofNullable(toJson(objectMapper, value));
    }

    @Nullable
    public static <T> T parse(ObjectMapper objectMapper, String content, TypeReference<T> typeReference) {
        try {
            return objectMapper.readValue(content, typeReference);
        } catch (Exception e) {
            log.error("parse from json failed ", e);
            return null;
        }
    }

    /**
     * 将json字符串转换为对象
     *
     * @param objectMapper
     * @param json
     * @param javaType
     * @return 正常返回转换后的对象，否则返回null
     */
    @Nullable
    public static <T> T parse(ObjectMapper objectMapper, String json, JavaType javaType) {
        if (json == null || "".equals(json)) {
            return null;
        }
        try {
            return objectMapper.readValue(json, javaType);
        } catch (JsonGenerationException | JsonMappingException e) {
            log.error("json convert exception", e);
            return null;
        } catch (IOException e) {
            log.error("json convert exception", e);
            return null;
        }
    }

    /**
     * 将json字符串转换为对象
     *
     * @param objectMapper
     * @param json
     * @param itemClass    list中的对象类型
     * @param <T>
     * @return 正常返回转换后的list对象，否则返回null
     */
    @Nullable
    public static <T> List<T> json2List(ObjectMapper objectMapper, String json, Class<T> itemClass) {
        return parse(objectMapper, json, objectMapper.getTypeFactory().constructCollectionType(List.class, itemClass));
    }

    public static <T> Optional<T> parseOpt(ObjectMapper objectMapper, String content, TypeReference<T> typeReference) {
        return Optional.ofNullable(parse(objectMapper, content, typeReference));
    }

    @Nullable
    public static <T> T parse(ObjectMapper objectMapper, String content, Class<?> container, Class<?>... actualType) {
        try {
            JavaType javaType = objectMapper.getTypeFactory().constructParametricType(container, actualType);
            return objectMapper.readValue(content, javaType);
        } catch (Exception e) {
            log.error("parse from json {} failed ", content, e);
            return null;
        }
    }

    public static <T> Optional<T> parseOpt(ObjectMapper objectMapper, String content, Class<?> container, Class<?>... actualType) {
        return Optional.ofNullable(parse(objectMapper, content, container, actualType));
    }

    /**
     * 将object 转为map
     *
     * @param objectMapper
     * @param object
     * @return
     */
    public static Optional<Map<String, Object>> convertToStrObjMap(ObjectMapper objectMapper, Object object) {
        if (Objects.isNull(object)) {
            return Optional.empty();
        }
        try {
            return Optional.of(objectMapper.convertValue(object, STR_OBJ_MAP_TYPE_REFERENCE));
        } catch (Exception e) {
            log.error("charge info convert to map error", e);
            return Optional.empty();
        }
    }

    /**
     * 将object 转为map
     *
     * @param objectMapper
     * @param object
     * @return
     */
    public static Optional<Map<String, String>> convertToStrStrMap(ObjectMapper objectMapper, Object object) {
        if (Objects.isNull(object)) {
            return Optional.empty();
        }
        try {
            return Optional.of(objectMapper.convertValue(object, STR_STR_MAP_TYPE_REFERENCE));
        } catch (Exception e) {
            log.error("charge info convert to map error", e);
            return Optional.empty();
        }
    }
}
