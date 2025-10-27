package com.thomsonreuters.dataconnect.dataintegration.controllers;

import com.thomsonreuters.dataconnect.dataintegration.configuration.ModelMapperConfig;
import com.thomsonreuters.dataconnect.dataintegration.constant.Constants;
import com.thomsonreuters.dataconnect.dataintegration.dto.JobExecutionLogDto;
import com.thomsonreuters.dataconnect.dataintegration.exceptionhandler.DataSyncJobException;
import com.thomsonreuters.dataconnect.dataintegration.model.entity.DatasyncJobConfiguration;
import com.thomsonreuters.dataconnect.dataintegration.model.entity.JobExecutionLog;
import com.thomsonreuters.dataconnect.dataintegration.repository.JobExecutionLogRepository;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/job-execution-log")
public class JobExecutionLogController {

    private final JobExecutionLogRepository jobExecutionLogRepository;
    private final ModelMapperConfig modelMapperConfig;

    public JobExecutionLogController(JobExecutionLogRepository jobExecutionLogRepository, ModelMapperConfig modelMapperConfig) {
        this.jobExecutionLogRepository = jobExecutionLogRepository;
        this.modelMapperConfig = new ModelMapperConfig();
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "JobExecutionLog created successfully.",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = JobExecutionLogDto.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid request data.",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Invalid JobExecutionLog data."))}),
            @ApiResponse(responseCode = "500", description = "Internal server error.",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "An error occurred while saving the JobExecutionLog."))})
    })
    @PostMapping
    public ResponseEntity<Object> saveJobExecutionLog(@Valid @RequestBody JobExecutionLogDto jobExecutionLogDto, BindingResult bindingResult) {
        try {
            // Validate the request body
            if (bindingResult.hasErrors()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Invalid JobExecutionLog data: " + bindingResult.getAllErrors());
            }
            JobExecutionLog jobExecutionLog =modelMapperConfig.modelMapper().map(jobExecutionLogDto, JobExecutionLog.class);
            JobExecutionLog savedLog = jobExecutionLogRepository.save(jobExecutionLog);
            JobExecutionLogDto jobExecutionLogDtoResponse =modelMapperConfig.modelMapper().map(savedLog, JobExecutionLogDto.class);
            return ResponseEntity.status(HttpStatus.CREATED).body(jobExecutionLogDtoResponse);
        } catch (Exception e) {
            log.error("Error saving JobExecutionLog: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while saving the JobExecutionLog.");
        }
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "JobExecutionLog updated successfully.",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = JobExecutionLogDto.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid request data.",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Invalid JobExecutionLog data."))}),
            @ApiResponse(responseCode = "404", description = "JobExecutionLog not found.",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "JobExecutionLog with the given ID not found."))}),
            @ApiResponse(responseCode = "500", description = "Internal server error.",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "An error occurred while updating the JobExecutionLog."))})
    })
    @PutMapping("/{id}")
    public ResponseEntity<Object> updateJobExecutionLog(@PathVariable UUID id,
                                                        @Valid @RequestBody JobExecutionLogDto jobExecutionLogDto,
                                                        BindingResult bindingResult) {
        try {
            // Validate the request body
            if (bindingResult.hasErrors()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Invalid JobExecutionLog data: " + bindingResult.getAllErrors());
            }
            // Check if the JobExecutionLog exists
            if (!jobExecutionLogRepository.existsById(id)) {
                throw new DataSyncJobException("JobExecutionLog with the given ID not found.", Constants.NOT_FOUND);
            }
            JobExecutionLog jobExecutionLog =modelMapperConfig.modelMapper().map(jobExecutionLogDto, JobExecutionLog.class);
            jobExecutionLog.setId(id);
            // Update the JobExecutionLog entity
            JobExecutionLog updatedLog = jobExecutionLogRepository.save(jobExecutionLog);
            JobExecutionLogDto jobExecutionLogDtoResponse =modelMapperConfig.modelMapper().map(updatedLog, JobExecutionLogDto.class);
            return ResponseEntity.status(HttpStatus.OK).body(jobExecutionLogDtoResponse);
        } catch (Exception e) {
            log.error("Error updating JobExecutionLog: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while updating the JobExecutionLog.");
        }
    }
}