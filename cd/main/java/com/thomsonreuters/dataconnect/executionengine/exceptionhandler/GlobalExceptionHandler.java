package com.thomsonreuters.dataconnect.executionengine.exceptionhandler;

import com.thomsonreuters.dataconnect.executionengine.constant.Constants;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Hidden
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DataSyncJobException.class)
    public ResponseEntity<Map<String,Object>> handleCustomError(DataSyncJobException ex) {
        Map<String, Object> errorResponse = new HashMap<>();
        Map<String, String> errorDetails = new HashMap<>();
        errorDetails.put(Constants.CODE,ex.getCode() );
        errorDetails.put(Constants.MESSAGE, ex.getMessage());
        errorResponse.put(Constants.ERROR, errorDetails);
        if(ex.getCode().equals(Constants.INTERNAL_SERVER_ERROR)){
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if(ex.getCode().equals(Constants.NOT_FOUND)){
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }
        if(ex.getCode().equals("CONFLICT")){
            return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericError(Exception ex) {
        Map<String, Object> errorResponse = new HashMap<>();
        Map<String, String> errorDetails = new HashMap<>();
        errorDetails.put(Constants.CODE, Constants.DATASYNC_JOB_BAD_REQUEST);
        errorDetails.put(Constants.MESSAGE, Constants.BAD_REQUEST);
        errorResponse.put(Constants.ERROR, errorDetails);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        Map<String, Object> errorResponse = new HashMap<>();
        Map<String, String> errorDetails = new HashMap<>();
        errorDetails.put(Constants.CODE, Constants.DATASYNC_JOB_BAD_REQUEST);
        errorDetails.put(Constants.MESSAGE, Constants.ENUM_VALIDATION_ERROR);
        errorResponse.put(Constants.ERROR, errorDetails);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    @ResponseBody
    public ResponseEntity<String> handleHttpMediaTypeNotAcceptableException() {
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                .body("Acceptable MIME type: " + MediaType.APPLICATION_JSON_VALUE);
    }
}
