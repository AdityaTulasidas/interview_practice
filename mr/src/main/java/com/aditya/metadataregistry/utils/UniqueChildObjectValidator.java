package com.thomsonreuters.metadataregistry.utils;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class UniqueChildObjectValidator implements ConstraintValidator<UniqueChildObject ,String> {



    @Override
    public boolean isValid(String childObjectId, ConstraintValidatorContext context) {
        return true;
    }
}