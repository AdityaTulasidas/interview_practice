package com.thomsonreuters.metadataregistry.service;

import java.time.LocalDateTime;


import com.thomsonreuters.dataconnect.dataintegration.exceptionhandler.DataConnectClientException;
import com.thomsonreuters.dataconnect.dataintegration.model.entity.enums.ExecType;
import com.thomsonreuters.dataconnect.dataintegration.model.entity.enums.OperationType;
import com.thomsonreuters.dataconnect.dataintegration.services.job.DataConnectClientService;
import com.thomsonreuters.dep.api.spring.ApiCriteria;
import com.thomsonreuters.dep.api.spring.ApiSupport;
import com.thomsonreuters.dep.api.spring.response.ApiCollection;
import com.thomsonreuters.dep.api.spring.response.ApiCollectionFactory;
import com.thomsonreuters.dep.api.spring.response.ApiMeta;
import com.thomsonreuters.metadataregistry.configuration.ModelMapperConfig;
import com.thomsonreuters.metadataregistry.exceptionhandler.MetaDataRegistryException;
import com.thomsonreuters.metadataregistry.model.dto.DataSourceDTO;
import com.thomsonreuters.metadataregistry.model.dto.DataSourceUpdateDTO;
import com.thomsonreuters.metadataregistry.model.entity.DataSource;
import com.thomsonreuters.metadataregistry.model.entity.MetaObject;
import com.thomsonreuters.metadataregistry.model.entity.OnesourceRegion;
import com.thomsonreuters.metadataregistry.model.entity.enums.*;
import com.thomsonreuters.metadataregistry.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DataSourceCatalogServiceTest {

    @Mock
    private ApiSupport apiSupport;

    @Mock
    private ApiCriteria<DataSourceDTO> apiCriteria;

    @Mock
    private ApiCollectionFactory apiCollection;

    @Mock
    private Specification<DataSourceDTO> specification;

    @Mock
    private DataSourceCatalogRepository dataSourceCatalogRepository;

    @Mock
    private ModelMapperConfig modelMapperConfig;

    @Mock
    private DataConnectClientService dataConnectClientService;

    @Mock
    private MetaObjectRepository metaObjectRepository;

    @Mock
    private DomainRepository domainRepository;

    @Mock
    private OnesourceRegionRepository onesourceRegionRepository;

    @Mock
    private OnesourceDatabaseTypeRepository onesourceDatabaseTypeRepository;

    @Mock
    private DomainObjectRepository domainObjectRepository;

    @InjectMocks
    private DataSourceCatalogService dataSourceCatalogService;

    private ModelMapper modelMapper = new ModelMapper();

    private DataSourceDTO dataSourceDTO;
    private DataSource dataSource;

    private DataSourceUpdateDTO dataSourceUpdateDTO;




    @BeforeEach
    public void setUp() {
        dataSourceDTO = new DataSourceDTO();
        dataSourceDTO.setId(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"));
        dataSourceDTO.setSystemName("TestStore");
        dataSourceDTO.setCustomerTenantId("TestCustomer");
        dataSourceDTO.setDbType("MYSQL");
        dataSourceDTO.setOnesourceRegion("AMER");
        dataSourceDTO.setHost("/a204896/host/DEV");
        dataSourceDTO.setDb("/a204896/dbname/DEV");
        dataSourceDTO.setPort("/a204896/port/DEV");
        dataSourceDTO.setDomain("BF_AR");
        dataSourceDTO.setRegionalTenantId("TestRegion");
        dataSourceDTO.setDescription("Test Description");
        dataSourceDTO.setUserName("TestUser");
        dataSourceDTO.setPassword("TestPassword");
        dataSourceDTO.setDomainObjectSysName("CD_MAST_COA");

        dataSource = new DataSource();
        dataSource.setId(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"));
        dataSource.setSystemName("TestStore");
        dataSource.setCustomerTenantId("TestCustomer");
        dataSource.setDbType("MYSQL");
        dataSource.setOnesourceRegion("AMER");
        dataSource.setHost("/a204896/host/DEV");
        dataSource.setDb("/a204896/dbname/DEV");
        dataSource.setPort("/a204896/port/DEV");
        dataSource.setDomain("BF_AR");
        dataSource.setRegionalTenantId("TestRegion");
        dataSource.setDescription("Test Description");

        dataSourceUpdateDTO = new DataSourceUpdateDTO();

        dataSourceUpdateDTO.setDescription("Test Description");
        dataSourceUpdateDTO.setUserName("TestUser");
        dataSourceUpdateDTO.setPassword("TestPassword");


        // Ensure the modelMapperConfig mock returns a ModelMapper instance
        when(modelMapperConfig.modelMapper()).thenReturn(modelMapper);
    }

    @Test
    void ShouldSaveDataSource_WhenDataISValid() {
        when(dataSourceCatalogRepository.save(any(DataSource.class))).thenReturn(dataSource);
        when(domainRepository.existsBySystemName(anyString())).thenReturn(true);
        when(onesourceRegionRepository.findBySystemName(anyString())).thenReturn(Optional.of(new OnesourceRegion()));
        when(onesourceDatabaseTypeRepository.existsById(anyString())).thenReturn(true);
        when(domainRepository.existsBySystemName(anyString())).thenReturn(true);
        when(domainObjectRepository.existsBySystemName(anyString())).thenReturn(true);
        DataSource result = dataSourceCatalogService.saveDataSource(dataSourceDTO);
        assertNotNull(result);
        assertEquals("TestStore", result.getSystemName());
        verify(dataSourceCatalogRepository, times(1)).save(any(DataSource.class));
    }

    @Test
    void shouldGetDataSourceById_WhenIdiIsValid() {
        when(dataSourceCatalogRepository.findById(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"))).thenReturn(Optional.of(dataSource));
        Optional<DataSourceDTO> result = dataSourceCatalogService.getDataSourceById(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"));
        assertTrue(result.isPresent());
        assertEquals("TestStore", result.get().getSystemName());
        verify(dataSourceCatalogRepository, times(1)).findById(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"));
    }

    @Test
    void shouldUpdateDataSource_WhenDataIsValid() {
        when(dataSourceCatalogRepository.findById(UUID.fromString("132e4567-e89b-12d3-a456-426614174000"))).thenReturn(Optional.of(dataSource));
        when(dataSourceCatalogRepository.save(any(DataSource.class))).thenReturn(dataSource);
        when(domainRepository.existsBySystemName("BF_AR")).thenReturn(true);
        when(onesourceRegionRepository.findBySystemName("AMER")).thenReturn(Optional.of(new OnesourceRegion()));
        when(onesourceDatabaseTypeRepository.existsById(anyString())).thenReturn(true);
        when(onesourceRegionRepository.findBySystemName(anyString())).thenReturn(Optional.of(new OnesourceRegion()));
        when(domainRepository.existsBySystemName(anyString())).thenReturn(true);
        Optional<DataSourceUpdateDTO> result = dataSourceCatalogService.updateDataSource(UUID.fromString("132e4567-e89b-12d3-a456-426614174000"), dataSourceUpdateDTO);
        assertNotNull(result);

        verify(dataSourceCatalogRepository, times(1)).findById(UUID.fromString("132e4567-e89b-12d3-a456-426614174000"));
        verify(dataSourceCatalogRepository, times(1)).save(any(DataSource.class));
    }

    @Test
    void testGetAllDataSources_whenDataIsValid() {
        when(dataSourceCatalogRepository.findAll()).thenReturn(Arrays.asList(dataSource));
        List<DataSourceDTO> result = dataSourceCatalogService.getAllDataSources();
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("TestStore", result.get(0).getSystemName());
        verify(dataSourceCatalogRepository, times(1)).findAll();
    }

    @Test
    void dataSourceSearch_shouldReturnInternalServerError_whenExceptionThrown() {

        ResponseEntity<?> response = dataSourceCatalogService.dataSourceSearch(0, 10, null, null);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody() instanceof MetaDataRegistryException);
    }


    @Test
    void getAllDataSources_returnSuccess() {
        DataSourceDTO job1Dto = new DataSourceDTO();
        List<DataSourceDTO> jobList = List.of(job1Dto);
        Page<DataSourceDTO> jobsPage = new PageImpl<>(jobList);
        when(apiSupport.getCriteriaHolder(any(Class.class))).thenReturn(apiCriteria);
        when(apiCriteria.getSpecification()).thenReturn(specification);

        Pageable pageable = PageRequest.of(0, 10);
        when(apiCriteria.getPageable()).thenReturn(pageable);
        when(dataSourceCatalogRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(jobsPage);
        ApiCollection<DataSourceDTO> apiCollectionMock = spy(new ApiCollection<>(jobList, new ApiMeta()));
        when(apiCollection.from(any(Page.class))).thenReturn(apiCollectionMock);
        //  mock it to return the same mock
        doReturn(apiCollectionMock).when(apiCollectionMock).mapItems(any());

        // Call the method
        ResponseEntity<?> response = dataSourceCatalogService.dataSourceSearch(0, 10, null, null);
        // Assertions
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
    }
    @Test
    void testSyncDataAcrossRegions() throws DataConnectClientException {
        // Mock MetaObjectRepository behavior
        MetaObject metaObject = new MetaObject();
        metaObject.setDbTable("public.onesource_data_source");
        metaObject.setSystemName("TestMetaObject");
        when(metaObjectRepository.findAll()).thenReturn(Collections.singletonList(metaObject));

        // Call the method
        dataSourceCatalogService.syncDataAcrossRegions(dataSource, "CREATE");

    // Verify interactions
        verify(dataConnectClientService, times(1)).sendDataChanges(
            eq(OperationType.CREATE),
            eq("system.platform.dataconnect.onesource-data-source"),
            eq("") ,
            eq("") ,
            anyList(),
            eq("") ,
            any(LocalDateTime.class),
            eq(false)
        );
    }

}
