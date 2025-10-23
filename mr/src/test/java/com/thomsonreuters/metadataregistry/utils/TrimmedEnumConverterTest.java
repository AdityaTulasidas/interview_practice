package com.thomsonreuters.metadataregistry.utils;

import com.thomsonreuters.metadataregistry.utils.TrimmedEnumConverter;
import org.junit.jupiter.api.Test;
import org.modelmapper.spi.MappingContext;
import org.mockito.Mockito;
import org.springframework.data.mapping.MappingException;

import static org.junit.jupiter.api.Assertions.*;

class TrimmedEnumConverterTest {

    private enum TestEnum {
        VALUE_ONE, VALUE_TWO
    }

    @Test
    void should_returnEnumValue_when_validStringProvided() {
        TrimmedEnumConverter<TestEnum> converter = new TrimmedEnumConverter<>(TestEnum.class);
        MappingContext<String, TestEnum> context = Mockito.mock(MappingContext.class);
        Mockito.when(context.getSource()).thenReturn(" value_one ");

        TestEnum result = converter.convert(context);

        assertEquals(TestEnum.VALUE_ONE, result);
    }

    @Test
    void should_returnNull_when_nullStringProvided() {
        TrimmedEnumConverter<TestEnum> converter = new TrimmedEnumConverter<>(TestEnum.class);
        MappingContext<String, TestEnum> context = Mockito.mock(MappingContext.class);
        Mockito.when(context.getSource()).thenReturn(null);

        TestEnum result = converter.convert(context);

        assertNull(result);
    }

    @Test
    void should_throwException_when_invalidStringProvided() {
        TrimmedEnumConverter<TestEnum> converter = new TrimmedEnumConverter<>(TestEnum.class);
        MappingContext<String, TestEnum> context = Mockito.mock(MappingContext.class);
        Mockito.when(context.getSource()).thenReturn("invalid_value");

        assertThrows(MappingException.class, () -> converter.convert(context));
    }
}