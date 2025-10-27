package com.thomsonreuters.dataconnect.dataintegration.repository;

import com.thomsonreuters.dataconnect.dataintegration.model.entity.DataSyncTransformation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.UUID;

@Repository
public interface DataSyncTransformationRepository extends JpaRepository<DataSyncTransformation, UUID> {
    List<DataSyncTransformation> findByDatasyncJobSysName(String datasyncJobSysName);
}