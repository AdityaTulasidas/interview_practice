package com.thomsonreuters.dataconnect.dataintegration.model.entity.enums;

public enum TransformType {
    BUILT_IN("built-in"),
    CUSTOM_JAVA("custom_java"),
    XSLT( "xslt");

    private final String value;

    TransformType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static TransformType fromValue(String value) {
        for (TransformType type : TransformType.values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown value: " + value);
    }
}