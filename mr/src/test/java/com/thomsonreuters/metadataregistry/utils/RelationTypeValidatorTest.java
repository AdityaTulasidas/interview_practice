package com.thomsonreuters.metadataregistry.utils;

import com.thomsonreuters.metadataregistry.model.entity.enums.RelationType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import com.thomsonreuters.metadataregistry.utils.enumvalidators.RelationTypeValidator;

class RelationTypeValidatorTest {

    private final RelationTypeValidator validator = new RelationTypeValidator();

    @Test
    void shouldReturnTrue_WhenValidRelationTypeProvided() {
        assertTrue(validator.isValid(RelationType.ONE_TO_MANY, null));
    }

    @Test
    void shouldReturnFalse_WhenNullRelationTypeProvided() {
        assertFalse(validator.isValid(null, null));
    }
}