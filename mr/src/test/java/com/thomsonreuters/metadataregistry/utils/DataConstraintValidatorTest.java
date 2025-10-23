package com.thomsonreuters.metadataregistry.utils;

import com.thomsonreuters.metadataregistry.model.entity.enums.DataConstraint;
import com.thomsonreuters.metadataregistry.utils.enumvalidators.DataConstraintValidator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DataConstraintValidatorTest {

    private final DataConstraintValidator validator = new DataConstraintValidator();

    @Test
    void shouldReturnTrue_WhenValidDataConstraintProvided() {
        assertTrue(validator.isValid(DataConstraint.CHECK, null));
    }

    @Test
    void shouldReturnFalse_WhenNullDataConstraintProvided() {
        assertFalse(validator.isValid(null, null));
    }
}