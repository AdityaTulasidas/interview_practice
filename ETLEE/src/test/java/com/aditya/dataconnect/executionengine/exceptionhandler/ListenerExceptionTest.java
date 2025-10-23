package com.aditya.dataconnect.executionengine.exceptionhandler;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ListenerExceptionTest {

    @Test
    void testConstructorAndGetters() {
        String message = "Test exception message";
        String code = "ERR003";

        ListenerException exception = new ListenerException(message, code);

        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertEquals(code, exception.getCode());
    }

    @Test
    void testConstructorWithNullValues() {
        ListenerException exception = new ListenerException(null, null);

        assertNotNull(exception);
        assertEquals(null, exception.getMessage());
        assertEquals(null, exception.getCode());
    }
}