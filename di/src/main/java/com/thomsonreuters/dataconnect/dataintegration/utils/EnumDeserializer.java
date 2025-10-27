package com.thomsonreuters.dataconnect.dataintegration.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;

public class EnumDeserializer<T extends Enum<T>> extends JsonDeserializer<T> {
    private final Class<T> enumClass;

    public EnumDeserializer(Class<T> enumClass) {
        this.enumClass = enumClass;
    }

    @Override
    public T deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String value = p.getText();
        try {
            return Enum.valueOf(enumClass, value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IOException("Invalid value for Enum " + enumClass.getSimpleName() + ": " + value);
        }
    }
}