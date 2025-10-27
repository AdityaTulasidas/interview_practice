package com.thomsonreuters.dataconnect.dataintegration.testutil;

import static org.junit.jupiter.api.Assertions.fail;

/**
 * Shared reflection utilities for test code to avoid duplication.
 * Provides fail-fast semantics so misconfigured tests are immediately visible.
 */
public final class ReflectionTestHelper {

    private ReflectionTestHelper() {
        // utility
    }

    /**
     * Set a (possibly private) field value via reflection.
     * Fails the test if the field is not found or cannot be written.
     */
    public static void setField(Object target, String fieldName, Object value) {
        if (target == null) {
            fail("setField: target is null for field '" + fieldName + "'");
        }
        try {
            var f = target.getClass().getDeclaredField(fieldName);
            f.setAccessible(true);
            f.set(target, value);
        } catch (Exception e) {
            fail("setField failed for '" + fieldName + "' on " + target.getClass().getSimpleName() + ": " + e.getClass().getSimpleName() + " - " + e.getMessage());
        }
    }

    /**
     * Get a (possibly private) field value via reflection.
     * Fails the test if the field is not found or cannot be read.
     */
    public static Object getField(Object target, String fieldName) {
        if (target == null) {
            fail("getField: target is null for field '" + fieldName + "'");
        }
        try {
            var f = target.getClass().getDeclaredField(fieldName);
            f.setAccessible(true);
            return f.get(target);
        } catch (Exception e) {
            fail("getField failed for '" + fieldName + "' on " + target.getClass().getSimpleName() + ": " + e.getClass().getSimpleName() + " - " + e.getMessage());
            return null; // unreachable
        }
    }
}
