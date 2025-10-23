package com.thomsonreuters.dataconnect.executionengine.repository;

import com.thomsonreuters.dataconnect.executionengine.model.entity.ActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActivityLogRepository extends JpaRepository<ActivityLog, String> {
}
