package com.aditya.dataconnect.executionengine.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD , ElementType.METHOD , ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CustomValidator.class)
@Documented
public @interface NonNullable {
    String message() default "Field cannot be null, empty, or 'null' string";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}