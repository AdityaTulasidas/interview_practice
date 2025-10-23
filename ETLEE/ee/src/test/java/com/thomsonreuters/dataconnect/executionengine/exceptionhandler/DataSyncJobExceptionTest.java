package com.thomsonreuters.dataconnect.executionengine.exceptionhandler;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class DataSyncJobExceptionTest {

    @Test
    void testConstructorAndGetters() {
        String message = "Test exception message";
        String code = "ERR002";

        DataSyncJobException exception = new DataSyncJobException(message, code);

        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertEquals(code, exception.getCode());
    }

    @Test
    void testConstructorWithNullValues() {
        DataSyncJobException exception = new DataSyncJobException(null, null);

        assertNotNull(exception);
        assertEquals(null, exception.getMessage());
        assertEquals(null, exception.getCode());
    }
}