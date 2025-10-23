package com.thomsonreuters.dataconnect.executionengine.repository;

import com.thomsonreuters.dataconnect.executionengine.model.entity.DataSyncActivityConfig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DataSyncActivityRepository extends JpaRepository<DataSyncActivityConfig, UUID> {

    public List<DataSyncActivityConfig> findByDatasyncJobSysNameAndExecType(String dataSyncJobName, String execType);
}
