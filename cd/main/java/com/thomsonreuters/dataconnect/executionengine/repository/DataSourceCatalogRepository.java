package com.thomsonreuters.dataconnect.executionengine.repository;


import com.thomsonreuters.dataconnect.executionengine.model.entity.DataSource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface DataSourceCatalogRepository extends JpaRepository<DataSource, UUID> {

}
