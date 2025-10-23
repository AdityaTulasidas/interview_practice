/*
package com.thomsonreuters.dataconnect.executionengine.controller;

import com.thomsonreuters.dataconnect.executionengine.controllers.JobExecutionLogController;
import com.thomsonreuters.dataconnect.executionengine.dto.JobExecutionLogStatusDTO;
import com.thomsonreuters.dataconnect.executionengine.exceptionhandler.DataSyncJobException;
import com.thomsonreuters.dataconnect.executionengine.model.entity.JobExecutionLog;
import com.thomsonreuters.dep.api.spring.response.ApiCollection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class JobExecutionLogControllerTest {


    @InjectMocks
    private JobExecutionLogController jobExecutionLogController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }



    @Test
    void shouldReturnJobExecutionLogStatus_whenValidJobExecutionId() throws DataSyncJobException {
        // Arrange
        String jobExecutionId = UUID.randomUUID().toString();
        JobExecutionLogStatusDTO statusDTO = new JobExecutionLogStatusDTO(
                UUID.randomUUID(), LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now(), "Status"
        );
        when(dataSyncJob.getJobExecutionLogStatus(jobExecutionId)).thenReturn(statusDTO);

        // Act
        ResponseEntity<JobExecutionLogStatusDTO> response = jobExecutionLogController.getJobExecutionLogStatus(jobExecutionId);

        // Assert
        assertEquals(200, response.getStatusCode().value());
        assertEquals("Status", Objects.requireNonNull(response.getBody()).getStatus());
        verify(dataSyncJob, times(1)).getJobExecutionLogStatus(jobExecutionId);
    }

    @Test
    void shouldThrowException_whenJobExecutionIdNotFound() throws DataSyncJobException {
        // Arrange
        String jobExecutionId = UUID.randomUUID().toString();
        when(dataSyncJob.getJobExecutionLogStatus(jobExecutionId)).thenThrow(new DataSyncJobException("Job execution log not found", "NOT_FOUND"));

        // Act & Assert
        try {
            jobExecutionLogController.getJobExecutionLogStatus(jobExecutionId);
        } catch (DataSyncJobException e) {
            assertEquals("Job execution log not found", e.getMessage());
            assertEquals("NOT_FOUND", e.getCode());
        }
        verify(dataSyncJob, times(1)).getJobExecutionLogStatus(jobExecutionId);
    }
    @Test
    void shouldReturnAllJobExecutionLogs_whenValidParams() throws DataSyncJobException {
        ApiCollection<JobExecutionLog> mockCollection = mock(ApiCollection.class);
        ResponseEntity<?> mockResponse = ResponseEntity.ok(mockCollection);
        when(dataSyncJob.getAllJobExecutionLogs(0, 200, null, null)).thenReturn((ResponseEntity) mockResponse);

        // Act
        ResponseEntity<?> response = jobExecutionLogController.getAllJobExecutionLogs(null, null, null, null);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        verify(dataSyncJob, times(1)).getAllJobExecutionLogs(0, 200, null, null);
    }

    @Test
    void shouldThrowException_whenOffsetIsNegative() {
        // Act & Assert
        DataSyncJobException exception = assertThrows(DataSyncJobException.class, () ->
                jobExecutionLogController.getAllJobExecutionLogs(-1, 10, null, null)
        );
        assertEquals("Offset must be greater than or equal to 0.", exception.getMessage());
        assertEquals("INVALID_REQUEST", exception.getCode());
        verifyNoInteractions(dataSyncJob);
    }

    @Test
    void shouldThrowException_whenLimitIsZeroOrNegative() {
        // Act & Assert
        DataSyncJobException exception = assertThrows(DataSyncJobException.class, () ->
                jobExecutionLogController.getAllJobExecutionLogs(0, 0, null, null)
        );
        assertEquals("Limit must be greater than 0.", exception.getMessage());
        assertEquals("INVALID_REQUEST", exception.getCode());
        verifyNoInteractions(dataSyncJob);
    }
}*/
