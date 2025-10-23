package com.aditya.dataconnect.executionengine.exceptionhandler;

import com.aditya.dataconnect.executionengine.constant.Constants;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void testHandleCustomError_InternalServerError() {
        DataSyncJobException exception = new DataSyncJobException("Internal Server Error", Constants.INTERNAL_SERVER_ERROR);

        ResponseEntity<Map<String, Object>> response = handler.handleCustomError(exception);

        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(Constants.INTERNAL_SERVER_ERROR, ((Map<?, ?>) response.getBody().get(Constants.ERROR)).get(Constants.CODE));
    }

    @Test
    void testHandleCustomError_NotFound() {
        DataSyncJobException exception = new DataSyncJobException("Not Found", Constants.NOT_FOUND);

        ResponseEntity<Map<String, Object>> response = handler.handleCustomError(exception);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(Constants.NOT_FOUND, ((Map<?, ?>) response.getBody().get(Constants.ERROR)).get(Constants.CODE));
    }

    @Test
    void testHandleCustomError_Conflict() {
        DataSyncJobException exception = new DataSyncJobException("Conflict", "CONFLICT");

        ResponseEntity<Map<String, Object>> response = handler.handleCustomError(exception);

        assertNotNull(response);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("CONFLICT", ((Map<?, ?>) response.getBody().get(Constants.ERROR)).get(Constants.CODE));
    }

    @Test
    void testHandleCustomError_BadRequest() {
        DataSyncJobException exception = new DataSyncJobException("Bad Request", "BAD_REQUEST");

        ResponseEntity<Map<String, Object>> response = handler.handleCustomError(exception);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("BAD_REQUEST", ((Map<?, ?>) response.getBody().get(Constants.ERROR)).get(Constants.CODE));
    }

    @Test
    void testHandleGenericError() {
        Exception exception = new Exception("Generic Error");

        ResponseEntity<Map<String, Object>> response = handler.handleGenericError(exception);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(Constants.DATASYNC_JOB_BAD_REQUEST, ((Map<?, ?>) response.getBody().get(Constants.ERROR)).get(Constants.CODE));
    }

    @Test
    void testHandleHttpMessageNotReadableException() {
        HttpMessageNotReadableException exception = new HttpMessageNotReadableException("Message Not Readable");

        ResponseEntity<Map<String, Object>> response = handler.handleHttpMessageNotReadableException(exception);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(Constants.ENUM_VALIDATION_ERROR, ((Map<?, ?>) response.getBody().get(Constants.ERROR)).get(Constants.MESSAGE));
    }

    @Test
    void testHandleHttpMediaTypeNotAcceptableException() {
        ResponseEntity<String> response = handler.handleHttpMediaTypeNotAcceptableException();

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_ACCEPTABLE, response.getStatusCode());
        assertEquals("Acceptable MIME type: application/json", response.getBody());
    }
}