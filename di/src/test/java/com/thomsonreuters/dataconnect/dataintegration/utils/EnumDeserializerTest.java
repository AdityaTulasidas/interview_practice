package com.thomsonreuters.dataconnect.dataintegration.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.thomsonreuters.dataconnect.dataintegration.utils.EnumDeserializer;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class EnumDeserializerTest {

    private enum TestEnum {
        VALUE1, VALUE2
    }

    @Test
    void shouldDeserializeEnumSuccessfully_WhenValidValueIsProvided() throws IOException {
        JsonParser jsonParser = mock(JsonParser.class);
        DeserializationContext deserializationContext = mock(DeserializationContext.class);
        EnumDeserializer<TestEnum> deserializer = new EnumDeserializer<>(TestEnum.class);

        when(jsonParser.getText()).thenReturn("VALUE1");

        TestEnum result = deserializer.deserialize(jsonParser, deserializationContext);

        assertEquals(TestEnum.VALUE1, result);
    }

    @Test
    void shouldThrowIOException_WhenInvalidValueIsProvided() throws IOException {
        JsonParser jsonParser = mock(JsonParser.class);
        DeserializationContext deserializationContext = mock(DeserializationContext.class);
        EnumDeserializer<TestEnum> deserializer = new EnumDeserializer<>(TestEnum.class);

        when(jsonParser.getText()).thenReturn("INVALID");

        IOException exception = assertThrows(IOException.class, () -> {
            deserializer.deserialize(jsonParser, deserializationContext);
        });

        assertEquals("Invalid value for Enum TestEnum: INVALID", exception.getMessage());
    }
}