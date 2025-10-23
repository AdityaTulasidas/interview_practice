package com.thomsonreuters.metadataregistry.exceptionhandler;

import com.thomsonreuters.metadataregistry.constants.Constants;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler();

    @Test
    void shouldReturnBadRequest_WhenMetaDataRegistryExceptionWithInvalidRequestCode() {
        MetaDataRegistryException exception = new MetaDataRegistryException("Validation error", "INVALID_REQUEST");

        ResponseEntity<Map<String, Object>> response = globalExceptionHandler.handleCustomError(exception);

        assertEquals(400, response.getStatusCode().value());
        assertEquals("INVALID_REQUEST", ((Map<String, String>) response.getBody().get("error")).get("code"));
        assertEquals("Validation error", ((Map<String, String>) response.getBody().get("error")).get("message"));
    }

    @Test
    void shouldReturnInternalServerError_WhenMetaDataRegistryExceptionWithInternalServerErrorCode() {
        MetaDataRegistryException exception = new MetaDataRegistryException("Server error", "INTERNAL_SERVER_ERROR");

        ResponseEntity<Map<String, Object>> response = globalExceptionHandler.handleCustomError(exception);

        assertEquals(500, response.getStatusCode().value());
        assertEquals("INTERNAL_SERVER_ERROR", ((Map<String, String>) response.getBody().get("error")).get("code"));
        assertEquals("Server error", ((Map<String, String>) response.getBody().get("error")).get("message"));
    }

    @Test
    void shouldReturnBadRequest_WhenGenericExceptionOccurs() {
        Exception exception = new Exception("Generic error");

        ResponseEntity<Map<String, Object>> response = globalExceptionHandler.handleGenericError(exception);

        assertEquals(400, response.getStatusCode().value());
        assertEquals("INVALID_REQUEST", ((Map<String, String>) response.getBody().get("error")).get("code"));
        assertEquals(Constants.VALIDATION_ERROR, ((Map<String, String>) response.getBody().get("error")).get("message"));
    }
}