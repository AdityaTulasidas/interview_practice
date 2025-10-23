package com.thomsonreuters.metadataregistry.service;


import com.thomsonreuters.dataconnect.dataintegration.model.entity.enums.OperationType;
import com.thomsonreuters.dataconnect.dataintegration.services.job.DataConnectClientService;
import com.thomsonreuters.dep.api.spring.ApiCriteria;
import com.thomsonreuters.dep.api.spring.ApiSupport;
import com.thomsonreuters.dep.api.spring.response.ApiCollection;
import com.thomsonreuters.dep.api.spring.response.ApiCollectionFactory;
import com.thomsonreuters.metadataregistry.configuration.ModelMapperConfig;
import com.thomsonreuters.metadataregistry.constants.Constants;
import com.thomsonreuters.metadataregistry.exceptionhandler.MetaDataRegistryException;
import com.thomsonreuters.metadataregistry.model.dto.DataSourceDTO;
import com.thomsonreuters.metadataregistry.model.dto.DataSourceUpdateDTO;
import com.thomsonreuters.metadataregistry.model.entity.DataSource;
import com.thomsonreuters.metadataregistry.repository.DataSourceCatalogRepository;
import com.thomsonreuters.metadataregistry.repository.DomainRepository;
import com.thomsonreuters.metadataregistry.repository.MetaObjectRepository;
import com.thomsonreuters.metadataregistry.repository.OnesourceRegionRepository;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import com.thomsonreuters.metadataregistry.repository.*;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.xml.crypto.Data;
import java.time.LocalDateTime;
import java.util.*;

import static com.thomsonreuters.metadataregistry.constants.Constants.*;
import static java.lang.Boolean.FALSE;
import static com.thomsonreuters.metadataregistry.constants.Constants.INVALID_REQUEST;
import static com.thomsonreuters.metadataregistry.constants.Constants.USER_NAME;
import static java.lang.Character.toLowerCase;


@Service
public class DataSourceCatalogService {

    public static final Logger log = org.slf4j.LoggerFactory.getLogger(DataSourceCatalogService.class);


    private final DataSourceCatalogRepository dataSourceCatalogRepository;


    private final ModelMapperConfig modelMapperConfig;


    private final ApiSupport apiSupport;


    private final ApiCollectionFactory apiCollection;

    private final DataConnectClientService dataConnectClientService;

    private final MetaObjectRepository metaObjectRepository;

    private final DomainRepository domainRepository;

    private final DomainObjectRepository domainObjectRepository;

    private final OnesourceRegionRepository onesourceRegionRepository;

    private final OnesourceDatabaseTypeRepository onesourceDatabaseTypeRepository;



    @Autowired
    public DataSourceCatalogService(DataSourceCatalogRepository dataSourceCatalogRepository, ModelMapperConfig modelMapperConfig, ApiCollectionFactory apiCollection, ApiSupport apiSupport, DataConnectClientService dataConnectClientService, MetaObjectRepository metaObjectRepository, OnesourceDatabaseTypeRepository onesourceDatabaseTypeRepository, DomainRepository domainRepository, DomainObjectRepository domainObjectRepository, OnesourceRegionRepository onesourceRegionRepository) {
        this.dataSourceCatalogRepository = dataSourceCatalogRepository;
        this.modelMapperConfig = modelMapperConfig;
        this.apiCollection = apiCollection;
        this.apiSupport = apiSupport;
        this.dataConnectClientService = dataConnectClientService;
        this.metaObjectRepository = metaObjectRepository;
        this.domainRepository = domainRepository;
        this.domainObjectRepository = domainObjectRepository;
        this.onesourceRegionRepository = onesourceRegionRepository;
        this.onesourceDatabaseTypeRepository = onesourceDatabaseTypeRepository;

    }

    public DataSource saveDataSource(DataSourceDTO dto) throws MetaDataRegistryException {
        String normalizedTenantId = normalizeTenantId(dto.getRegionalTenantId());
        dataSourceCatalogRepository.findByNormalizedTenantRegionDomainAndSysName(normalizedTenantId, dto.getOnesourceRegion().toString(), dto.getDomain().toString(), dto.getDomainObjectSysName())
                .ifPresent(existingDataSource -> {
                    throw new MetaDataRegistryException(Constants.DATA_SOURCE_ALREADY_EXISTS, Constants.CONFLICT);
                });
        validateOneSourceDomain(dto.getDomain());
        validateRegion(dto.getOnesourceRegion());
        if (dto.getDomainObjectSysName() != null) {
            validateDomainObject(dto.getDomainObjectSysName());
        }
        //validate db_type
        validateDbType(dto.getDbType());
        String systemName = buildSystemName(dto.getOnesourceRegion(), dto.getDomain(),dto.getDomainObjectSysName(),dto.getRegionalTenantId()).toLowerCase();
        DataSource dataSourceEntries = modelMapperConfig.modelMapper().map(dto, DataSource.class);
        dataSourceEntries.setCreatedBy(USER_NAME);
        dataSourceEntries.setCreatedAt(LocalDateTime.now());
        dataSourceEntries.setUpdatedAt(LocalDateTime.now());
        dataSourceEntries.setUpdatedBy(USER_NAME);
        dataSourceEntries.setSystemName(systemName);
        DataSource dataSource=dataSourceCatalogRepository.save(dataSourceEntries);
        syncDataAcrossRegions(dataSource,"CREATE");
        return dataSource;
    }

