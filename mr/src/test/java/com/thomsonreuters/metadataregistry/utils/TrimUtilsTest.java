package com.thomsonreuters.metadataregistry.utils;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

class TrimUtilsTest {

    private static class TestObject {
        @Trimmed
        private String fieldOne;
        private String fieldTwo;

        public TestObject(String fieldOne, String fieldTwo) {
            this.fieldOne = fieldOne;
            this.fieldTwo = fieldTwo;
        }

        public String getFieldOne() {
            return fieldOne;
        }

        public String getFieldTwo() {
            return fieldTwo;
        }
    }

    @Test
    void should_trimFields_when_trimmedAnnotationPresent() {
        TestObject testObject = new TestObject("  valueOne  ", "  valueTwo  ");
        TestObject result = TrimUtils.trimFields(testObject);

        assertEquals("valueOne", result.getFieldOne());
        assertEquals("  valueTwo  ", result.getFieldTwo());
    }

    @Test
    void should_returnNull_when_objectIsNull() {
        TestObject result = TrimUtils.trimFields(null);

        assertNull(result);
    }


}