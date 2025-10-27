package com.thomsonreuters.dataconnect.dataintegration.entity.pojo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thomsonreuters.dataconnect.dataintegration.model.pojo.DataUnitContent;
import com.thomsonreuters.dataconnect.dataintegration.model.pojo.DataUnitList;
import com.thomsonreuters.dataconnect.dataintegration.model.pojo.Header;
import com.thomsonreuters.dataconnect.dataintegration.model.pojo.JsonParser;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JsonParserTest {

    @Test
    void shouldThrowIOException_WhenHeaderIsInvalid() {
        // Mock invalid JSON input
        String json = """
        {
            "invalid_header": {}
        }
    """;
        InputStream inputStream = new ByteArrayInputStream(json.getBytes());

        // Test the method
        JsonParser parser = new JsonParser();
        assertThrows(IOException.class, () -> parser.readHeader(inputStream));
    }

    @Test
    void shouldThrowIOException_WhenDataIsInvalid() {
        // Mock invalid JSON input
        String json = """
        {
            "invalid_data": {}
        }
    """;
        InputStream inputStream = new ByteArrayInputStream(json.getBytes());

        // Test the method
        JsonParser parser = new JsonParser();
        assertThrows(IOException.class, () -> parser.readData(inputStream));
    }

    @Test
    void shouldInitializeObjectMapperSuccessfully_WhenConstructorIsCalled() {
        // Test the constructor
        JsonParser parser = new JsonParser();
        assertNotNull(parser);
        assertNotNull(parser.objectMapper);
    }
}