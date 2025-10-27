package com.thomsonreuters.dataconnect.dataintegration.repository;


import com.thomsonreuters.dataconnect.dataintegration.model.entity.TransformationFunction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransformationRepository extends JpaRepository<TransformationFunction, Integer> {

    Optional<TransformationFunction> findBySystemName(String name);
    List<TransformationFunction> findByType(String transformType);
}