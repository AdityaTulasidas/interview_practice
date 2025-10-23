package com.thomsonreuters.metadataregistry.exceptionhandler;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MetaDataRegistryExceptionTest {

    @Test
    void shouldSetMessageAndCode_WhenExceptionIsCreated() {
        String message = "Test exception message";
        String code = "TEST_CODE";

        MetaDataRegistryException exception = new MetaDataRegistryException(message, code);

        assertEquals(message, exception.getMessage());
        assertEquals(code, exception.getCode());
    }
}