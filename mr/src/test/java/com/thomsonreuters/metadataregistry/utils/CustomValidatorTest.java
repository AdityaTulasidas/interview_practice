package com.thomsonreuters.metadataregistry.utils;

import com.thomsonreuters.metadataregistry.utils.validation.CustomValidator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CustomValidatorTest {

    private final CustomValidator validator = new CustomValidator();

    @Test
    void shouldReturnFalse_WhenValueIsNull() {
        assertFalse(validator.isValid(null, null));
    }

    @Test
    void shouldReturnFalse_WhenStringIsEmptyOrNullString() {
        assertFalse(validator.isValid("", null));
        assertFalse(validator.isValid("null", null));
    }

    @Test
    void shouldReturnTrue_WhenValidStringProvided() {
        assertTrue(validator.isValid("valid", null));
    }

    @Test
    void shouldReturnFalse_WhenEnumIsNullOrEmpty() {
        assertFalse(validator.isValid(null, null));
    }

    @Test
    void shouldReturnTrue_WhenValidEnumProvided() {
        assertTrue(validator.isValid(TestEnum.VALUE1, null));
    }

    private enum TestEnum {
        VALUE1, VALUE2
    }
}