package com.thomsonreuters.dataconnect.dataintegration.repository;

import com.thomsonreuters.dataconnect.dataintegration.model.entity.DatasyncJobConfiguration;
import com.thomsonreuters.dataconnect.dataintegration.model.entity.enums.ExecType;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DatasyncJobConfigRepository extends JpaRepository<DatasyncJobConfiguration, UUID>, JpaSpecificationExecutor<DatasyncJobConfiguration> {
   Optional<DatasyncJobConfiguration> findDatasyncJobConfigurationById(UUID id);

   Optional<DatasyncJobConfiguration> findByCustomerTenantSysNameAndSourceTenantIdAndMetaObjectSysNameAndExecType(
           String customerTenantSysName, String sourceTenantId,
           String metaObjectSysName, String execType);

   Optional<DatasyncJobConfiguration> findByCustomerTenantSysNameAndMetaObjectSysNameAndExecType(
           String customerTenantSysName, String metaObjectSysName, String execType);

   Optional<DatasyncJobConfiguration> findBySourceTenantIdAndMetaObjectSysNameAndExecType(
           String sourceTenantId, String metaObjectSysName, String execType);

   Optional<DatasyncJobConfiguration> findByMetaObjectSysNameAndExecType(
           String metaObjectSysName, String execType);

   boolean existsBySystemName(String systemName);
}
