package com.thomsonreuters.metadataregistry.exceptionhandler;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ErrorResponseTest {

    @Test
    void shouldSetError_WhenErrorResponseIsCreated() {
        Error error = new Error("Error message", "ERROR_CODE");

        ErrorResponse errorResponse = new ErrorResponse(error);

        assertEquals(error, errorResponse.error);
    }
}