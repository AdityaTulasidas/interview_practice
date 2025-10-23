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
import com.thomsonreuters.metadataregistry.model.dto.*;
import com.thomsonreuters.metadataregistry.model.entity.MetaObject;
import com.thomsonreuters.metadataregistry.model.entity.MetaObjectAttribute;
import com.thomsonreuters.metadataregistry.repository.DomainObjectRepository;
import com.thomsonreuters.metadataregistry.repository.DomainRepository;
import com.thomsonreuters.metadataregistry.repository.MetaObjectRelationRepository;
import com.thomsonreuters.metadataregistry.repository.MetaObjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
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
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

class MetaObjectServiceTest {

    @Mock
    private ApiSupport apiSupport;

    @Mock
    private ApiCriteria<MetaObjectDTO> apiCriteria;

    @Mock
    private ApiCollectionFactory apiCollection;

    @Mock
    private Specification<MetaObjectDTO> specification;

    @Mock
    private MetaObjectRepository metaObjectRepository;

    @Mock
    private ModelMapperConfig modelMapperConfig;


    @Mock
    private MetaObjectRelationRepository metaObjectRelationRepository;

    @Mock
    private MetaAttributeService metaAttributeService;

    @Mock
    private DataConnectClientService dataConnectClientService;

    @Mock
    private DomainObjectRepository domainObjectRepository;

    @Mock
    private DomainRepository domainRepository;

    @InjectMocks
    private MetaObjectService metaObjectService;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ModelMapper mockModelMapper = mock(ModelMapper.class);
        when(modelMapperConfig.modelMapper()).thenReturn(mockModelMapper);
        when(mockModelMapper.map(any(MetaObjectPostDTO.class), eq(MetaObject.class)))
                .thenAnswer(invocation -> {
                    return new MetaObject(UUID.randomUUID(),"description", "tableName", "schema","displayName","BF_AR", "system_name","domain_object" ,1,false,false, new HashSet<>(), new HashSet<>());});


