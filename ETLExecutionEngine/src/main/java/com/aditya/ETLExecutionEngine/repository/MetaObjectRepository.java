package com.aditya.ETLExecutionEngine.repository;

import com.aditya.ETLExecutionEngine.model.entity.MetaObject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MetaObjectRepository extends JpaRepository<MetaObject, UUID> {
}

