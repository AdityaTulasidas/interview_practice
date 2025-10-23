package com.aditya.dataconnect.executionengine.services.job;

import com.aditya.dataconnect.executionengine.constant.Constants;
import com.aditya.dataconnect.executionengine.dto.JobExecutionLogResponseDTO;
import com.aditya.dataconnect.executionengine.dto.JobExecutionLogStatusDTO;
import com.aditya.dataconnect.executionengine.exceptionhandler.DataSyncJobException;
import com.aditya.dataconnect.executionengine.model.entity.JobExecutionLog;
import com.aditya.dataconnect.executionengine.repository.JobExecutionLogRepository;
import com.thomsonreuters.dep.api.spring.ApiCriteria;
import com.thomsonreuters.dep.api.spring.ApiSupport;
import com.thomsonreuters.dep.api.spring.response.ApiCollection;
import com.thomsonreuters.dep.api.spring.response.ApiCollectionFactory;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
@NoArgsConstructor
public class DataSyncJob {

    @Autowired
    private ApiSupport apiSupport;
    @Autowired
    private ApiCollectionFactory apiCollection;
    @Autowired
    private JobExecutionLogRepository jobExecutionLogRepository;
    @Autowired
    private ModelMapper modelMapper;

    public ResponseEntity<?> getAllJobExecutionLogs(int page, int size, String sort, String filter) throws DataSyncJobException {
        try {
            ApiCriteria<JobExecutionLog> criteria = apiSupport.getCriteriaHolder(JobExecutionLog.class);
            final Specification<JobExecutionLog> spec = criteria.getSpecification();
            Page<JobExecutionLog> jobExecutionLogDTOPage = jobExecutionLogRepository.findAll(spec, criteria.getPageable());
            ApiCollection<JobExecutionLogResponseDTO> jobExecutionLogResponseList = apiCollection.from(jobExecutionLogDTOPage)
                    .mapItems(jobExecutionLog -> modelMapper.map(jobExecutionLog, JobExecutionLogResponseDTO.class));
            return ResponseEntity.status(HttpStatus.OK).body(jobExecutionLogResponseList);
        } catch (Exception e) {
            throw new DataSyncJobException("Error retrieving job execution logs", Constants.INTERNAL_SERVER_ERROR);
        }
    }



    public JobExecutionLogStatusDTO getJobExecutionLogStatus(String jobExecutionId) throws DataSyncJobException {
        try {
            JobExecutionLog jobExecutionLog = jobExecutionLogRepository.findById(UUID.fromString(jobExecutionId))
                    .orElseThrow(() -> new DataSyncJobException("Job execution log not found", "NOT_FOUND"));

            return new JobExecutionLogStatusDTO(
                    jobExecutionLog.getJobId(),
                    jobExecutionLog.getWhenAccepted(),
                    jobExecutionLog.getWhenStarted(),
                    jobExecutionLog.getWhenCompleted(),
                    jobExecutionLog.getStatus()
            );
        } catch (DataSyncJobException e) {
            throw new DataSyncJobException(e.getMessage(), e.getCode());
        } catch (IllegalArgumentException e) {
            log.error("Error retrieving job execution log status for ID: {}", jobExecutionId, e);
            throw new DataSyncJobException("Error retrieving job execution log status", Constants.INTERNAL_SERVER_ERROR);
        } catch (NoSuchElementException e) {
            log.error("Job execution log not found for ID: {}", jobExecutionId, e);
            throw new DataSyncJobException("Job execution log not found", "NOT_FOUND");

        }
    }

}
