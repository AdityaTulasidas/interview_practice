package com.thomsonreuters.metadataregistry.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;

@Slf4j
public class TrimUtils {

    public static <T> T trimFields(T object) {
        if (object == null) {
            return null;
        }

        Field[] fields = object.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(Trimmed.class) && field.getType().equals(String.class)) {
                field.setAccessible(true);
                try {
                    String value = (String) field.get(object);
                    if (value != null) {
                        field.set(object, StringUtils.trimToNull(value));
                        log.info("Trimmed field: {}. New value: {}", field.getName(), field.get(object));
                    }
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("Failed to trim field: " + field.getName(), e);
                }
            }
        }
        return object;
    }
}