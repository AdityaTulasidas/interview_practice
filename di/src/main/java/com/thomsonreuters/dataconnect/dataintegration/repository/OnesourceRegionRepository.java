package com.thomsonreuters.dataconnect.dataintegration.repository;

import com.thomsonreuters.dataconnect.dataintegration.model.entity.OnesourceRegion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OnesourceRegionRepository extends JpaRepository<OnesourceRegion, Integer> {
    Optional<OnesourceRegion> findBySystemName(String region);
}
