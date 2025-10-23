package com.thomsonreuters.metadataregistry.repository;

import com.thomsonreuters.metadataregistry.model.entity.DomainObject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DomainObjectRepository extends JpaRepository<DomainObject,Integer> {
    boolean existsBySystemName(String name);
}
