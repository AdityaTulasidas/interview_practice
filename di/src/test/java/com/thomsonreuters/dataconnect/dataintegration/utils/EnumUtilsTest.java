package com.thomsonreuters.dataconnect.dataintegration.utils;

import com.thomsonreuters.dataconnect.dataintegration.utils.EnumUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EnumUtilsTest {

    private enum TestEnum {
        VALUE1, VALUE2
    }

    @Test
    void shouldReturnTrue_WhenValidEnumValueIsProvided() {
        assertTrue(EnumUtils.isValidEnum(TestEnum.class, "VALUE1"));
    }

    @Test
    void shouldReturnFalse_WhenInvalidEnumValueIsProvided() {
        assertFalse(EnumUtils.isValidEnum(TestEnum.class, "INVALID"));
    }

    @Test
    void shouldReturnFalse_WhenNullEnumValueIsProvided() {
        assertFalse(EnumUtils.isValidEnum(TestEnum.class, null));
    }
}