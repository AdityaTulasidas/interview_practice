package com.aditya.dataconnect.executionengine.repository;

import com.aditya.dataconnect.executionengine.model.entity.TransformationFunctionParam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransformParamRepository extends JpaRepository<TransformationFunctionParam,Integer> {


    List<TransformationFunctionParam> findByTransformFuncId(String id);
}
