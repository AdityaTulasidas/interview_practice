package com.thomsonreuters.metadataregistry.utils;

import com.thomsonreuters.metadataregistry.model.entity.enums.DataType;
import com.thomsonreuters.metadataregistry.utils.enumvalidators.DataTypeValidator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DataTypeValidatorTest {

    private final DataTypeValidator validator = new DataTypeValidator();

    @Test
    void shouldReturnTrue_WhenValidDataTypeProvided() {
        assertTrue(validator.isValid(DataType.STRING, null));
    }

    @Test
    void shouldReturnFalse_WhenNullDataTypeProvided() {
        assertFalse(validator.isValid(null, null));
    }
}