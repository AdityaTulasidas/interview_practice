package com.thomsonreuters.dataconnect.executionengine.repository;

import com.thomsonreuters.dataconnect.executionengine.model.entity.RegionalJobConfiguration;
import com.thomsonreuters.dataconnect.executionengine.model.entity.enums.ExecutionLeg;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RegionalJobConfigRepository extends JpaRepository<RegionalJobConfiguration, UUID> {


    Optional<RegionalJobConfiguration> findByDatasyncJobSysNameAndSourceRegionAndExecLegAndIsActive(String jobName, String execRegion, ExecutionLeg executionLeg, boolean isActive);
    Optional<RegionalJobConfiguration> findByDatasyncJobSysNameAndTargetRegionAndExecLegAndIsActive(String jobName, String execRegion, ExecutionLeg executionLeg, boolean isActive);
}