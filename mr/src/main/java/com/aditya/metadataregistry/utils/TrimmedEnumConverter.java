package com.thomsonreuters.metadataregistry.utils;

import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;
import org.springframework.data.mapping.MappingException;

public class TrimmedEnumConverter<T extends Enum<T>> implements Converter<String, T> {
 
    private final Class<T> enumType;
 
    public TrimmedEnumConverter(Class<T> enumType) {
        this.enumType = enumType;
    }
 
    @Override
    public T convert(MappingContext<String, T> context) {
        String source = context.getSource();
        if (source == null) return null;
 
        String trimmed = source.trim().toUpperCase(); // optional: normalize case
        try {
            return Enum.valueOf(enumType, trimmed);
        } catch (IllegalArgumentException e) {
            throw new MappingException("Invalid enum value: " + trimmed + " for enum " + enumType.getSimpleName());
        }
    }


}