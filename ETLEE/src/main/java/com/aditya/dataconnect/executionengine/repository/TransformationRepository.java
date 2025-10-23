package com.aditya.dataconnect.executionengine.repository;


import com.aditya.dataconnect.executionengine.model.entity.TransformationFunction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TransformationRepository extends JpaRepository<TransformationFunction, Integer> {

    Optional<Object> findBySystemName(String name);
}
