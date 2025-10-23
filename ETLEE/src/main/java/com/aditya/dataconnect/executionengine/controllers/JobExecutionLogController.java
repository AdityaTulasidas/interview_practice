package com.aditya.dataconnect.executionengine.controllers;

import com.aditya.dataconnect.executionengine.constant.Constants;
import com.aditya.dataconnect.executionengine.dto.JobExecutionLogStatusDTO;
import com.aditya.dataconnect.executionengine.exceptionhandler.DataSyncJobException;
import com.aditya.dataconnect.executionengine.services.job.DataSyncJob;
import com.thomsonreuters.dep.api.spring.annotations.Filter;
import com.thomsonreuters.dep.api.spring.annotations.Limit;
import com.thomsonreuters.dep.api.spring.annotations.Offset;
import com.thomsonreuters.dep.api.spring.annotations.Sort;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/data-connect/jobs/execution-logs")
public class JobExecutionLogController {

    private final DataSyncJob dataSyncJob;

    public JobExecutionLogController(DataSyncJob dataSyncJob) {
        this.dataSyncJob = dataSyncJob;
    }

    @GetMapping
    @ApiResponse(responseCode = "200", description = "Fetch all job execution logs")
    @ApiResponse(responseCode = "500", description = "Internal Server Error")
    public ResponseEntity<?> getAllJobExecutionLogs(
            @Offset Integer offset,
            @Limit Integer limit,
            @Sort String sort,
            @Filter String filter

    ) throws DataSyncJobException {

        if (offset != null && offset < 0) {
            throw new DataSyncJobException("Offset must be greater than or equal to 0.", "INVALID_REQUEST");
        }
        if (limit != null && limit <= 0) {
            throw new DataSyncJobException("Limit must be greater than 0.", "INVALID_REQUEST");
        }
        // It should return all job execution logs.
        int page = offset != null ? offset : 0;
        int size = limit != null ? limit : 200;

        return dataSyncJob.getAllJobExecutionLogs(page, size, sort, filter);

    }

    @GetMapping("/{job_execution_id}")
    @ApiResponse(responseCode = "200", description = Constants.API_HEADER_ONESOURCE_JOB_EXECUTION_SUCCESS,
            content = {@Content(mediaType = Constants.API_HEADER_CONTENT_JSON,
                    schema = @Schema(implementation = String.class))})
    @ApiResponse(responseCode = "404", description = "Job execution log not found",
    content = {@Content(mediaType = Constants.API_HEADER_CONTENT_JSON,
                    schema = @Schema(implementation = String.class))})
    @ApiResponse(responseCode = "500", description = "Internal Server Error")
    public ResponseEntity<JobExecutionLogStatusDTO> getJobExecutionLogStatus(@PathVariable("job_execution_id") String jobExecutionId) throws DataSyncJobException {
        // It should return the status of a specific job execution log.
        JobExecutionLogStatusDTO status = dataSyncJob.getJobExecutionLogStatus(jobExecutionId);
        return ResponseEntity.ok(status);
    }
}
