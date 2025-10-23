package com.thomsonreuters.dataconnect.executionengine.utils.enumvalidators;

import com.thomsonreuters.dataconnect.executionengine.model.entity.enums.DataConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class DataConstraintValidator implements ConstraintValidator<ValidDataConstraint, DataConstraint> {

    @Override
    public void initialize(ValidDataConstraint constraintAnnotation) {
        //
    }



    @Override
    public boolean isValid(DataConstraint value, ConstraintValidatorContext context) {
        return value != null && !value.toString().isEmpty();        // Add any additional checks if needed
    }


}