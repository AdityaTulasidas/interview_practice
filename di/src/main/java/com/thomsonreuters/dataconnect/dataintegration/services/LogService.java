package com.thomsonreuters.dataconnect.dataintegration.services;

import com.thomsonreuters.dataconnect.dataintegration.configuration.ModelMapperConfig;
import com.thomsonreuters.dataconnect.dataintegration.constant.Constants;
import com.thomsonreuters.dataconnect.dataintegration.dto.ActivityLogDTO;
import com.thomsonreuters.dataconnect.dataintegration.dto.JobExecutionLogSearchDTO;
import com.thomsonreuters.dataconnect.dataintegration.exceptionhandler.DataSyncJobException;
import com.thomsonreuters.dataconnect.dataintegration.model.entity.ActivityLog;
import com.thomsonreuters.dataconnect.dataintegration.model.entity.JobExecutionLog;
import com.thomsonreuters.dataconnect.dataintegration.repository.ActivityLogRepository;
import com.thomsonreuters.dataconnect.dataintegration.repository.JobExecutionLogRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class LogService {

    private final ActivityLogRepository activityLogRepository;

    private final JobExecutionLogRepository jobExecutionLogRepository;

    private final ModelMapperConfig modelMapperConfig;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    public LogService(ActivityLogRepository activityLogRepository, ModelMapperConfig modelMapperConfig,JobExecutionLogRepository jobExecutionLogRepository) {
        this.activityLogRepository = activityLogRepository;
        this.modelMapperConfig = modelMapperConfig;
        this.jobExecutionLogRepository = jobExecutionLogRepository;
    }

    public Page<ActivityLogDTO> getActivityLogs(String execId, String from, String to, String domain, String onesourceJobName, String customerTenant, Pageable pageable) throws DataSyncJobException {
        // Validate input parameters
        if (execId != null) {
            validateExecId(execId);
        }
        // Build specifications for filtering
        Specification<ActivityLog> spec = Specification.where(null);
        LocalDateTime fromDate = (from != null ? parseFlexibleDateTime(from, false) : null);
        LocalDateTime toDate = (to != null ? parseFlexibleDateTime(to, true) : null);
        if (execId != null) {
            spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("jobExecutionId"), UUID.fromString(execId)));
        }
        if (from != null) {
            spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), fromDate));
        }
        if (to != null) {
            spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), toDate));
        }
        // Fetch logs from repository
        Page<ActivityLog> logs = activityLogRepository.findAll(spec, pageable);
        // Map entities to DTOs
        return logs.map(log -> modelMapperConfig.modelMapper().map(log, ActivityLogDTO.class));
    }

    private void validateExecId(String execId) throws DataSyncJobException {
        // Validate that the exec_id exists in the source region
        List<ActivityLog> logs = activityLogRepository.findByJobExecutionId(UUID.fromString(execId));
        if (logs.isEmpty()) {
            throw new DataSyncJobException("Invalid execution id : " + execId, Constants.BAD_REQUEST);
        }
    }

    public Page<JobExecutionLogSearchDTO> getJobExecutionLogs(String from, String to, String domain, String onesourceJobName, String customerTenant, Pageable pageable) throws DataSyncJobException {
        // Build specifications for filtering
        Specification<JobExecutionLog> spec = Specification.where(null);
        LocalDateTime fromDate = (from != null ? parseFlexibleDateTime(from, false) : null);
        LocalDateTime toDate = (to != null ? parseFlexibleDateTime(to, true) : null);
        if (from != null) {
            spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), fromDate));
        }
        if (to != null) {
            spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), toDate));
        }
        if (domain != null) {
            spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("domain"), domain));
        }
        if (onesourceJobName != null) {
            spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("regionalJobSysName"), onesourceJobName));
        }
        if (customerTenant != null) {
            spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("customerId"), customerTenant));
        }
        // Fetch logs from repository
        Page<JobExecutionLog> logs = jobExecutionLogRepository.findAll(spec, pageable);
        // Map entities to DTOs
        return logs.map(log -> modelMapperConfig.modelMapper().map(log, JobExecutionLogSearchDTO.class));
    }

    public LocalDateTime parseFlexibleDateTime(String input, boolean isToDate) throws DataSyncJobException {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        try {
            return LocalDateTime.parse(input, dateTimeFormatter);
        } catch (DateTimeParseException e1) {
            try {
                String time = isToDate ? "23:59:00" : "00:00:00";
                return LocalDateTime.parse(input + " " + time, dateTimeFormatter);
            } catch (DateTimeParseException e2) {
                throw new DataSyncJobException("Invalid date format.", Constants.BAD_REQUEST);
            }
        }
    }
}