    public String buildSystemName(String region, String domain, String domainObjectSysName, String regionalTenantId) {
        StringBuilder systemName = new StringBuilder(region).append(".");

        if (domainObjectSysName != null && !domainObjectSysName.isEmpty()) {
            systemName.append(domainObjectSysName);
        } else {
            systemName.append(domain);
        }

        if (regionalTenantId != null && !regionalTenantId.isEmpty()) {
            systemName.append(".").append(regionalTenantId);
        }

        return systemName.toString();
    }





    public Optional<DataSourceDTO> getDataSourceById(UUID id) {
        Optional<DataSource> dataSourceCatalog = dataSourceCatalogRepository.findById(id);
        return Optional.ofNullable(modelMapperConfig.modelMapper().map(dataSourceCatalog, DataSourceDTO.class));
    }


    public List<DataSourceDTO> getAllDataSources() {
        List<DataSource> dataSources = dataSourceCatalogRepository.findAll();
        if (dataSources.isEmpty()) {
            return Collections.emptyList();
        }
        return dataSources.stream()
                .map(dataSource -> modelMapperConfig.modelMapper().map(dataSource, DataSourceDTO.class))
                .toList();
    }


    public Optional<DataSourceUpdateDTO> updateDataSource(UUID id, DataSourceUpdateDTO dataSourceDTO) {
        // Validate if the payload is null
        if (dataSourceDTO == null) {
            throw new MetaDataRegistryException(Constants.BAD_REQUEST, "Payload cannot be null");
        }

        // Fetch the existing data source by ID
        Optional<DataSource> existingDataSourceOpt = dataSourceCatalogRepository.findById(id);
        if (existingDataSourceOpt.isEmpty()) {
            throw new MetaDataRegistryException(Constants.DATA_SOURCE_NOT_FOUND, Constants.NOT_FOUND);
        }

        DataSource existingDataSource = existingDataSourceOpt.get();
        existingDataSource.setDisplayName(dataSourceDTO.getDisplayName());
        existingDataSource.setDescription(dataSourceDTO.getDescription());
        existingDataSource.setUserName(dataSourceDTO.getUserName());
        existingDataSource.setPassword(dataSourceDTO.getPassword());
        existingDataSource.setUpdatedBy(USER_NAME);
        existingDataSource.setUpdatedAt(LocalDateTime.now());
        // Save the updated data source
        DataSource savedDataSource;
        try {
            savedDataSource = dataSourceCatalogRepository.save(existingDataSource);
        } catch (org.springframework.dao.DataIntegrityViolationException ex) {
            if (ex.getMessage() != null && ex.getMessage().contains("duplicate key value violates unique constraint")) {
                throw new MetaDataRegistryException(Constants.ERROR_UPDATING_DATA_SOURCE, Constants.CONFLICT); // 409
            }
            throw ex;
        }
        syncDataAcrossRegions(savedDataSource,"UPDATE");
        // Map the saved entity back to DTO and return
        return Optional.ofNullable(modelMapperConfig.modelMapper().map(savedDataSource, DataSourceUpdateDTO.class));
    }

