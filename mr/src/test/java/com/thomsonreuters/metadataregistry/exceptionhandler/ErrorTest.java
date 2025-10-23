package com.thomsonreuters.metadataregistry.exceptionhandler;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ErrorTest {

    @Test
    void shouldSetMessageAndCode_WhenErrorIsCreated() {
        String message = "Error message";
        String code = "ERROR_CODE";

        Error error = new Error(message, code);

        assertEquals(message, error.getMessage());
        assertEquals(code, error.getCode());
    }

    @Test
    void shouldSetMessageAndCode_WhenUsingSetters() {
        Error error = new Error();
        error.setMessage("New error message");
        error.setCode("NEW_ERROR_CODE");

        assertEquals("New error message", error.getMessage());
        assertEquals("NEW_ERROR_CODE", error.getCode());
    }
}