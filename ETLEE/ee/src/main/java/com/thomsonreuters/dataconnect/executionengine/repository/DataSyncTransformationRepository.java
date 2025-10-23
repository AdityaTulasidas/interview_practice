package com.thomsonreuters.dataconnect.executionengine.repository;

import com.thomsonreuters.dataconnect.executionengine.model.entity.DataSyncTransformation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DataSyncTransformationRepository extends JpaRepository<DataSyncTransformation, UUID> {
}
