package com.thomsonreuters.dataconnect.dataintegration.repository;

import com.thomsonreuters.dataconnect.dataintegration.model.entity.DataSyncActivityConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DatasyncActivityRepository extends JpaRepository<DataSyncActivityConfig, UUID> {
    List<DataSyncActivityConfig> findByDatasyncJobSysName(String  jobName);
}
