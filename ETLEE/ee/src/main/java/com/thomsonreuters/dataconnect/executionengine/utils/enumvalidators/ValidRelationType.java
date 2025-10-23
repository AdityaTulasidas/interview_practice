package com.thomsonreuters.dataconnect.executionengine.utils.enumvalidators;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DataTypeValidator.class)
public @interface ValidRelationType {
    String message() default "Invalid RelationType value";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}