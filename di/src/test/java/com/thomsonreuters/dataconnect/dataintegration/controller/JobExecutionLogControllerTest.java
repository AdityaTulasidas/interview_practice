package com.thomsonreuters.dataconnect.dataintegration.controller;

import com.thomsonreuters.dataconnect.dataintegration.configuration.ModelMapperConfig;
import com.thomsonreuters.dataconnect.dataintegration.controllers.JobExecutionLogController;
import com.thomsonreuters.dataconnect.dataintegration.dto.JobExecutionLogDto;
import com.thomsonreuters.dataconnect.dataintegration.model.entity.JobExecutionLog;
import com.thomsonreuters.dataconnect.dataintegration.repository.JobExecutionLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class JobExecutionLogControllerTest {

    @Mock
    private JobExecutionLogRepository jobExecutionLogRepository;

    @Mock
    private BindingResult bindingResult;

    @InjectMocks
    private JobExecutionLogController jobExecutionLogController;

    private JobExecutionLog jobExecutionLog;
    private JobExecutionLogDto jobExecutionLogDto;
    @Mock
    private ModelMapperConfig modelMapperConfig;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Mock the modelMapper() method to return a valid ModelMapper instance
        when(modelMapperConfig.modelMapper()).thenReturn(new org.modelmapper.ModelMapper());
        jobExecutionLog = new JobExecutionLog();
        jobExecutionLog.setId(UUID.randomUUID());
        jobExecutionLogDto = new JobExecutionLogDto();
        jobExecutionLogDto.setId(jobExecutionLog.getId());

    }

    @Test
    void shouldSaveJobExecutionLogSuccessfully() {
        when(bindingResult.hasErrors()).thenReturn(false);
        when(jobExecutionLogRepository.save(any(JobExecutionLog.class))).thenReturn(jobExecutionLog);
        JobExecutionLogDto jobExecutionLogDto =modelMapperConfig.modelMapper().map(jobExecutionLog, JobExecutionLogDto.class);
        ResponseEntity<Object> response = jobExecutionLogController.saveJobExecutionLog(jobExecutionLogDto, bindingResult);

        assertEquals(201, response.getStatusCodeValue());
        verify(jobExecutionLogRepository, times(1)).save(jobExecutionLog);
    }

    @Test
    void shouldReturnBadRequestWhenJobExecutionLogValidationFails() {
        when(bindingResult.hasErrors()).thenReturn(true);
        when(jobExecutionLogRepository.save(any(JobExecutionLog.class))).thenReturn(jobExecutionLog);
        JobExecutionLogDto jobExecutionLogDto =modelMapperConfig.modelMapper().map(jobExecutionLog, JobExecutionLogDto.class);
        ResponseEntity<Object> response = jobExecutionLogController.saveJobExecutionLog(jobExecutionLogDto, bindingResult);

        assertEquals(400, response.getStatusCodeValue());
        verify(jobExecutionLogRepository, never()).save(any(JobExecutionLog.class));
    }

    @Test
    void shouldHandleExceptionWhenSavingJobExecutionLog() {
        when(bindingResult.hasErrors()).thenReturn(false);
        when(jobExecutionLogRepository.save(any(JobExecutionLog.class))).thenThrow(new RuntimeException("Database error"));
        JobExecutionLogDto jobExecutionLogDto =modelMapperConfig.modelMapper().map(jobExecutionLog, JobExecutionLogDto.class);
        ResponseEntity<Object> response = jobExecutionLogController.saveJobExecutionLog(jobExecutionLogDto, bindingResult);

        assertEquals(500, response.getStatusCodeValue());
        verify(jobExecutionLogRepository, times(1)).save(jobExecutionLog);
    }

    @Test
    void shouldUpdateJobExecutionLogSuccessfully() {
        UUID id = jobExecutionLog.getId();
        when(bindingResult.hasErrors()).thenReturn(false);
        when(jobExecutionLogRepository.existsById(id)).thenReturn(true);
        when(jobExecutionLogRepository.save(any(JobExecutionLog.class))).thenReturn(jobExecutionLog);
        JobExecutionLogDto jobExecutionLogDto =modelMapperConfig.modelMapper().map(jobExecutionLog, JobExecutionLogDto.class);
        ResponseEntity<Object> response = jobExecutionLogController.updateJobExecutionLog(id, jobExecutionLogDto, bindingResult);

        assertEquals(200, response.getStatusCodeValue());
        verify(jobExecutionLogRepository, times(1)).save(jobExecutionLog);
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingNonExistentJobExecutionLog() {
        UUID id = jobExecutionLog.getId();
        when(bindingResult.hasErrors()).thenReturn(false);
        when(jobExecutionLogRepository.existsById(id)).thenReturn(false);
        JobExecutionLogDto jobExecutionLogDto =modelMapperConfig.modelMapper().map(jobExecutionLog, JobExecutionLogDto.class);
        ResponseEntity<Object> response = jobExecutionLogController.updateJobExecutionLog(id, jobExecutionLogDto, bindingResult);

        assertEquals(500, response.getStatusCodeValue());
        verify(jobExecutionLogRepository, never()).save(any(JobExecutionLog.class));
    }

    @Test
    void shouldReturnBadRequestWhenUpdatingJobExecutionLogValidationFails() {
        UUID id = jobExecutionLog.getId();
        when(bindingResult.hasErrors()).thenReturn(true);
        JobExecutionLogDto jobExecutionLogDto =modelMapperConfig.modelMapper().map(jobExecutionLog, JobExecutionLogDto.class);
        ResponseEntity<Object> response = jobExecutionLogController.updateJobExecutionLog(id, jobExecutionLogDto, bindingResult);

        assertEquals(400, response.getStatusCodeValue());
        verify(jobExecutionLogRepository, never()).save(any(JobExecutionLog.class));
    }

    @Test
    void shouldHandleExceptionWhenUpdatingJobExecutionLog() {
        UUID id = jobExecutionLog.getId();
        when(bindingResult.hasErrors()).thenReturn(false);
        when(jobExecutionLogRepository.existsById(id)).thenReturn(true);
        when(jobExecutionLogRepository.save(any(JobExecutionLog.class))).thenThrow(new RuntimeException("Database error"));
        JobExecutionLogDto jobExecutionLogDto =modelMapperConfig.modelMapper().map(jobExecutionLog, JobExecutionLogDto.class);
        ResponseEntity<Object> response = jobExecutionLogController.updateJobExecutionLog(id, jobExecutionLogDto, bindingResult);

        assertEquals(500, response.getStatusCodeValue());
        verify(jobExecutionLogRepository, times(1)).save(jobExecutionLog);
    }
}