package org.dcsa.ctk.consumer.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.netty.handler.codec.DecoderException;
import org.dcsa.ctk.consumer.model.TestConfig;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class JsonUtility {

    public static <T> String getStringFormObject(T t) {
        ObjectMapper mapper = JsonMapper.builder()
                .addModule(new JavaTimeModule())
                .build();
        String inputJson = "";
        if (t != null) {
            try {
                inputJson = mapper.writeValueAsString(t);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        return inputJson;
    }

    public static Map<String, Object> convertToMap(Object object) {
        ObjectMapper mapObject = new ObjectMapper();
        JavaTimeModule module = new JavaTimeModule();
        mapObject.registerModule(module);
        Map<String, Object> mapObj = mapObject.convertValue(object, Map.class);
        return mapObj;
    }

    public static <T> List<Map<String, Object>> convertToList(List<T> object) {
        List<Map<String, Object>> list = new ArrayList<>();
        for (Object obj : object) {
            Map<String, Object> mapObj = convertToMap(obj);
            list.add(mapObj);
        }
        return list;
    }

    public static <T, R> R convertTo(Class<R> type, T object) {
        String jsonString =getStringFormat(object);
        R r=getObjectFromJson(type,jsonString);
            return getObjectFromJson(type,jsonString);
    }

    public static Map<String, Object> addFieldToMap(Map<String, Object> map, Map<String, Object> value) {
        for (Map.Entry<String, Object> entry : value.entrySet()) {
            if (map.containsKey(entry.getKey())) {
                Object obj = map.get(entry.getKey());
                if (obj instanceof List) {
                    ((List<Object>) obj).addAll((Collection<?>) entry.getValue());
                }
                else
                    map.putAll(value);
            }
            else
                map.putAll(value);

        }

        return map;
    }

    public static TestConfig loadTestConfig(String testSuiteJson) {
        String jsonString = FileUtility.loadResourceAsString(testSuiteJson);
        TestConfig testConfig = JsonUtility.getObjectFromJson(TestConfig.class, jsonString);
        return testConfig;
    }

    public static <T> T getObjectFromJson(Class<T> t1, String jsonString) {
        ObjectMapper mapper = new ObjectMapper();
        T t = null;
        try {
            t = (T) mapper.readValue(jsonString, t1);
        } catch (JsonProcessingException e) {
            throw new DecoderException(e.getLocalizedMessage());
        }
        return t;
    }

    public static <T> String getStringFormat(T t) {
        ObjectMapper mapper = new ObjectMapper();

        String inputJson = "";
        if (t != null) {
            try {
                inputJson = mapper.writeValueAsString(t);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        return inputJson;
    }

    public static String beautify(Object obj) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
    }
    public static String getSubscriptionId(String notificationBody){
        String subscriptionId = "";
        String[] tokens = notificationBody.split(",");
        if(tokens.length > 0){
            String subscriptionVal =   tokens[tokens.length - 1];
            tokens = subscriptionVal.split(":");
            if(tokens.length > 0 ){
                subscriptionId = tokens[tokens.length-1].trim();
                subscriptionId = subscriptionId.replaceAll("\"", "");
                subscriptionId = subscriptionId.replaceAll("}", "").trim();
            }
        }
        return subscriptionId;
    }

}
