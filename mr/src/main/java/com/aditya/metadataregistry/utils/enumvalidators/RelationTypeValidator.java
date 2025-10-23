package com.thomsonreuters.metadataregistry.utils.enumvalidators;

import com.thomsonreuters.metadataregistry.model.entity.enums.RelationType;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class RelationTypeValidator implements ConstraintValidator<ValidRelationType, RelationType> {

    @Override
    public void initialize(ValidRelationType constraintAnnotation) {
        // Initialization logic if needed
    }

    @Override
    public boolean isValid(RelationType value, ConstraintValidatorContext context) {
        return value != null && !value.toString().isEmpty();
    }
}