package com.github.alvinli1991.metadata.toolkit.utils;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

/**
 * 使用jackson的Json工具类
 *
 * @author: Li Xiang
 * Date: 2021/11/5
 * Time: 5:16 PM
 */
public class JacksonUtils {
    private static ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        SimpleModule thriftModule = new SimpleModule("reqThrift",
                Version.unknownVersion());

        OBJECT_MAPPER = OBJECT_MAPPER
                .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
                .registerModule(thriftModule);
    }


    @Nullable
    public static <T> T parse(String content, Class<T> valueType) {
        return BaseJacksonUtils.parse(OBJECT_MAPPER, content, valueType);
    }

    public static <T> Optional<T> parseOpt(String content, Class<T> valueType) {
        return BaseJacksonUtils.parseOpt(OBJECT_MAPPER, content, valueType);
    }

    @Nullable
    public static String toJson(Object value) {
        return BaseJacksonUtils.toJson(OBJECT_MAPPER, value);
    }

    public static Optional<String> toJsonOpt(Object value) {
        return BaseJacksonUtils.toJsonOpt(OBJECT_MAPPER, value);
    }

    @Nullable
    public static <T> T parse(String content, TypeReference<T> typeReference) {
        return BaseJacksonUtils.parse(OBJECT_MAPPER, content, typeReference);
    }

    /**
     * 将json字符串转换为对象
     *
     * @param json
     * @param javaType
     * @return 正常返回转换后的对象，否则返回null
     */
    @Nullable
    public static <T> T parse(String json, JavaType javaType) {
        return BaseJacksonUtils.parse(OBJECT_MAPPER, json, javaType);
    }

    /**
     * 将json字符串转换为对象
     *
     * @param json
     * @param itemClass list中的对象类型
     * @param <T>
     * @return 正常返回转换后的list对象，否则返回null
     */
    @Nullable
    public static <T> List<T> json2List(String json, Class<T> itemClass) {
        return BaseJacksonUtils.json2List(OBJECT_MAPPER, json, itemClass);
    }

    public static <T> Optional<T> parseOpt(String content, TypeReference<T> typeReference) {
        return BaseJacksonUtils.parseOpt(OBJECT_MAPPER, content, typeReference);
    }

    @Nullable
    public static <T> T parse(String content, Class<?> container, Class<?>... actualType) {
        return BaseJacksonUtils.parse(OBJECT_MAPPER, content, container, actualType);
    }

    public static <T> Optional<T> parseOpt(String content, Class<?> container, Class<?>... actualType) {
        return BaseJacksonUtils.parseOpt(OBJECT_MAPPER, content, container, actualType);
    }

}
