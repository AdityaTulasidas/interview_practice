package com.thomsonreuters.dataconnect.dataintegration.repository;

import com.thomsonreuters.dataconnect.dataintegration.model.entity.TransformationFunctionParam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransformParamRepository extends JpaRepository<TransformationFunctionParam,Integer> {


    List<TransformationFunctionParam> findByTransformFuncId(String id);

    @Query("SELECT tfp FROM TransformationFunctionParam tfp WHERE tfp.systemName = ?1")
    List<TransformationFunctionParam> findBySystemName(String funcName);

}