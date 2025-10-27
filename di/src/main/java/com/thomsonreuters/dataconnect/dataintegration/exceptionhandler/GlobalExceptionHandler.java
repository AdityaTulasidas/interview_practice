package com.thomsonreuters.dataconnect.dataintegration.exceptionhandler;

import com.thomsonreuters.dataconnect.common.logging.LogClient;
import com.thomsonreuters.dataconnect.dataintegration.configuration.RegionConfig;
import com.thomsonreuters.dataconnect.dataintegration.constant.Constants;
import com.thomsonreuters.dataconnect.dataintegration.repository.ActivityLogRepository;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import com.thomsonreuters.dataconnect.dataintegration.exceptionhandler.NoUgeDataException;

@Hidden
@RestControllerAdvice
public class GlobalExceptionHandler {

    @Autowired
    private LogClient logClient;

    @Autowired
    private RegionConfig regionConfig;

    // Setter for test injection
    void setRegionConfig(RegionConfig regionConfig) {
        this.regionConfig = regionConfig;
    }

    @Autowired
    private ActivityLogRepository activityLogRepository;

    LocalDateTime localdateTime = null;

    @ExceptionHandler(NoUgeDataException.class)
    public ResponseEntity<Map<String, Object>> handleNoUgeDataException(NoUgeDataException ex) {
        Map<String, Object> errorResponse = new HashMap<>();
        Map<String, String> errorDetails = new HashMap<>();
        errorDetails.put(Constants.CODE, "NOT_FOUND");
        errorDetails.put(Constants.MESSAGE, ex.getMessage());
        errorResponse.put(Constants.ERROR, errorDetails);
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DataSyncJobException.class)
    public ResponseEntity<Map<String,Object>> handleCustomError(DataSyncJobException ex) {
        Map<String, Object> errorResponse = new HashMap<>();
        Map<String, String> errorDetails = new HashMap<>();
        errorDetails.put(Constants.CODE,ex.getCode() );
        errorDetails.put(Constants.MESSAGE, ex.getMessage());
        errorResponse.put(Constants.ERROR, errorDetails);
        if(ex.getCode().equals("INTERNAL_SERVER_ERROR")){
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if(ex.getCode().equals("NOT_FOUND")){
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }
        if(ex.getCode().equals("CONFLICT")){
            return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        Map<String, Object> errorResponse = new HashMap<>();
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put(Constants.CODE, Constants.DATASYNC_JOB_BAD_REQUEST);
        errorDetails.put(Constants.MESSAGE, "Request validation failed");
        errorDetails.put("field_errors", ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> Map.of("field", fe.getField(), "message", fe.getDefaultMessage()))
                .toList());
        errorResponse.put(Constants.ERROR, errorDetails);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({ConstraintViolationException.class})
    public ResponseEntity<Map<String, Object>> handleConstraintViolation(ConstraintViolationException ex) {
        Map<String, Object> errorResponse = new HashMap<>();
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put(Constants.CODE, Constants.DATASYNC_JOB_BAD_REQUEST);
        errorDetails.put(Constants.MESSAGE, "Constraint violation");
        errorDetails.put("violations", ex.getConstraintViolations().stream()
                .map(v -> Map.of("property", v.getPropertyPath().toString(), "message", v.getMessage()))
                .toList());
        errorResponse.put(Constants.ERROR, errorDetails);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericError(Exception ex) {
        Map<String, Object> errorResponse = new HashMap<>();
        Map<String, Object> errorDetails = new HashMap<>();

        String code = Constants.DATASYNC_JOB_BAD_REQUEST;
        String message = Constants.VALIDATION_ERROR;

        if (ex instanceof DataIntegrityViolationException) {
            code = Constants.DATASYNC_JOB_BAD_REQUEST;
            message = "Data integrity violation";
        } else if (ex.getCause() instanceof DataIntegrityViolationException) {
            code = Constants.DATASYNC_JOB_BAD_REQUEST;
            message = "Data integrity violation";
        }

        errorDetails.put(Constants.CODE, code);
        errorDetails.put(Constants.MESSAGE, message);
        errorDetails.put("exception_type", ex.getClass().getSimpleName());
        errorResponse.put(Constants.ERROR, errorDetails);

        // Basic log (kept minimal – underlying LogClient can be integrated if desired)
        ex.printStackTrace();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        Map<String, Object> errorResponse = new HashMap<>();
        Map<String, String> errorDetails = new HashMap<>();
        errorDetails.put(Constants.CODE, Constants.DATASYNC_JOB_BAD_REQUEST);
        errorDetails.put(Constants.MESSAGE, Constants.VALIDATION_ERROR);
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
