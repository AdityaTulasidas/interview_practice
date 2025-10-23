package com.aditya.dataconnect.executionengine.utils.enumvalidators;

import com.aditya.dataconnect.executionengine.model.entity.enums.RelationType;

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