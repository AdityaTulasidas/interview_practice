package com.thomsonreuters.metadataregistry.repository;

import com.thomsonreuters.metadataregistry.model.entity.MetaObjectAttribute;
import com.thomsonreuters.metadataregistry.model.entity.MetaObject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MetaAttributeRepository extends JpaRepository<MetaObjectAttribute, UUID> {
    List<MetaObjectAttribute> findByMetaObject(MetaObject metaObject);
}