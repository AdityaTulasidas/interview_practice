package com.thomsonreuters.metadataregistry.repository;

import com.thomsonreuters.metadataregistry.model.entity.DataSource;

import com.thomsonreuters.metadataregistry.model.entity.enums.SystemName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DataSourceCatalogRepository extends JpaRepository<DataSource, UUID>, JpaSpecificationExecutor<DataSource> {

    @Query("SELECT d FROM DataSource d WHERE d.regionalTenantId = ?1 AND d.onesourceRegion = ?2 AND d.domain = ?3 AND d.domainObjectSysName = ?4")
    Optional<Object> findByRegionalTenantRegionDomainAndSysName(String regionalTenantId, String onesourceRegion, String domain, SystemName sysName);

    @Query(value = """
    SELECT * FROM onesource_data_source WHERE COALESCE(regional_tenant_id, '__NULL__') = :normalizedTenantId AND onesource_region = :region AND domain = :domain AND domain_object_sys_name = :sysName""", nativeQuery = true)
    Optional<DataSource> findByNormalizedTenantRegionDomainAndSysName(@Param("normalizedTenantId") String normalizedTenantId, @Param("region") String region, @Param("domain") String domain, @Param("sysName") String sysName);

    @Query("SELECT d FROM DataSource d WHERE " +
            "(:regionalTenantId IS NULL OR d.regionalTenantId = :regionalTenantId) AND " +
            "(:domain IS NULL OR d.domain = :domain) AND " +
            "(:onesourceRegion IS NULL OR d.onesourceRegion = :onesourceRegion) AND " +
            "(:domainObjectSysName IS NULL OR d.domainObjectSysName = :domainObjectSysName)")
    List<DataSource> findFiltered(@Param("regionalTenantId") String regionalTenantId,
                                  @Param("domain") String domain,
                                  @Param("onesourceRegion") String onesourceRegion,
                                  @Param("domainObjectSysName") String domainObjectSysName);

}
