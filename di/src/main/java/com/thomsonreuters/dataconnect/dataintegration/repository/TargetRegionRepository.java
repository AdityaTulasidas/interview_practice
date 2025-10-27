package com.thomsonreuters.dataconnect.dataintegration.repository;

import com.thomsonreuters.dataconnect.dataintegration.model.entity.TargetRegion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TargetRegionRepository extends JpaRepository<TargetRegion, UUID> {

    List<TargetRegion> findByDatasyncJobSysName(String datasyncJobSysName);

    boolean existsByDatasyncJobSysNameAndTargetRegion(String datasyncJobSysName, String targetRegion);

    void deleteByDatasyncJobSysNameAndTargetRegion(String datasyncJobSysName, String targetRegion);
}
