package com.thomsonreuters.dataconnect.dataintegration.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.thomsonreuters.dataconnect.dataintegration.constant.Constants;
import com.thomsonreuters.dataconnect.dataintegration.exceptionhandler.DataSyncJobException;
import com.thomsonreuters.dataconnect.dataintegration.model.pojo.ChildRequestModel;
import com.thomsonreuters.dataconnect.dataintegration.model.pojo.OptionalRequestBody;
import com.thomsonreuters.dataconnect.dataintegration.model.pojo.RequestModel;
import com.thomsonreuters.dataconnect.dataintegration.repository.ActivityLogRepository;
import com.thomsonreuters.dataconnect.dataintegration.services.DataSyncService;
import com.thomsonreuters.dataconnect.dataintegration.utils.CommonUtils;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/jobs")
public class DataIntegrationManagerController {

    private final DataSyncService dataSyncService;

    @Autowired
    public DataIntegrationManagerController(DataSyncService dataSyncService) {
        this.dataSyncService = dataSyncService;
    }

    @Autowired
    private ActivityLogRepository activityLogRepository;

    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = Constants.JOB_EXECUTION_SUCCESS,
                    content = {@Content(mediaType = Constants.API_HEADER_CONTENT_JSON,
                            schema = @Schema(implementation = String.class))}),
            @ApiResponse(responseCode = "400", description = Constants.API_HEADER_BAD_REQUEST,
                    content = {@Content(mediaType = Constants.API_HEADER_CONTENT_JSON,
                            schema = @Schema(implementation = Map.class), examples = @ExampleObject(value = Constants.ERROR_CREATING_DATA_SOURCE))}),
            @ApiResponse(responseCode = "500", description = Constants.API_HEADER_SERVER_ERROR,
                    content = {@Content(mediaType = Constants.API_HEADER_CONTENT_JSON,
                            schema = @Schema(implementation = String.class), examples = @ExampleObject(value = Constants.ERROR_RETRIEVING_DATA_SOURCE_ENTRIES))})
    })
    @PostMapping("/execute/{jobId}")
    public ResponseEntity<Object> publishDataSyncJob(@PathVariable(name = "jobId") UUID requestJobId,
                                                     @Valid @RequestBody OptionalRequestBody optionalRequestBody,
                                                     @RequestParam(name = "operationType", required = false, defaultValue = "") String operationType,
                                                     BindingResult bindingResult) throws DataSyncJobException, JsonProcessingException {

        if (bindingResult.hasErrors()) {
            throw new DataSyncJobException(Constants.VALIDATION_ERROR, Constants.DATASYNC_JOB_BAD_REQUEST);
        }
        String jobExecutionId = dataSyncService.publishDataSyncJob(requestJobId, optionalRequestBody, operationType);
        return new ResponseEntity<>(CommonUtils.generateResponse(jobExecutionId, Constants.JOB_EXECUTION_SUCCESS), HttpStatus.CREATED);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = Constants.JOB_EXECUTION_SUCCESS,
                    content = {@Content(mediaType = Constants.API_HEADER_CONTENT_JSON,
                            schema = @Schema(implementation = String.class))}),
            @ApiResponse(responseCode = "400", description = Constants.API_HEADER_BAD_REQUEST,
                    content = {@Content(mediaType = Constants.API_HEADER_CONTENT_JSON,
                            schema = @Schema(implementation = Map.class), examples = @ExampleObject(value = Constants.ERROR_CREATING_DATA_SOURCE))}),
            @ApiResponse(responseCode = "500", description = Constants.API_HEADER_SERVER_ERROR,
                    content = {@Content(mediaType = Constants.API_HEADER_CONTENT_JSON,
                            schema = @Schema(implementation = String.class), examples = @ExampleObject(value = Constants.ERROR_RETRIEVING_DATA_SOURCE_ENTRIES))})
    })
    @PostMapping("/execute")
    public ResponseEntity<Object> publishDataSync(@RequestBody(required = false) RequestModel requestModel) throws DataSyncJobException, JsonProcessingException {
        String jobId = dataSyncService.publishDataSyncJob(requestModel);
        return new ResponseEntity<>(CommonUtils.generateResponse(jobId, Constants.JOB_EXECUTION_SUCCESS), HttpStatus.CREATED);
    }

    @PostMapping("/validate")
    public ResponseEntity<Boolean> validateDataSyncJob(@RequestBody RequestModel requestModel) {
        try {
            boolean isJobValid = dataSyncService.validateRequestModelAndJob(requestModel, null);
            return ResponseEntity.status(HttpStatus.OK).body(isJobValid);
        } catch (DataSyncJobException e) {
            log.error("DataSync job not found : {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.OK).body(false);
        }
    }


    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = Constants.JOB_EXECUTION_SUCCESS,
                    content = {@Content(mediaType = Constants.API_HEADER_CONTENT_JSON,
                            schema = @Schema(implementation = String.class))}),
            @ApiResponse(responseCode = "400", description = Constants.API_HEADER_BAD_REQUEST,
                    content = {@Content(mediaType = Constants.API_HEADER_CONTENT_JSON,
                            schema = @Schema(implementation = Map.class), examples = @ExampleObject(value = Constants.ERROR_CREATING_DATA_SOURCE))}),
            @ApiResponse(responseCode = "500", description = Constants.API_HEADER_SERVER_ERROR,
                    content = {@Content(mediaType = Constants.API_HEADER_CONTENT_JSON,
                            schema = @Schema(implementation = String.class), examples = @ExampleObject(value = Constants.ERROR_RETRIEVING_DATA_SOURCE_ENTRIES))})
    })
    @PostMapping("/execute/child")
    public ResponseEntity<Object> executeChildJob(@RequestBody(required = false) ChildRequestModel requestModel)
            throws DataSyncJobException, JsonProcessingException {
        String childJobId = dataSyncService.childDataSyncJob(requestModel);
        return new ResponseEntity<>(CommonUtils.generateResponse(childJobId, Constants.JOB_EXECUTION_SUCCESS), HttpStatus.CREATED);
    }
}









