package com.thomsonreuters.dataconnect.dataintegration.controllers;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.thomsonreuters.dataconnect.dataintegration.constant.Constants;
import com.thomsonreuters.dataconnect.dataintegration.dto.DatasyncJobConfigurationRequestDTO;
import com.thomsonreuters.dataconnect.dataintegration.dto.DatasyncJobConfigurationSearchResponseDTO;
import com.thomsonreuters.dataconnect.dataintegration.dto.DatasyncJobConfigurationUpdateRequestDTO;
import com.thomsonreuters.dataconnect.dataintegration.exceptionhandler.DataSyncJobException;
import com.thomsonreuters.dataconnect.dataintegration.model.entity.MetaObject;
import com.thomsonreuters.dataconnect.dataintegration.services.DataSyncJobConfigService;
import com.thomsonreuters.dataconnect.dataintegration.services.MetaObjectService;
import com.thomsonreuters.dataconnect.dataintegration.utils.CommonUtils;
import com.thomsonreuters.dataconnect.dataintegration.utils.PayloadTrimmingUtil;
import com.thomsonreuters.dep.api.spring.annotations.Filter;
import com.thomsonreuters.dep.api.spring.annotations.Limit;
import com.thomsonreuters.dep.api.spring.annotations.Offset;
import com.thomsonreuters.dep.api.spring.annotations.Sort;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/jobs")
public class JobConfigurationController {

    private final DataSyncJobConfigService dataSyncJobConfigService;

    @Autowired
    private MetaObjectService metaObjectService;

