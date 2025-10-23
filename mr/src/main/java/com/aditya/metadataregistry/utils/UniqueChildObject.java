package com.thomsonreuters.metadataregistry.utils;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = UniqueChildObjectValidator.class)
@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueChildObject {
    String message() default "Child Object ID must be unique";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}