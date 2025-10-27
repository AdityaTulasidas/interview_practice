package com.thomsonreuters.dataconnect.dataintegration.utils;

import java.util.Arrays;

public class EnumUtils {

    private EnumUtils() {
        // Private constructor to prevent instantiation
    }
    public static <E extends Enum<E>> boolean isValidEnum(Class<E> enumClass, String value) {
        if (value == null) {
            return false;
        }
        return Arrays.stream(enumClass.getEnumConstants())
                .anyMatch(e -> e.name().equals(value));
    }
}