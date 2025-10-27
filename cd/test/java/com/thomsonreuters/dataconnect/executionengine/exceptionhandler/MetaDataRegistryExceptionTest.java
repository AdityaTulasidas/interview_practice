package com.thomsonreuters.dataconnect.executionengine.exceptionhandler;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class MetaDataRegistryExceptionTest {

    @Test
    void testConstructorAndGetters() {
        String message = "Test exception message";
        String code = "ERR004";

        MetaDataRegistryException exception = new MetaDataRegistryException(message, code);

        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertEquals(code, exception.getCode());
    }

    @Test
    void testConstructorWithNullValues() {
        MetaDataRegistryException exception = new MetaDataRegistryException(null, null);

        assertNotNull(exception);
        assertEquals(null, exception.getMessage());
        assertEquals(null, exception.getCode());
    }
}