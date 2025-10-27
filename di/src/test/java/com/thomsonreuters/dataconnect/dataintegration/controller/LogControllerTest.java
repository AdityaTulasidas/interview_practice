package com.thomsonreuters.dataconnect.dataintegration.controller;

import com.thomsonreuters.dataconnect.dataintegration.controllers.LogController;
import com.thomsonreuters.dataconnect.dataintegration.dto.ActivityLogDTO;
import com.thomsonreuters.dataconnect.dataintegration.dto.JobExecutionLogSearchDTO;
import com.thomsonreuters.dataconnect.dataintegration.exceptionhandler.DataSyncJobException;
import com.thomsonreuters.dataconnect.dataintegration.services.LogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LogControllerTest {

    @Mock
    private LogService logService;

    @InjectMocks
    private LogController logController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldReturnActivityLogsSuccessfully() throws Exception {
        Page<ActivityLogDTO> page = new PageImpl<>(Collections.emptyList());
        when(logService.getActivityLogs(any(), any(), any(), any(), any(), any(), any())).thenReturn(page);

        ResponseEntity<?> response = logController.getActivityLogs("execId", "2024-06-01", "2024-06-02", "domain", "job", "tenant", 0, 10);

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(((java.util.Map<?, ?>) response.getBody()).containsKey("logs"));
    }

    @Test
    void shouldThrowExceptionWhenExecutionIdAndFromAreMissing() {
        DataSyncJobException ex = assertThrows(DataSyncJobException.class, () ->
                logController.getActivityLogs(null, null, null, null, null, null, 0, 10));
        assertEquals("Either 'execution_id' or 'from' must be provided.", ex.getMessage());
    }

    @Test
    void shouldSetToFromWhenToIsNullInActivityLogs() throws Exception {
        Page<ActivityLogDTO> page = new PageImpl<>(Collections.emptyList());
        when(logService.getActivityLogs(any(), any(), any(), any(), any(), any(), any())).thenReturn(page);

        ResponseEntity<?> response = logController.getActivityLogs("execId", "2024-06-01", null, null, null, null, 0, 10);

        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    void shouldPropagateExceptionFromServiceInActivityLogs() throws Exception {
        when(logService.getActivityLogs(any(), any(), any(), any(), any(), any(), any()))
                .thenThrow(new DataSyncJobException("error", "ERR"));
        assertThrows(DataSyncJobException.class, () ->
                logController.getActivityLogs("execId", "2024-06-01", "2024-06-02", null, null, null, 0, 10));
    }

    @Test
    void shouldReturnJobExecutionLogsSuccessfully() throws Exception {
        Page<JobExecutionLogSearchDTO> page = new PageImpl<>(Collections.emptyList());
        when(logService.getJobExecutionLogs(any(), any(), any(), any(), any(), any())).thenReturn(page);

        ResponseEntity<?> response = logController.getJobExecutionLogs("2024-06-01", "2024-06-02", "domain", "job", "tenant", 0, 10);

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(((java.util.Map<?, ?>) response.getBody()).containsKey("logs"));
    }

    @Test
    void shouldThrowExceptionWhenFromIsMissingInJobExecutionLogs() {
        DataSyncJobException ex = assertThrows(DataSyncJobException.class, () ->
                logController.getJobExecutionLogs(null, null, null, null, null, 0, 10));
        assertEquals("'from' parameter is required.", ex.getMessage());
    }

    @Test
    void shouldSetToFromWhenToIsNullInJobExecutionLogs() throws Exception {
        Page<JobExecutionLogSearchDTO> page = new PageImpl<>(Collections.emptyList());
        when(logService.getJobExecutionLogs(any(), any(), any(), any(), any(), any())).thenReturn(page);

        ResponseEntity<?> response = logController.getJobExecutionLogs("2024-06-01", null, null, null, null, 0, 10);

        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    void shouldPropagateExceptionFromServiceInJobExecutionLogs() throws Exception {
        when(logService.getJobExecutionLogs(any(), any(), any(), any(), any(), any()))
                .thenThrow(new DataSyncJobException("error", "ERR"));
        assertThrows(DataSyncJobException.class, () ->
                logController.getJobExecutionLogs("2024-06-01", "2024-06-02", null, null, null, 0, 10));
    }
}