package com.thomsonreuters.dataconnect.dataintegration.controllers;


import com.thomsonreuters.dataconnect.dataintegration.constant.Constants;
import com.thomsonreuters.dataconnect.dataintegration.dto.ActivityLogDTO;
import com.thomsonreuters.dataconnect.dataintegration.dto.JobExecutionLogSearchDTO;
import com.thomsonreuters.dataconnect.dataintegration.exceptionhandler.DataSyncJobException;
import com.thomsonreuters.dataconnect.dataintegration.services.LogService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/logs")
public class LogController {

    private  final LogService logService;

    public LogController(LogService logService) {
        this.logService = logService;
    }

    @ApiResponse(responseCode = "200", description = "Successful retrieval of activity logs",
            content = {@Content(mediaType = Constants.API_HEADER_CONTENT_JSON,
                    schema = @Schema(implementation = String.class))})
    @ApiResponse(responseCode = "404", description = "Activity logs not found",
            content = {@Content(mediaType = Constants.API_HEADER_CONTENT_JSON,
                    schema = @Schema(implementation = String.class))})
    @ApiResponse(responseCode = "500", description = "Internal Server Error")
    @GetMapping("/activity-logs")
    public ResponseEntity<?> getActivityLogs(@RequestParam(required = false) String executionId,
                                             @RequestParam(required = false) String from,
                                             @RequestParam(required = false) String to,
                                             @RequestParam(required = false) String domain,
                                             @RequestParam(required = false) String onesourceJobName,
                                             @RequestParam(required = false) String customerTenant,
                                             @RequestParam(defaultValue = "0") int page,
                                             @RequestParam(defaultValue = "200") int size) throws DataSyncJobException {

            if (executionId== null && from == null) {
                throw new DataSyncJobException("Either 'execution_id' or 'from' must be provided.", "INVALID_REQUEST");
            }
            if (to == null) {
                to = from;
            }
            Pageable pageable = PageRequest.of(page, size);
            Page<ActivityLogDTO> logs = logService.getActivityLogs(executionId, from, to, domain, onesourceJobName, customerTenant, pageable);
            return ResponseEntity.ok().body(Map.of("logs", logs));
    }

    @ApiResponse(responseCode = "200", description = "Successful retrieval of job execution logs",
            content = {@Content(mediaType = Constants.API_HEADER_CONTENT_JSON,
                    schema = @Schema(implementation = String.class))})
    @ApiResponse(responseCode = "404", description = "Job execution logs not found",
            content = {@Content(mediaType = Constants.API_HEADER_CONTENT_JSON,
                    schema = @Schema(implementation = String.class))})
    @ApiResponse(responseCode = "500", description = "Internal Server Error")
    @GetMapping("/job-logs")
    public ResponseEntity<?> getJobExecutionLogs(
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to,
            @RequestParam(required = false) String domain,
            @RequestParam(required = false) String onesourceJobName,
            @RequestParam(required = false) String customerTenant,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "200") int size) throws DataSyncJobException {

        if (from == null) {
            throw new DataSyncJobException("'from' parameter is required.", "INVALID_REQUEST");
        }
        if (to == null) {
            to = from;
        }
        Pageable pageable = PageRequest.of(page, size);
        Page<JobExecutionLogSearchDTO> logs = logService.getJobExecutionLogs(from, to, domain, onesourceJobName, customerTenant, pageable);
        return ResponseEntity.ok().body(Map.of("logs", logs));
    }
}
