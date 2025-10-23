package com.aditya.dataconnect.executionengine.repository;

import com.aditya.dataconnect.executionengine.model.entity.DatasyncJobConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DatasyncJobConfigRepository extends JpaRepository<DatasyncJobConfiguration, UUID> {
}