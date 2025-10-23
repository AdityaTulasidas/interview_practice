package com.thomsonreuters.metadataregistry.utils;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = NotNullOrStringNullValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface NotNullOrStringNull {
    String message() default "Value cannot be null or the string 'null'";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}