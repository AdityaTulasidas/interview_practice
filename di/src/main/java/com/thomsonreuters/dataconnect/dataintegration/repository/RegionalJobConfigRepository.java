package com.thomsonreuters.dataconnect.dataintegration.repository;

import com.thomsonreuters.dataconnect.dataintegration.model.entity.RegionalJobConfiguration;
import com.thomsonreuters.dataconnect.dataintegration.model.entity.enums.ExecutionLeg;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface RegionalJobConfigRepository extends JpaRepository<RegionalJobConfiguration, UUID> {
    Set<RegionalJobConfiguration> findByDatasyncJobIdAndIsActive(UUID datasyncJobId, boolean isActive);
    Optional<RegionalJobConfiguration> findByDatasyncJobIdAndExecLeg(UUID jobId, ExecutionLeg execLeg);
    Optional<RegionalJobConfiguration> findByDatasyncJobIdAndExecLegAndTargetRegion(UUID jobId, ExecutionLeg execLeg, String targetRegion);

}