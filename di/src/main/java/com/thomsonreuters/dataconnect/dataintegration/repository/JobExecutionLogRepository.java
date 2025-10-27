package com.thomsonreuters.dataconnect.dataintegration.repository;

import com.thomsonreuters.dataconnect.dataintegration.model.entity.JobExecutionLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface JobExecutionLogRepository extends JpaRepository<JobExecutionLog, UUID> {

    Optional<JobExecutionLog> findTopByJobIdOrderByWhenAcceptedDesc(UUID jobId);

    Page<JobExecutionLog> findAll(Specification<JobExecutionLog> spec, Pageable pageable);
}