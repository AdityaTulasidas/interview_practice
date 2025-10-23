package com.thomsonreuters.metadataregistry.utils;

import com.thomsonreuters.metadataregistry.model.entity.enums.DataConstraint;
import com.thomsonreuters.metadataregistry.model.entity.enums.DataType;
import com.thomsonreuters.metadataregistry.model.entity.enums.RelationType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EnumValidatorTest {

    @Test
    void shouldReturnTrue_WhenValidEnumValueProvided() {
        assertTrue(EnumValidator.isValidEnumValue("STRING", DataType.class));
        assertTrue(EnumValidator.isValidEnumValue("NOT_NULL", DataConstraint.class));
        assertTrue(EnumValidator.isValidEnumValue("ONE_TO_MANY", RelationType.class));
    }

    @Test
    void shouldReturnFalse_WhenInvalidEnumValueProvided() {
        assertFalse(EnumValidator.isValidEnumValue("INVALID", DataType.class));
        assertFalse(EnumValidator.isValidEnumValue("INVALID", DataConstraint.class));
        assertFalse(EnumValidator.isValidEnumValue("INVALID", RelationType.class));
    }

    @Test
    void shouldReturnTrue_WhenValidDataTypeEnumValueProvided() {
        assertTrue(EnumValidator.dataTypeEnumValidation("STRING"));
    }

    @Test
    void shouldReturnFalse_WhenInvalidDataTypeEnumValueProvided() {
        assertFalse(EnumValidator.dataTypeEnumValidation("INVALID"));
    }


    @Test
    void shouldReturnTrue_WhenValidDataConstraintEnumValueProvided() {
        assertTrue(EnumValidator.dataConstraintEnumValidation("NOT_NULL"));
    }

    @Test
    void shouldReturnFalse_WhenInvalidDataConstraintEnumValueProvided() {
        assertFalse(EnumValidator.dataConstraintEnumValidation("INVALID"));
    }

    @Test
    void shouldReturnTrue_WhenValidRelationTypeEnumValueProvided() {
        assertTrue(EnumValidator.relationTypeEnumValidation("ONE_TO_MANY"));
    }

    @Test
    void shouldReturnFalse_WhenInvalidRelationTypeEnumValueProvided() {
        assertFalse(EnumValidator.relationTypeEnumValidation("INVALID"));
    }
}