    @Autowired
    public JobConfigurationController(DataSyncJobConfigService dataSyncJobConfigService) {
        this.dataSyncJobConfigService = dataSyncJobConfigService;
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = Constants.API_HEADER_ONESOURCE_JOB_CREATION_SUCCESS,
                    content = {@Content(mediaType = Constants.API_HEADER_CONTENT_JSON,
                            schema = @Schema(implementation = String.class))}),
            @ApiResponse(responseCode = "400", description = Constants.API_HEADER_BAD_REQUEST,
                    content = {@Content(mediaType = Constants.API_HEADER_CONTENT_JSON,
                            schema = @Schema(implementation = String.class))}),
            @ApiResponse(responseCode = "500", description = Constants.API_HEADER_SERVER_ERROR,
                    content = {@Content(mediaType = Constants.API_HEADER_CONTENT_JSON,
                            schema = @Schema(implementation = String.class))})
    })
    @PostMapping
    public ResponseEntity<Object> createDataSyncJob(@Valid @RequestBody DatasyncJobConfigurationRequestDTO request, BindingResult bindingResult) throws DataSyncJobException, JsonProcessingException {
        // Trim payload values before validation
        PayloadTrimmingUtil.trimJobConfigurationRequest(request);
        
        if (bindingResult.hasErrors()) {
            throw new DataSyncJobException(Constants.VALIDATION_ERROR, Constants.DATASYNC_JOB_BAD_REQUEST);
        }
        String jobId = dataSyncJobConfigService.createDataSyncJob(request);
        // Call usage counter
        MetaObject metaObject = dataSyncJobConfigService.getMetaObjectDetails(request.getMetaObjectSysName());
        metaObjectService.incrementUsageCount(metaObject.getId(), 1, true);
        return new ResponseEntity<>(CommonUtils.generateResponse(jobId, Constants.JOB_CONFIG_CREATED_SUCCESSFULLY), HttpStatus.CREATED);
    }

    @GetMapping("/{jobId}")
    @ApiResponse(responseCode = "200", description = Constants.DATASYNC_JOB_SUCCESSFUL_FETCH,
            content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = DatasyncJobConfigurationRequestDTO.class))})
    @ApiResponse(responseCode = "401", description = Constants.API_UNAUTHORIZED)
    @ApiResponse(responseCode = "400", description = Constants.DATASYNC_JOB_BAD_REQUEST,
            content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = Map.class), examples = @ExampleObject(value = Constants.DATASYNC_JOB_BAD_REQUEST))})
    @ApiResponse(responseCode = "404", description = Constants.DATASYNC_JOB_NOT_FOUND,
            content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = String.class), examples = @ExampleObject(value = Constants.DATASYNC_JOB_NOT_FOUND))})

    public ResponseEntity<Object> getJobConfiguration(@PathVariable UUID jobId) throws DataSyncJobException {
        return new ResponseEntity<>(dataSyncJobConfigService.getJobConfigurationDetails(jobId), HttpStatus.OK);
    }

    @PutMapping( "/{jobId}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = Constants.DATA_SOURCE_UPDATED_SUCCESSFULLY,
                    content = {@Content(mediaType = Constants.API_HEADER_CONTENT_JSON,
                            schema = @Schema(implementation = String.class))}),
            @ApiResponse(responseCode = "204", description = Constants.API_HEADER_NO_CONTENT),
            @ApiResponse(responseCode = "400", description = Constants.API_HEADER_BAD_REQUEST,
                    content = {@Content(mediaType = Constants.API_HEADER_CONTENT_JSON,
                            schema = @Schema(implementation = Map.class), examples = @ExampleObject(value = Constants.ERROR_CREATING_DATA_SOURCE))}),
            @ApiResponse(responseCode = "500", description = Constants.API_HEADER_SERVER_ERROR,
                    content = {@Content(mediaType = Constants.API_HEADER_CONTENT_JSON,
                            schema = @Schema(implementation = String.class), examples = @ExampleObject(value = Constants.ERROR_RETRIEVING_DATA_SOURCE_ENTRIES))})
    })
    public ResponseEntity<Object> updateDataSyncJob(@PathVariable  UUID jobId, @Valid @RequestBody DatasyncJobConfigurationUpdateRequestDTO request,BindingResult bindingResult) throws DataSyncJobException, JsonProcessingException {
        // Trim payload values before validation
        PayloadTrimmingUtil.trimJobConfigurationUpdate(request);
        
        if (bindingResult.hasErrors()) {
            throw new DataSyncJobException(Constants.VALIDATION_ERROR, Constants.DATASYNC_JOB_BAD_REQUEST);
        }
        request.setId(jobId);
        String updatedJobId = dataSyncJobConfigService.updateDataSyncJob(request);
        return new ResponseEntity<>(CommonUtils.generateResponse(updatedJobId, Constants.JOB_CONFIG_UPDATED_SUCCESSFULLY), HttpStatus.OK);
    }

    @Operation(summary = "Search Datasync Job Configurations", description = "Retrieve Datasync Job Configurations either by onesource_domain or customer_id or source_region")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "A list of Datasync Job configurations that match the search criteria.",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = DatasyncJobConfigurationSearchResponseDTO.class))}),
            @ApiResponse(responseCode = "400",
                    description = "Bad Request",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "The request is invalid due to missing or incorrect parameters."))}),
            @ApiResponse(responseCode = "404", description = "Not Found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "The requested resource was not found."))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Authentication is required and has failed or has not yet been provided."))})
    })
    @GetMapping
    public ResponseEntity<?> getDataSyncJobs(@Offset Integer offset,
                                             @Limit Integer limit,
                                             @Sort String sort,
                                             @Filter String filter) throws DataSyncJobException {
        // Validate pagination and sorting parameters
        if (offset != null && offset < 0) {
            throw new DataSyncJobException("Offset must be greater than or equal to 0.", "INVALID_REQUEST");
        }
        if (limit != null && limit <= 0) {
            throw new DataSyncJobException("Limit must be greater than 0.", "INVALID_REQUEST");
        }
        int page = offset != null ? offset : 0;
        int size = limit != null ? limit : 200;
        return dataSyncJobConfigService.searchJobConfiguration(page, size, sort, filter);
    }
}
