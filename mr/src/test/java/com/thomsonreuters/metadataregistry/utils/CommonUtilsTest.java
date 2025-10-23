package com.thomsonreuters.metadataregistry.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thomsonreuters.metadataregistry.exceptionhandler.MetaDataRegistryException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;

class CommonUtilsTest {

    @Mock
    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    @Test
    void shouldGenerateResponse_WhenValidInputProvided() throws MetaDataRegistryException {
        String id = "123";
        String message = "Success: ";

        String response = CommonUtils.generateResponse(id, message);

        assertEquals("\"Success: 123\"", response);
    }


}