package com.aditya.dataconnect.executionengine.repository;

import com.aditya.dataconnect.executionengine.model.entity.TransitHubConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TransitHubConfigRepository extends JpaRepository<TransitHubConfig, Integer> {
    Optional<TransitHubConfig> findByMetaObjectSysName(String name);
}
