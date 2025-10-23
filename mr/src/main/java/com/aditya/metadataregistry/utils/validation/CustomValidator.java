package com.thomsonreuters.metadataregistry.utils.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CustomValidator implements ConstraintValidator<NonNullable, Object> {

    @Override
    public void initialize(NonNullable constraintAnnotation) {
        // Initialization code can be added here if needed
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }

        if (value instanceof String str) {
            return !str.trim().isEmpty() && !"null".equalsIgnoreCase(str);
        }

        if (value instanceof Enum<?> enumValue) {
            String enumName = enumValue.name();
            return !enumName.trim().isEmpty() && !"null".equalsIgnoreCase(enumName);
        }
        return false;
    }
}
