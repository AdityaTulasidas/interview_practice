package com.aditya.dataconnect.executionengine.repository;

import com.aditya.dataconnect.executionengine.model.entity.MetaObject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MetaObjectRepository extends JpaRepository<MetaObject, UUID> {
    MetaObject findMetaObjectById(UUID metaObjectId);
    MetaObject findMetaObjectBySystemName(String metaObjectId);
}