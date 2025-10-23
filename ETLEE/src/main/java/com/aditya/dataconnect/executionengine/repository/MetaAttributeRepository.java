package com.aditya.dataconnect.executionengine.repository;

import com.aditya.dataconnect.executionengine.model.entity.MetaObject;
import com.aditya.dataconnect.executionengine.model.entity.MetaObjectAttribute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MetaAttributeRepository extends JpaRepository<MetaObjectAttribute, UUID> {
    MetaObjectAttribute findByMetaObject(MetaObject metaObject);
}