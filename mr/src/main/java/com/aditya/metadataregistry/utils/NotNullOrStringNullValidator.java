package com.thomsonreuters.metadataregistry.utils;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NotNullOrStringNullValidator implements ConstraintValidator<NotNullOrStringNull, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }
        return !"null".equalsIgnoreCase(value.trim());
    }
}