        when(mockModelMapper.map(any(MetaObject.class), eq(MetaObjectDTO.class)))
                .thenAnswer(invocation -> {
                    MetaObject entity = invocation.getArgument(0);
                    MetaObjectDTO dto = new MetaObjectDTO();
                    dto.setDbTable(entity.getDbTable());
                    return dto;
                });
    }


    @Test
    void shouldThrowException_WhenCreatingDuplicateMetaObject() {
        // Arrange
        MetaObjectPostDTO postDTO = new MetaObjectPostDTO();
        postDTO.setDescription("description");
        postDTO.setDbTable("public.meta_object");
        postDTO.setSchema("schema");
        postDTO.setDisplayName("displayName");
        postDTO.setOneSourceDomain("BF_AR");
        postDTO.setSystemName("system_name");
        postDTO.setDomainObject("domain_object");
        postDTO.setAutogenId(false);
        postDTO.setEventEnabled(true);
        MetaObject metaObject = new MetaObject(UUID.randomUUID(),"description", "tableName", "schema","displayName","BF_AR", "system_name","domain_object" ,1,false,false, new HashSet<>(), new HashSet<>());

        when(modelMapperConfig.modelMapper().map(postDTO, MetaObject.class)).thenReturn(metaObject);
        when(domainRepository.existsBySystemName(anyString())).thenReturn(true);
        when(domainObjectRepository.existsBySystemName(postDTO.getDomainObject())).thenReturn(true);
        when(metaObjectRepository.save(any(MetaObject.class))).thenThrow(new MetaDataRegistryException("Error while creating MetaObject", "CONFLICT"));

        // Act & Assert
        MetaDataRegistryException exception = assertThrows(MetaDataRegistryException.class, () -> metaObjectService.createMetaObject(postDTO));
        assertEquals("Failed to create MetaObject", exception.getMessage());
    }

    @Test
    void shouldGetMetaObjectById_WhenValidIdProvided() {
        // Arrange
        UUID id = UUID.randomUUID();
        MetaObject metaObject = new MetaObject(UUID.randomUUID(),"description", "tableName", "schema","displayName","BF_AR", "system_name","domain_object" ,1,false,false, new HashSet<>(), new HashSet<>());
        MetaObjectDTO metaObjectDTO = new MetaObjectDTO();

        when(metaObjectRepository.findById(id)).thenReturn(Optional.of(metaObject));

        when(modelMapperConfig.modelMapper().map(metaObject, MetaObjectDTO.class)).thenReturn(metaObjectDTO);

        // Act
        MetaObjectDTO result = metaObjectService.getMetaObjectById(id);

        // Assert
        assertNotNull(result);
        verify(metaObjectRepository, times(1)).findById(id);
    }

    @Test
    void shouldThrowException_WhenMetaObjectNotFoundById() {
        // Arrange
        UUID id = UUID.randomUUID();

        when(metaObjectRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        MetaDataRegistryException exception = assertThrows(MetaDataRegistryException.class, () -> metaObjectService.getMetaObjectById(id));
        assertEquals("MetaObject not found", exception.getMessage());
    }

    @Test
    void shouldGetAllMetaObjects_WhenCalled() {
        // Arrange
        List<MetaObject> metaObjects = List.of(
                new MetaObject(UUID.randomUUID(), "description", "tableName1", "schema", "displayName", "BF_AR", "system_name1", "domain_object", 1, false, false, new HashSet<>(), new HashSet<>())
        );

        when(metaObjectRepository.findAll()).thenReturn(metaObjects);
        when(modelMapperConfig.modelMapper().map(any(MetaObject.class), eq(MetaObjectDTO.class))).thenReturn(new MetaObjectDTO());

        // Act
        List<MetaObjectDTO> result = metaObjectService.getAllMetaObjects();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(metaObjectRepository, times(1)).findAll();
    }


    @Test
    void shouldThrowException_WhenUpdatingNonExistentMetaObject() {
        // Arrange
        UUID id = UUID.randomUUID();
        MetaObjectPutDTO postDTO = new MetaObjectPutDTO();
        postDTO.setDescription("updatedDescription");
        postDTO.setDbTable("updated_table");
        postDTO.setDisplayName("updatedDisplayName");
        postDTO.setAutogenId(false);
        postDTO.setEventEnabled(true);

        when(metaObjectRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        MetaDataRegistryException exception = assertThrows(MetaDataRegistryException.class, () -> metaObjectService.updateMetaObjects(id, postDTO));
        assertEquals("MetaObject not found", exception.getMessage());
    }


    @Test
    void shouldThrowException_WhenNoRelationsFoundForMetaObjectId() {
        // Arrange
        UUID metaObjectId = UUID.randomUUID();
        when(metaObjectRelationRepository.findByParentObjectId(metaObjectId)).thenReturn(Collections.emptySet());

        // Act & Assert
        MetaDataRegistryException exception = assertThrows(MetaDataRegistryException.class,
                () -> metaObjectService.getMetaModel(metaObjectId));
        assertEquals("MetaObject not found", exception.getMessage());
        verify(metaObjectRelationRepository, times(1)).findByParentObjectId(metaObjectId);
        verify(metaObjectRepository, never()).findById(any());
    }



    @Test
    void shouldThrowException_WhenIdIsNull() {
        // Arrange
        MetaObjectPutDTO postDTO = new MetaObjectPutDTO();

        // Act & Assert
        MetaDataRegistryException exception = assertThrows(MetaDataRegistryException.class,
                () -> metaObjectService.updateMetaObjects(null, postDTO));
        assertEquals("MetaObject ID and request body cannot be null", exception.getMessage());
    }

    @Test
    void shouldThrowException_WhenRequestBodyIsNull() {
        // Arrange
        UUID id = UUID.randomUUID();

        // Act & Assert
        MetaDataRegistryException exception = assertThrows(MetaDataRegistryException.class,
                () -> metaObjectService.updateMetaObjects(id, null));
        assertEquals("MetaObject ID and request body cannot be null", exception.getMessage());
    }

    @Test
    void shouldThrowException_WhenMetaObjectNotFound() {
        // Arrange
        UUID id = UUID.randomUUID();
        MetaObjectPutDTO postDTO = new MetaObjectPutDTO();

        when(metaObjectRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        MetaDataRegistryException exception = assertThrows(MetaDataRegistryException.class,
                () -> metaObjectService.updateMetaObjects(id, postDTO));
        assertEquals("MetaObject not found", exception.getMessage());
    }




    @Test
    void shouldThrowException_WhenUnexpectedErrorOccurs() {
        // Arrange
        UUID id = UUID.randomUUID();
        MetaObjectPutDTO postDTO = new MetaObjectPutDTO();

        when(metaObjectRepository.findById(id)).thenThrow(new RuntimeException("Unexpected error"));

        // Act & Assert
        MetaDataRegistryException exception = assertThrows(MetaDataRegistryException.class,
                () -> metaObjectService.updateMetaObjects(id, postDTO));
        assertEquals("Failed to update MetaObject", exception.getMessage());


    }


    @Test
    void shouldUpdateUsageCounterAbsolute_WhenValidValueProvided() {
        // Arrange
        UUID id = UUID.randomUUID();
        MetaObject metaObject = new MetaObject();
        metaObject.setId(id);
        metaObject.setUsageCount(5);

        when(metaObjectRepository.findById(id)).thenReturn(Optional.of(metaObject));

        // Act
        int updatedCount = metaObjectService.updateUsageCounterAbsolute(id.toString(), 10);

        // Assert
        assertEquals(10, updatedCount);
        verify(metaObjectRepository, times(1)).save(metaObject);
    }

    @Test
    void shouldThrowException_WhenUsageCounterAbsoluteValueIsNegative() {
        // Arrange
        UUID id = UUID.randomUUID();
        MetaObject metaObject = new MetaObject();
        metaObject.setId(id);
        metaObject.setUsageCount(5);

        when(metaObjectRepository.findById(id)).thenReturn(Optional.of(metaObject));

        // Act & Assert
        MetaDataRegistryException exception = assertThrows(MetaDataRegistryException.class, () ->
                metaObjectService.updateUsageCounterAbsolute(id.toString(), -1)
        );
        assertEquals("Usage count cannot be negative.", exception.getMessage());
        verify(metaObjectRepository, never()).save(metaObject);
    }


    @Test
    void shouldReturnInternalServerError_WhenExceptionThrown() {
        // Arrange
        when(apiSupport.getCriteriaHolder(MetaObject.class)).thenThrow(new RuntimeException("DB error"));

        // Act
        ResponseEntity<?> response = metaObjectService.getAllMetaObjects(0, 10, null, null);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody() instanceof MetaDataRegistryException);
    }
    @Test
    void getAllMetaObjects_ResourceNotFound(){
        when(apiSupport.getCriteriaHolder(any(Class.class))).thenReturn(apiCriteria);
        when(apiCriteria.getSpecification()).thenReturn(specification);
        when(apiCriteria.getSpecification().and(any())).thenReturn(specification);
        Pageable pageable = PageRequest.of(0, 10);
        when(apiCriteria.getPageable()).thenReturn(pageable);
        when(metaObjectRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(Page.empty());

        doReturn(new ApiCollection<>(List.of(), new ApiMeta())).when(apiCollection).from(any(Page.class));
        // Call the method
        ResponseEntity<?> response = metaObjectService.getAllMetaObjects(0, 10, null, null);

        // Assertions
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    void getAllMetaObjects_ReturnSuccess() {
        MetaObjectDTO metaobject1 = new MetaObjectDTO();
        metaobject1.setId(UUID.randomUUID());
        metaobject1.setSystemName("mockName");
        MetaObjectDTO metaobject2 = new MetaObjectDTO();
        metaobject2.setId(UUID.randomUUID());
        metaobject2.setSystemName("mockName");
        List<MetaObjectDTO> metaObjectDTOList = List.of(metaobject1, metaobject2);
        Page<MetaObjectDTO> metaObjectDTOPage = new PageImpl<>(metaObjectDTOList);
        when(apiSupport.getCriteriaHolder(any(Class.class))).thenReturn(apiCriteria);
        when(apiCriteria.getSpecification()).thenReturn(specification);
        when(apiCriteria.getSpecification().and(any())).thenReturn(specification);
        Pageable pageable = PageRequest.of(0, 10);
        when(apiCriteria.getPageable()).thenReturn(pageable);


        // Mock repository to return the page with DTOs
        when(metaObjectRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(metaObjectDTOPage);

        // Mock ApiCollectionFactory to return a collection with your DTOs
        ApiCollection<MetaObjectDTO> apiCollectionMock = spy(new ApiCollection<>(metaObjectDTOList, new ApiMeta()));
        when(apiCollection.from(any(Page.class))).thenReturn(apiCollectionMock);
        //  mock it to return the same mock
        doReturn(apiCollectionMock).when(apiCollectionMock).mapItems(any());


        // Act
        ResponseEntity<?> response = metaObjectService.getAllMetaObjects(0, 10, null, null);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertTrue(response.getBody() instanceof ApiCollection);
        ApiCollection<?> resultCollection = (ApiCollection<?>) response.getBody();
        assertEquals(2, resultCollection.getItems().size());

    }


    // Test case: shouldUpdateMetaObject_WhenValidInputProvided
    @Test
    void shouldUpdateMetaObject_WhenValidInputProvided() throws DataConnectClientException {
        // Arrange
        UUID id = UUID.randomUUID();
        MetaObject existingMetaObject = new MetaObject(UUID.randomUUID(),"description", "tableName", "schema","displayName","BF_AR", "system_name","domain_object" ,1,false,false, new HashSet<>(), new HashSet<>());
        existingMetaObject.setId(id);
        existingMetaObject.setAttributes(new HashSet<>());
        existingMetaObject.setSystemName("existingName");
        existingMetaObject.setDbTable("public.meta_object");
        existingMetaObject.setOneSourceDomain("BF_AR");



        MetaObjectPutDTO postDTO = new MetaObjectPutDTO();
        postDTO.setDescription("newDescription");
        postDTO.setDbTable("public.meta_object");
        postDTO.setDisplayName("newDisplayName");
        postDTO.setAutogenId(false);
        postDTO.setEventEnabled(true);

        postDTO.setAttributes(Collections.emptySet());
        postDTO.setOneSourceDomain("BF_AR");
        postDTO.setDbTable("public.meta_object");

        when(metaObjectRepository.findById(id)).thenReturn(Optional.of(existingMetaObject));
        when(domainRepository.existsBySystemName(anyString())).thenReturn(true);
        when(metaObjectRepository.save(any(MetaObject.class))).thenReturn(existingMetaObject);
        when(metaObjectRepository.findByDbTable(anyString())).thenReturn(Optional.of(existingMetaObject));
        when(modelMapperConfig.modelMapper().map(existingMetaObject, MetaObjectDTO.class)).thenReturn(new MetaObjectDTO());
        doNothing().when(dataConnectClientService).sendDataChanges(
                eq(OperationType.UPDATE),
                eq("REAL_TIME"),
                eq("BF_AR"),
                eq(existingMetaObject.getSystemName()),
                anyList(),
                anyString(),
                any(LocalDateTime.class),
                eq(false)
        );

        // Act
        MetaObjectDTO result = metaObjectService.updateMetaObjects(id, postDTO);

        // Assert
        assertNotNull(result);
        verify(metaObjectRepository, times(1)).findById(id);
        verify(metaObjectRepository, times(1)).save(existingMetaObject);
    verify(dataConnectClientService, times(1)).sendDataChanges(
        eq(OperationType.UPDATE),
        eq("system.platform.dataconnect.meta-object"),
        eq("") ,
        eq("") ,
        anyList(),
        eq("") ,
        any(LocalDateTime.class),
        eq(false)
    );
    }

    // Test case: shouldCreateMetaObject_WhenValidInputProvided
    @Test
    void shouldCreateMetaObject_WhenValidInputProvided() throws DataConnectClientException {
        // Arrange
        MetaObjectPostDTO postDTO = new MetaObjectPostDTO();
        postDTO.setDescription("description");
        postDTO.setDbTable("public.meta_object");
        postDTO.setSchema("schema");
        postDTO.setDisplayName("displayName");
        postDTO.setOneSourceDomain("BF_AR");
        postDTO.setSystemName("system_name");
        postDTO.setDomainObject("domain_object");
        postDTO.setAutogenId(false);
        postDTO.setBusinessName("business_name");
        MetaObject metaObject = new MetaObject(UUID.randomUUID(),"description", "tableName", "schema","displayName","BF_AR", "system_name","domain_object" ,1,false,false, new HashSet<>(), new HashSet<>());
        MetaObjectDTO metaObjectDTO = new MetaObjectDTO();
        metaObjectDTO.setDbTable(postDTO.getDbTable());
        metaObjectDTO.setSystemName(postDTO.getSystemName());
        metaObjectDTO.setOneSourceDomain(postDTO.getOneSourceDomain());

        when(domainRepository.existsBySystemName(anyString())).thenReturn(true);
        when(modelMapperConfig.modelMapper().map(postDTO, MetaObject.class)).thenReturn(metaObject);
        when(metaObjectRepository.save(any(MetaObject.class))).thenReturn(metaObject);
        when(metaObjectRepository.findByDbTable(anyString())).thenReturn(Optional.of(metaObject));
        when(domainObjectRepository.existsBySystemName(anyString())).thenReturn(true);
        when(modelMapperConfig.modelMapper().map(metaObject, MetaObjectDTO.class)).thenReturn(metaObjectDTO);
        doNothing().when(dataConnectClientService).sendDataChanges(
                eq(OperationType.CREATE),
                eq("REAL_TIME"),
                eq("BF_AR"),
                eq(metaObject.getSystemName()),
                anyList(),
                anyString(),
                any(LocalDateTime.class),
                eq(false)
        );

        // Act
        MetaObjectDTO result = metaObjectService.createMetaObject(postDTO);

        // Assert
        assertNotNull(result);
        verify(metaObjectRepository, times(1)).save(any(MetaObject.class));
    verify(dataConnectClientService, times(1)).sendDataChanges(
        eq(OperationType.CREATE),
        eq("system.platform.dataconnect.meta-object"),
        eq("") ,
        eq("") ,
        anyList(),
        eq("") ,
        any(LocalDateTime.class),
        eq(false)
    );
    }

    // Test case: shouldUpdateExistingAttribute_WhenPresent
    @Test
    void shouldUpdateExistingAttribute_WhenPresent() throws DataConnectClientException {
        // Arrange
        UUID id = UUID.randomUUID();
        MetaObject existingMetaObject = new MetaObject(UUID.randomUUID(),"description", "tableName", "schema","displayName","BF_AR", "system_name","domain_object" ,1,false,false, new HashSet<>(), new HashSet<>());
        existingMetaObject.setId(id);

        MetaObjectAttribute existingAttribute = new MetaObjectAttribute();
        existingAttribute.setDbColumn("existingColumn");
        existingAttribute.setDisplayName("existingAttribute");
        existingMetaObject.setAttributes(Set.of(existingAttribute));

        MetaObjectAttributePutDTO updatedAttribute = new MetaObjectAttributePutDTO();
        updatedAttribute.setDbColumn("existingColumn");
        updatedAttribute.setDisplayName("updatedAttribute");

        MetaObjectPutDTO postDTO = new MetaObjectPutDTO();
        postDTO.setDescription("newDescription");
        postDTO.setDbTable("public.meta_object");
        postDTO.setDisplayName("newDisplayName");
        postDTO.setAutogenId(false);
        postDTO.setEventEnabled(true);
        postDTO.setAttributes(Set.of(updatedAttribute));
        postDTO.setOneSourceDomain("BF_AR");

        when(metaObjectRepository.findById(id)).thenReturn(Optional.of(existingMetaObject));
        when(domainRepository.existsBySystemName(anyString())).thenReturn(true);
        when(metaObjectRepository.save(any(MetaObject.class))).thenReturn(existingMetaObject);
        when(metaObjectRepository.findByDbTable(anyString())).thenReturn(Optional.of(existingMetaObject));
        when(modelMapperConfig.modelMapper().map(existingMetaObject, MetaObjectDTO.class)).thenReturn(new MetaObjectDTO());
        doNothing().when(dataConnectClientService).sendDataChanges(
                eq(OperationType.UPDATE),
                eq("REAL_TIME"),
                eq("BF_AR"),
                eq(existingMetaObject.getSystemName()),
                anyList(),
                anyString(),
                any(LocalDateTime.class),
                eq(false)
        );

        // Act
        MetaObjectDTO result = metaObjectService.updateMetaObjects(id, postDTO);

        // Assert
        assertNotNull(result);
        verify(metaObjectRepository, times(1)).findById(id);
        verify(metaObjectRepository, times(1)).save(existingMetaObject);
    verify(dataConnectClientService, times(1)).sendDataChanges(
        eq(OperationType.UPDATE),
        eq("system.platform.dataconnect.meta-object"),
        eq("") ,
        eq("") ,
        anyList(),
        eq("") ,
        any(LocalDateTime.class),
        eq(false)
    );
    }

    // Test case: shouldAddNewAttribute_WhenNotPresent
    @Test
    void shouldAddNewAttribute_WhenNotPresent() throws DataConnectClientException {
        // Arrange
        UUID id = UUID.randomUUID();
        MetaObject existingMetaObject = new MetaObject(UUID.randomUUID(),"description", "tableName", "schema","displayName","BF_AR", "system_name","domain_object" ,1,false,false, new HashSet<>(), new HashSet<>());
        existingMetaObject.setId(id);
        existingMetaObject.setAttributes(new HashSet<>());

        MetaObjectAttributePutDTO newAttribute = new MetaObjectAttributePutDTO();
        newAttribute.setDbColumn("newColumn");
        newAttribute.setDisplayName("newAttribute");

        MetaObjectPutDTO postDTO = new MetaObjectPutDTO();
        postDTO.setDescription("newDescription");
        postDTO.setDbTable("public.meta_object");
        postDTO.setDisplayName("newDisplayName");
        postDTO.setAutogenId(false);
        postDTO.setEventEnabled(true);
        postDTO.setAttributes(Set.of(newAttribute));
        postDTO.setOneSourceDomain("BF_AR");
        when(domainRepository.existsBySystemName(anyString())).thenReturn(true);

        when(metaObjectRepository.findById(id)).thenReturn(Optional.of(existingMetaObject));
        when(metaObjectRepository.save(any(MetaObject.class))).thenReturn(existingMetaObject);
        when(metaObjectRepository.findByDbTable(anyString())).thenReturn(Optional.of(existingMetaObject));
        when(modelMapperConfig.modelMapper().map(existingMetaObject, MetaObjectDTO.class)).thenReturn(new MetaObjectDTO());
        when(modelMapperConfig.modelMapper().map(newAttribute, MetaObjectAttribute.class)).thenReturn(new MetaObjectAttribute());
        doNothing().when(dataConnectClientService).sendDataChanges(
                eq(OperationType.UPDATE),
                eq("REAL_TIME"),
                eq("BF_AR"),
                eq(existingMetaObject.getSystemName()),
                anyList(),
                anyString(),
                any(LocalDateTime.class),
                eq(false)
        );

        // Act
        MetaObjectDTO result = metaObjectService.updateMetaObjects(id, postDTO);

        // Assert
        assertNotNull(result);
        verify(metaObjectRepository, times(1)).findById(id);
        verify(metaObjectRepository, times(1)).save(existingMetaObject);
    verify(dataConnectClientService, times(1)).sendDataChanges(
        eq(OperationType.UPDATE),
        eq("system.platform.dataconnect.meta-object"),
        eq("") ,
        eq("") ,
        anyList(),
        eq("") ,
        any(LocalDateTime.class),
        eq(false)
    );
    }
}