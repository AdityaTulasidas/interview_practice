package com.thomsonreuters.metadataregistry.repository;

import com.thomsonreuters.metadataregistry.model.entity.Domain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface DomainRepository extends JpaRepository<Domain,Integer> {
    boolean existsBySystemName(String oneSourceDomain);
}

