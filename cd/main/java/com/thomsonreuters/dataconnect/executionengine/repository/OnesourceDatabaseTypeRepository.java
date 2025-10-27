package com.thomsonreuters.dataconnect.executionengine.repository;

import com.thomsonreuters.dataconnect.executionengine.model.entity.OnesourceDatabaseType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OnesourceDatabaseTypeRepository extends JpaRepository<OnesourceDatabaseType, String> {
    // You can define custom query methods here if needed
}
