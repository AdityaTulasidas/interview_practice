package com.thomsonreuters.metadataregistry.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.*;

import com.thomsonreuters.metadataregistry.configuration.ModelMapperConfig;
import com.thomsonreuters.metadataregistry.exceptionhandler.MetaDataRegistryException;
import com.thomsonreuters.metadataregistry.model.dto.MetaObjectDTO;
import com.thomsonreuters.metadataregistry.model.dto.MetaObjectRelationDTO;
import com.thomsonreuters.metadataregistry.model.dto.MetaRelationMetaModelDTO;
import com.thomsonreuters.metadataregistry.model.dto.MetaRelationModelDTO;
import com.thomsonreuters.metadataregistry.model.entity.MetaObject;
import com.thomsonreuters.metadataregistry.model.entity.MetaObjectRelation;
import com.thomsonreuters.metadataregistry.repository.MetaObjectRelationRepository;
import com.thomsonreuters.metadataregistry.repository.MetaObjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

@ExtendWith(MockitoExtension.class)
class MetaModelServiceTest {

    @Mock
    private MetaObjectRelationRepository metaObjectRelationRepository;

    @Mock
    private MetaObjectRepository metaObjectRepository;

    @Mock
    private MetaAttributeService metaAttributeService;

    @Mock
    private ModelMapperConfig modelMapperConfig;

    @InjectMocks
    private MetaObjectService metaModelService;

    private UUID metaObjectId;
    private MetaObject parentObject;
    private MetaObject childObject;
    private MetaObjectRelation relation;
    private Set<MetaObjectRelation> relations;
    private MetaObjectDTO parentObjectDTO;
    private MetaObjectDTO childObjectDTO;
    private MetaObjectRelationDTO relationDTO;

    @BeforeEach
    public void setUp() {

        metaObjectId = UUID.randomUUID();
        parentObject = new MetaObject();
        parentObject.setId(metaObjectId);
        parentObject.setCreatedBy("creator");
        parentObject.setUpdatedBy("updater");


        childObject = new MetaObject();
        childObject.setId(UUID.randomUUID());
        childObject.setCreatedBy("creator");
        childObject.setUpdatedBy("updater");
        childObject.setSystemName("ChildObject");


        relation = new MetaObjectRelation();
        relation.setId(UUID.randomUUID());
        relation.setParentObject(parentObject);
        relation.setChildObject(childObject);
        relation.setDescription("relation description");
        relation.setParentObjRelCol("parentCol");
        relation.setChildObjRelCol("childCol");
        relation.setRelationType("CHILD");
        relation.setCreatedBy("creator");
        relation.setUpdatedBy("updater");


        relations = new HashSet<>();
        relations.add(relation);

        parentObjectDTO = new MetaObjectDTO();
        parentObjectDTO.setId(metaObjectId);
        parentObjectDTO.setCreatedBy("creator");
        parentObjectDTO.setUpdatedBy("updater");


        childObjectDTO = new MetaObjectDTO();
        childObjectDTO.setId(childObject.getId());
        childObjectDTO.setCreatedBy("creator");
        childObjectDTO.setUpdatedBy("updater");
        childObjectDTO.setSystemName(childObject.getSystemName());


        relationDTO = new MetaObjectRelationDTO();
        relationDTO.setId(relation.getId());
        relationDTO.setParentObjectId(parentObject.getId());
        relationDTO.setChildObjectId(childObject.getId());
        relationDTO.setDescription("relation description");
        relationDTO.setParentObjRelCol("parentCol");
        relationDTO.setChildObjRelCol("childCol");
        relationDTO.setCreatedBy("creator");
        relationDTO.setUpdatedBy("updater");
        relationDTO.setRelationType("CHILD");

    }


    @Test
     void testGetMetaModel_MetaObjectNotFound() {
        when(metaObjectRelationRepository.findByParentObjectId(metaObjectId)).thenReturn(Collections.emptySet());

        MetaDataRegistryException exception = assertThrows(MetaDataRegistryException.class, () -> {
            metaModelService.getMetaModel(metaObjectId);
        });

        assertEquals("MetaObject not found", exception.getMessage());
        assertEquals("NOT_FOUND", exception.getCode());
    }

    @Test
    void testGetMetaModel_Success() {
        // Arrange
        when(metaObjectRelationRepository.findByParentObjectId(metaObjectId)).thenReturn(relations);
        when(metaObjectRepository.findById(metaObjectId)).thenReturn(Optional.of(parentObject));

        ModelMapper mockModelMapper = mock(ModelMapper.class);
        when(modelMapperConfig.modelMapper()).thenReturn(mockModelMapper);

        // Stub the mapping for parent object
        when(mockModelMapper.map(parentObject, MetaObjectDTO.class)).thenReturn(parentObjectDTO);

        // Stub the mapping for child relations
        MetaRelationModelDTO relationModelDTO = new MetaRelationModelDTO();
        relationModelDTO.setChildObject(childObjectDTO);
        relationModelDTO.setId(relation.getId());
        relationModelDTO.setParentObjectId(relation.getParentObject().getId());
        relationModelDTO.setChildObjectId(relation.getChildObject().getId());
        relationModelDTO.setDescription(relation.getDescription());
        when(mockModelMapper.map(relation, MetaRelationModelDTO.class)).thenReturn(relationModelDTO);

        // Act
        MetaRelationMetaModelDTO result = metaModelService.getMetaModel(metaObjectId);

        // Assert
        assertNotNull(result);
        assertEquals(parentObjectDTO, result.getParentObject());
        assertEquals(1, result.getChildObjectRelations().size());
        assertTrue(result.getChildObjectRelations().contains(relationModelDTO));
    }
}
