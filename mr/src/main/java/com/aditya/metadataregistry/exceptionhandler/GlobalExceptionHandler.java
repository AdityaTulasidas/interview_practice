package com.thomsonreuters.metadataregistry.exceptionhandler;

import com.thomsonreuters.metadataregistry.constants.Constants;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Hidden
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MetaDataRegistryException.class)
    public ResponseEntity<Map<String,Object>> handleCustomError(MetaDataRegistryException ex) {
        Map<String, Object> errorResponse = new HashMap<>();
        Map<String, String> errorDetails = new HashMap<>();
        errorDetails.put("code",ex.getCode() );
        errorDetails.put("message", ex.getMessage());
        errorResponse.put("error", errorDetails);
        if(ex.getCode().equals("INTERNAL_SERVER_ERROR")){
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if(ex.getCode().equals("NOT_FOUND")){
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }
        if(ex.getCode().equals("CONFLICT")){
            return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
        }
        if(ex.getCode().equals("INVALID_REQUEST")){
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericError(Exception ex) {
        Map<String, Object> errorResponse = new HashMap<>();
        Map<String, String> errorDetails = new HashMap<>();
        errorDetails.put("code", "INVALID_REQUEST");
        errorDetails.put("message", Constants.VALIDATION_ERROR);
        errorResponse.put("error", errorDetails);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    @ResponseBody
    public ResponseEntity<String> handleHttpMediaTypeNotAcceptableException() {
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                .body("Acceptable MIME type: " + MediaType.APPLICATION_JSON_VALUE);
    }

}
