package com.thomsonreuters.dataconnect.executionengine.exceptionhandler;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class CustomErrorResponseTest {

    @Test
    void testConstructorAndGetter() {
        CustomError customError = new CustomError("Test error message", "ERR001");
        CustomErrorResponse response = new CustomErrorResponse(customError);

        assertNotNull(response.getCustomError());
        assertEquals(customError, response.getCustomError());
    }

    @Test
    void testSetterAndGetter() {
        CustomError customError = new CustomError("Test error message", "ERR001");
        CustomErrorResponse response = new CustomErrorResponse(null);

        response.setCustomError(customError);

        assertNotNull(response.getCustomError());
        assertEquals(customError, response.getCustomError());
    }
}