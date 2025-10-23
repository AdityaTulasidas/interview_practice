package com.thomsonreuters.metadataregistry.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UniqueChildObjectValidatorTest {

    private final UniqueChildObjectValidator validator = new UniqueChildObjectValidator();

    @Test
    void shouldReturnTrue_WhenAnyChildObjectIdProvided() {
        assertTrue(validator.isValid("child-id", null));
        assertTrue(validator.isValid(null, null));
    }
}