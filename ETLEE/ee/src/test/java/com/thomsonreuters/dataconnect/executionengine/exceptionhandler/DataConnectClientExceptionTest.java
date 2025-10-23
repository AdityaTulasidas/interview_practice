package com.thomsonreuters.dataconnect.executionengine.exceptionhandler;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class DataConnectClientExceptionTest {

    @Test
    void testConstructorWithMessageAndCause() {
        String message = "Test exception message";
        Throwable cause = new RuntimeException("Test cause");

        DataConnectClientException exception = new DataConnectClientException(message, cause);

        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    void testConstructorWithNullValues() {
        DataConnectClientException exception = new DataConnectClientException(null, null);

        assertNotNull(exception);
        assertEquals(null, exception.getMessage());
        assertEquals(null, exception.getCause());
    }
}