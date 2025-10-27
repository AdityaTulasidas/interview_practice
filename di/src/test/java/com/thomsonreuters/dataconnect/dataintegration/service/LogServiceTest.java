package com.thomsonreuters.dataconnect.dataintegration.service;

import com.thomsonreuters.dataconnect.dataintegration.configuration.ModelMapperConfig;
import com.thomsonreuters.dataconnect.dataintegration.constant.Constants;
import com.thomsonreuters.dataconnect.dataintegration.dto.ActivityLogDTO;
import com.thomsonreuters.dataconnect.dataintegration.dto.JobExecutionLogSearchDTO;
import com.thomsonreuters.dataconnect.dataintegration.exceptionhandler.DataSyncJobException;
import com.thomsonreuters.dataconnect.dataintegration.model.entity.ActivityLog;
import com.thomsonreuters.dataconnect.dataintegration.model.entity.JobExecutionLog;
import com.thomsonreuters.dataconnect.dataintegration.repository.ActivityLogRepository;
import com.thomsonreuters.dataconnect.dataintegration.repository.JobExecutionLogRepository;
import com.thomsonreuters.dataconnect.dataintegration.services.LogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LogServiceTest {

    @Mock
    private ActivityLogRepository activityLogRepository;
    @Mock
    private JobExecutionLogRepository jobExecutionLogRepository;
    @Mock
    private ModelMapperConfig modelMapperConfig;
    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private LogService logService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(modelMapperConfig.modelMapper()).thenReturn(modelMapper);
    }

    @Test
    void shouldReturnActivityLogsWhenValidParams() throws Exception {
        ActivityLog log = new ActivityLog();
        ActivityLogDTO dto = new ActivityLogDTO();
        Page<ActivityLog> page = new PageImpl<>(List.of(log));
        when(activityLogRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);
        when(modelMapper.map(any(ActivityLog.class), eq(ActivityLogDTO.class))).thenReturn(dto);

        Page<ActivityLogDTO> result = logService.getActivityLogs(null, "2024-06-01", "2024-06-02", null, null, null, PageRequest.of(0, 10));
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void shouldThrowExceptionWhenExecIdIsInvalid() {
        String invalidExecId = UUID.randomUUID().toString();
        when(activityLogRepository.findByJobExecutionId(any(UUID.class))).thenReturn(Collections.emptyList());

        DataSyncJobException ex = assertThrows(DataSyncJobException.class, () ->
                logService.getActivityLogs(invalidExecId, null, null, null, null, null, PageRequest.of(0, 10)));
        assertTrue(ex.getMessage().contains("Invalid execution id"));
    }

    @Test
    void shouldReturnActivityLogsWhenExecIdIsValid() throws Exception {
        String execId = UUID.randomUUID().toString();
        ActivityLog log = new ActivityLog();
        ActivityLogDTO dto = new ActivityLogDTO();
        when(activityLogRepository.findByJobExecutionId(any(UUID.class))).thenReturn(List.of(log));
        when(activityLogRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(new PageImpl<>(List.of(log)));
        when(modelMapper.map(any(ActivityLog.class), eq(ActivityLogDTO.class))).thenReturn(dto);

        Page<ActivityLogDTO> result = logService.getActivityLogs(execId, null, null, null, null, null, PageRequest.of(0, 10));
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void shouldReturnJobExecutionLogsWhenValidParams() throws Exception {
        JobExecutionLog log = new JobExecutionLog();
        JobExecutionLogSearchDTO dto = new JobExecutionLogSearchDTO();
        Page<JobExecutionLog> page = new PageImpl<>(List.of(log));
        when(jobExecutionLogRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);
        when(modelMapper.map(any(JobExecutionLog.class), eq(JobExecutionLogSearchDTO.class))).thenReturn(dto);

        Page<JobExecutionLogSearchDTO> result = logService.getJobExecutionLogs("2024-06-01", "2024-06-02", null, null, null, PageRequest.of(0, 10));
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void shouldParseFlexibleDateTimeWithDateTime() throws Exception {
        LocalDateTime dt = logService.parseFlexibleDateTime("2024-06-01 12:34:56", false);
        assertEquals(2024, dt.getYear());
        assertEquals(6, dt.getMonthValue());
        assertEquals(1, dt.getDayOfMonth());
        assertEquals(12, dt.getHour());
        assertEquals(34, dt.getMinute());
        assertEquals(56, dt.getSecond());
    }

    @Test
    void shouldParseFlexibleDateTimeWithDateOnlyAsFrom() throws Exception {
        LocalDateTime dt = logService.parseFlexibleDateTime("2024-06-01", false);
        assertEquals(0, dt.getHour());
        assertEquals(0, dt.getMinute());
        assertEquals(0, dt.getSecond());
    }

    @Test
    void shouldParseFlexibleDateTimeWithDateOnlyAsTo() throws Exception {
        LocalDateTime dt = logService.parseFlexibleDateTime("2024-06-01", true);
        assertEquals(23, dt.getHour());
        assertEquals(59, dt.getMinute());
        assertEquals(0, dt.getSecond());
    }

    @Test
    void shouldThrowExceptionForInvalidDateFormat() {
        DataSyncJobException ex = assertThrows(DataSyncJobException.class, () ->
                logService.parseFlexibleDateTime("invalid-date", false));
        assertEquals("Invalid date format.", ex.getMessage());
    }

    @Test
    void shouldReturnEmptyActivityLogsWhenNoResults() throws Exception {
        when(activityLogRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(Page.empty());
        Page<ActivityLogDTO> result = logService.getActivityLogs(null, "2024-06-01", "2024-06-02", null, null, null, PageRequest.of(0, 10));
        assertEquals(0, result.getTotalElements());
    }

    @Test
    void shouldReturnEmptyJobExecutionLogsWhenNoResults() throws Exception {
        when(jobExecutionLogRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(Page.empty());
        Page<JobExecutionLogSearchDTO> result = logService.getJobExecutionLogs("2024-06-01", "2024-06-02", null, null, null, PageRequest.of(0, 10));
        assertEquals(0, result.getTotalElements());
    }
}