package com.thomsonreuters.metadataregistry.repository;

import com.thomsonreuters.metadataregistry.model.entity.MetaObject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MetaObjectRepository extends JpaRepository<MetaObject, UUID>, JpaSpecificationExecutor<MetaObject> {


    Optional<MetaObject> findByDbTable(String name);
}