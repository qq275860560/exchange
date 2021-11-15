package com.ghf.exchange.util;

import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * @author jiangyuanlin@163.com
 */
@Slf4j
public class JsonUtil {

    private JsonUtil() {
    }

    private static ObjectMapper mapper = new ObjectMapper()
            //可解析反斜杠引用的所有字符
            .configure(JsonReadFeature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER.mappedFeature(), true)
            .configure(JsonReadFeature.ALLOW_UNQUOTED_FIELD_NAMES.mappedFeature(), true)
            //可解释包含tab或换行符
            .configure(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS.mappedFeature(), true);

    /**
     * 将对象序列化成Json字符串
     *
     * @param obj
     * @return
     */
    @SneakyThrows
    public static String toJsonString(Object obj) {
        return mapper.writeValueAsString(obj);
    }

    /**
     * 将Json字符串反序列化成对象
     *
     * @param jsonString
     * @param clazz
     * @return
     */
    @SneakyThrows
    public static <T> T parse(String jsonString, Class<T> clazz) {
        return mapper.readValue(jsonString, clazz);
    }

    /**
     * 将Json字符串反序列化成对象列表
     *
     * @param jsonString
     * @param clazz
     * @param <T>
     * @return
     */
    @SneakyThrows
    public static <T> List<T> parseList(String jsonString, Class<T> clazz) {
        CollectionType listType = mapper.getTypeFactory().constructCollectionType(ArrayList.class, clazz);
        return mapper.readValue(jsonString, listType);
    }

}
