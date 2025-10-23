package com.aditya.dataconnect.executionengine.utils.enumvalidators;

import com.aditya.dataconnect.executionengine.model.entity.enums.DataType;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class DataTypeValidator implements ConstraintValidator<ValidDataType, DataType> {

    @Override
    public void initialize(ValidDataType constraintAnnotation) {
        // This does not need to do anything
    }



    @Override
    public boolean isValid(DataType value, ConstraintValidatorContext context) {
        return value != null && !value.toString().isEmpty();
    }


}