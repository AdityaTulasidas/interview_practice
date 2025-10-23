package com.thomsonreuters.metadataregistry.service;

import java.time.LocalDateTime;

import com.thomsonreuters.dataconnect.dataintegration.exceptionhandler.DataConnectClientException;
import com.thomsonreuters.dataconnect.dataintegration.services.job.DataConnectClientService;
import com.thomsonreuters.metadataregistry.exceptionhandler.MetaDataRegistryException;
import com.thomsonreuters.metadataregistry.configuration.ModelMapperConfig;
import com.thomsonreuters.metadataregistry.model.dto.MetaObjectRelationDTO;
import com.thomsonreuters.metadataregistry.model.entity.MetaObject;
import com.thomsonreuters.metadataregistry.model.entity.MetaObjectRelation;
import com.thomsonreuters.metadataregistry.repository.MetaObjectRelationRepository;
import com.thomsonreuters.metadataregistry.repository.MetaObjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MetaObjectRelationServiceTest {

    @Mock
    private MetaObjectRelationRepository metaObjectRelationRepository;

    @Mock
    private MetaObjectRepository metaObjectRepository;

    @Mock
    private ModelMapperConfig modelMapperConfig;

    @Mock
    private DataConnectClientService dataConnectClientService;

    @InjectMocks
    private MetaObjectRelationService metaObjectRelationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }



    @Test
    void shouldThrowException_WhenParentObjectNotFound() {
        // Arrange
        MetaObjectRelationDTO dto = new MetaObjectRelationDTO();
        dto.setParentObjectId(UUID.randomUUID());
        dto.setChildObjectId(UUID.randomUUID());

        when(metaObjectRepository.findById(dto.getParentObjectId())).thenReturn(Optional.empty());

        // Act & Assert
        MetaDataRegistryException exception = assertThrows(MetaDataRegistryException.class,
                () -> metaObjectRelationService.createMetaObjectRelationService(dto));
        assertEquals("Parent Object does not exists", exception.getMessage());
    }

    @Test
    void shouldThrowException_WhenChildObjectNotFound() {
        // Arrange
        MetaObject parentObject = new MetaObject();
        parentObject.setId(UUID.randomUUID());

        MetaObjectRelationDTO dto = new MetaObjectRelationDTO();
        dto.setParentObjectId(parentObject.getId());
        dto.setChildObjectId(UUID.randomUUID());

        when(metaObjectRepository.findById(dto.getParentObjectId())).thenReturn(Optional.of(parentObject));
        when(metaObjectRepository.findById(dto.getChildObjectId())).thenReturn(Optional.empty());

        // Act & Assert
        MetaDataRegistryException exception = assertThrows(MetaDataRegistryException.class,
                () -> metaObjectRelationService.createMetaObjectRelationService(dto));
        assertEquals("Child Object does not exists", exception.getMessage());
    }

    @Test
    void shouldThrowException_WhenMetaObjectRelationAlreadyExists() {
        // Arrange
        MetaObject parentObject = new MetaObject();
        parentObject.setId(UUID.randomUUID());

        MetaObject childObject = new MetaObject();
        childObject.setId(UUID.randomUUID());

        MetaObjectRelationDTO dto = new MetaObjectRelationDTO();
        dto.setParentObjectId(parentObject.getId());
        dto.setChildObjectId(childObject.getId());

        when(metaObjectRepository.findById(dto.getParentObjectId())).thenReturn(Optional.of(parentObject));
        when(metaObjectRepository.findById(dto.getChildObjectId())).thenReturn(Optional.of(childObject));
        when(metaObjectRelationRepository.save(any(MetaObjectRelation.class)))
                .thenThrow(new MetaDataRegistryException("MetaObject Relation already exists", "CONFLICT"));

        // Act & Assert
        MetaDataRegistryException exception = assertThrows(MetaDataRegistryException.class,
                () -> metaObjectRelationService.createMetaObjectRelationService(dto));
        assertEquals("MetaObject Relation already exists", exception.getMessage());
    }

    @Test
    void shouldCreateMetaObjectRelation_WhenValidInputProvidedtests() throws DataConnectClientException {
        // Arrange
        MetaObject parentObject = new MetaObject();
        parentObject.setId(UUID.randomUUID());
        parentObject.setSystemName("ParentObject");
        parentObject.setOneSourceDomain("BF_AR"); // Set OneSourceDomain

        MetaObject childObject = new MetaObject();
        childObject.setId(UUID.randomUUID());
        childObject.setOneSourceDomain("BF_AR");
        childObject.setSystemName("ChildObject");

        MetaObjectRelationDTO dto = new MetaObjectRelationDTO();
        dto.setParentObjectId(parentObject.getId());
        dto.setChildObjectId(childObject.getId());
        dto.setRelationType("CHILD");
        dto.setDescription("Test Relation");

        MetaObjectRelation entity = new MetaObjectRelation();
        entity.setId(UUID.randomUUID());

        MetaObject metaObject = new MetaObject();
        metaObject.setSystemName("MetaObjectRelation");
        metaObject.setOneSourceDomain("BF_AR"); // Set OneSourceDomain for the mocked object

        when(metaObjectRepository.findById(dto.getParentObjectId())).thenReturn(Optional.of(parentObject));
        when(metaObjectRepository.findById(dto.getChildObjectId())).thenReturn(Optional.of(childObject));
        when(metaObjectRelationRepository.save(any(MetaObjectRelation.class))).thenReturn(entity);
        when(metaObjectRepository.findByDbTable("public.meta_object_relation")).thenReturn(Optional.of(metaObject));
        doNothing().when(dataConnectClientService).sendDataChanges(
                any(),
                any(),
                any(),
                anyString(),
                anyList(),
                anyString(),
                any(LocalDateTime.class),
                eq(false)
        ); // Mock sendDataChanges

        // Act
        String result = metaObjectRelationService.createMetaObjectRelationService(dto);

        // Assert
        assertNotNull(result);
        verify(metaObjectRelationRepository, times(1)).save(any(MetaObjectRelation.class));
        verify(dataConnectClientService, times(1)).sendDataChanges(
                any(),
                any(),
                any(),
                anyString(),
                anyList(),
                anyString(),
                any(LocalDateTime.class),
                eq(false)
        );
    }
}