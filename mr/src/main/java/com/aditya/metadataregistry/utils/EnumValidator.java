package com.thomsonreuters.metadataregistry.utils;

import com.thomsonreuters.metadataregistry.model.entity.enums.DataConstraint;
import com.thomsonreuters.metadataregistry.model.entity.enums.DataType;
import com.thomsonreuters.metadataregistry.model.entity.enums.RelationType;

import java.util.Arrays;
public class EnumValidator {
    public static <T extends Enum<T>> boolean isValidEnumValue(String value, Class<T> enumClass) {
        return Arrays.stream(enumClass.getEnumConstants())
                .map(Enum::name)
                .anyMatch(e -> e.equalsIgnoreCase(value));
    }

    private EnumValidator(){
        // Private constructor to prevent instantiation
    }

    public static boolean dataTypeEnumValidation(String value){
        return isValidEnumValue(value,DataType.class);
    }

    public static boolean dataConstraintEnumValidation(String value){
        return isValidEnumValue(value, DataConstraint.class);
    }
    public static boolean relationTypeEnumValidation(String value){
        return isValidEnumValue(value, RelationType.class);
    }
}