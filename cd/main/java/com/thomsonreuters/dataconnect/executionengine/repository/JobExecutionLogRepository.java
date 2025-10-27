package com.thomsonreuters.dataconnect.executionengine.repository;

import com.thomsonreuters.dataconnect.executionengine.model.entity.JobExecutionLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JobExecutionLogRepository extends JpaRepository<JobExecutionLog, UUID> , JpaSpecificationExecutor<JobExecutionLog> {

    @Query("SELECT MAX(j.whenCompleted) FROM JobExecutionLog j WHERE j.status = 'COMPLETED' AND j.jobId = :jobId")
    LocalDateTime findLastCompletedDateByJobId(@Param("jobId") UUID jobId);

    Optional<JobExecutionLog> findByJobId(UUID uuid);
}