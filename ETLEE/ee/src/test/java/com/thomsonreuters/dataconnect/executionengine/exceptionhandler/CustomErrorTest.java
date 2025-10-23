package com.thomsonreuters.dataconnect.executionengine.exceptionhandler;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CustomErrorTest {

    @Test
    void testConstructorAndGetters() {
        String message = "Test error message";
        String code = "ERR001";

        CustomError customError = new CustomError(message, code);

        assertEquals(message, customError.getMessage());
        assertEquals(code, customError.getCode());
    }

    @Test
    void testRuntimeExceptionBehavior() {
        String message = "Test error message";
        String code = "ERR001";

        CustomError customError = new CustomError(message, code);

        // Ensure it behaves like a RuntimeException
        RuntimeException runtimeException = customError;
        assertEquals(message, runtimeException.getMessage());
    }
}