    public ResponseEntity<?> dataSourceSearch(int page, int size, String sort, String filter) {
        try {
            ApiCriteria<DataSource> criteria = apiSupport.getCriteriaHolder(DataSource.class);
            final Specification<DataSource> spec = criteria.getSpecification();
            Page<DataSource> dataSourceDTOPage = dataSourceCatalogRepository.findAll(spec, criteria.getPageable());
            ApiCollection<DataSource> dataSourceResponseList = apiCollection.from(dataSourceDTOPage).mapItems(dataSourceDTO -> {
                return dataSourceDTO;
            });
            return ResponseEntity.status(HttpStatus.OK).body(dataSourceResponseList);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MetaDataRegistryException("Failed to fetch DataSources", "INTERNAL_SERVER_ERROR"));
        }
    }

    public void syncDataAcrossRegions(DataSource dataSource,String operationType) {
        try {
            log.info("Syncing created DataSource with ID to emea: {}", dataSource.getId());
            List<String> ids = new ArrayList<>();
            ids.add(dataSource.getId().toString());
            dataConnectClientService.sendDataChanges(
                    OperationType.valueOf(operationType),
                    DATA_SOURCE_SYS_NAME,
                    "",
                    "",
                    ids,
                    "",
                    LocalDateTime.now(),
                    FALSE
            );
            log.info("Successfully synced created DataSource with ID to emea: {}", dataSource.getId());
        } catch (Exception e) {
            log.error("Error syncing created DataSource with ID to emea: {}", e.getMessage());
        }
    }

    private String normalizeTenantId(String tenantId) {
        return tenantId == null ? "__NULL__" : tenantId;
    }

    private void validateDbType(String dbType) {
        if (!onesourceDatabaseTypeRepository.existsById(dbType.trim())) {
            throw new MetaDataRegistryException(Constants.DATABASE_TYPE_INVALID, Constants.INVALID_REQUEST);
        }
    }



    public DataSource getFilteredDataSource(String regionalTenantId, String domain,
                                            String onesourceRegion, String domainObjectSysName) {
        // Step 1: Try with all 4 parameters
        DataSource result = query(regionalTenantId, domain, onesourceRegion, domainObjectSysName);
        if (result != null) {
            return result;
        }

        // Step 2: Try with regionalTenantId = null
        regionalTenantId = null;
        result = query(regionalTenantId, domain, onesourceRegion, domainObjectSysName);
        if (result != null) {
            return result;
        }

        // Step 3: Try with regionalTenantId = null and domainObjectSysName = null
        domainObjectSysName = null;
        result = query(regionalTenantId, domain, onesourceRegion, domainObjectSysName);
        return result;

    }

    private DataSource query(Object regionalTenantId, Object domain, Object onesourceRegion, Object domainObjectSysName) {
        List<DataSource> result = dataSourceCatalogRepository.findFiltered(
                (String) regionalTenantId, (String) domain, (String) onesourceRegion, (String) domainObjectSysName);
        return result.isEmpty() ? null : result.get(0);
    }

    public void trimDataSource(DataSourceDTO dto) {
        if (dto.getDisplayName() != null) {
            dto.setDisplayName(dto.getDisplayName().trim());
        }
        if (dto.getSystemName() != null) {
            dto.setSystemName(dto.getSystemName().trim());
        }
        if (dto.getDescription() != null) {
            dto.setDescription(dto.getDescription().trim());
        }
        if (dto.getDbType() != null) {
            dto.setDbType(dto.getDbType().trim());
        }
        if (dto.getDomain() != null) {
            dto.setDomain(dto.getDomain().trim());
        }
        if (dto.getRegionalTenantId() != null) {
            dto.setRegionalTenantId(dto.getRegionalTenantId().trim());
        }
        if (dto.getOnesourceRegion() != null) {
            dto.setOnesourceRegion(dto.getOnesourceRegion().trim());
        }
        if (dto.getUserName() != null) {
            dto.setUserName(dto.getUserName().trim());
        }
        if (dto.getPassword() != null) {
            dto.setPassword(dto.getPassword().trim());
        }
        if (dto.getDomainObjectSysName() != null) {
            dto.setDomainObjectSysName(dto.getDomainObjectSysName().trim());
        }
        if (dto.getHost() != null) {
            dto.setHost(dto.getHost().trim());
        }
        if(dto.getDb() != null) {
            dto.setDb(dto.getDb().trim());
        }
        if (dto.getPort() != null) {
            dto.setPort(dto.getPort().trim());
        }
    }


    public void trimDataSource(DataSourceUpdateDTO dto) {
        if (dto.getDisplayName() != null) {
            dto.setDisplayName(dto.getDisplayName().trim());
        }

        if (dto.getDescription() != null) {
            dto.setDescription(dto.getDescription().trim());
        }

        if (dto.getUserName() != null) {
            dto.setUserName(dto.getUserName().trim());
        }
        if (dto.getPassword() != null) {
            dto.setPassword(dto.getPassword().trim());
        }

    }

    private void validateDomainObject(@NotNull @NotBlank @NotEmpty String domainObject) throws MetaDataRegistryException{
        if(!domainObjectRepository.existsBySystemName(domainObject)){
            throw new MetaDataRegistryException("Invalid domain_object " + domainObject, INVALID_REQUEST);
        }
    }

    private void validateOneSourceDomain(String oneSourceDomain) throws MetaDataRegistryException{
        if(!domainRepository.existsBySystemName(oneSourceDomain.trim()))
            throw new MetaDataRegistryException("Invalid onesource_domain " + oneSourceDomain, INVALID_REQUEST);
    }

    private void validateRegion(String region) throws MetaDataRegistryException{
        if(onesourceRegionRepository.findBySystemName(region).isEmpty())
            throw new MetaDataRegistryException("Invalid REGION " + region, INVALID_REQUEST);
    }

}


