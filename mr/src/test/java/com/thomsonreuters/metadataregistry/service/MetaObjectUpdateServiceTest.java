package com.thomsonreuters.metadataregistry.service;



import com.thomsonreuters.metadataregistry.configuration.ModelMapperConfig;
import com.thomsonreuters.metadataregistry.exceptionhandler.MetaDataRegistryException;
import com.thomsonreuters.metadataregistry.model.dto.MetaObjectAttributePutDTO;
import com.thomsonreuters.metadataregistry.model.dto.MetaObjectDTO;
import com.thomsonreuters.metadataregistry.model.dto.MetaObjectPutDTO;
import com.thomsonreuters.metadataregistry.model.entity.MetaObject;
import com.thomsonreuters.metadataregistry.model.entity.MetaObjectAttribute;
import com.thomsonreuters.metadataregistry.repository.MetaObjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MetaObjectUpdateServiceTest

{

    @InjectMocks
    private MetaObjectService metaObjectService;

    @Mock
    private MetaObjectRepository metaObjectRepository;

    @Mock
    private ModelMapper modelMapper;
    @Mock
    private ModelMapperConfig modelMapperConfig;



    private UUID metaObjectId;
    private MetaObject existingMetaObject;
    private MetaObjectPutDTO metaObjectPutDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        metaObjectId = UUID.randomUUID();

        // Mock existing MetaObject
        existingMetaObject = new MetaObject();
        existingMetaObject.setId(metaObjectId);
        existingMetaObject.setDescription("Existing Description");
        existingMetaObject.setDbTable("ExistingTable");
        existingMetaObject.setDisplayName("Existing Display Name");
        existingMetaObject.setOneSourceDomain("BF_AR");
        existingMetaObject.setAutogenId(true);
        existingMetaObject.setAttributes(Set.of(new MetaObjectAttribute()));

        // Mock MetaObjectPutDTO
        metaObjectPutDTO = new MetaObjectPutDTO();
        metaObjectPutDTO.setDescription("Updated Description");
        metaObjectPutDTO.setDbTable("UpdatedTable");
        metaObjectPutDTO.setDisplayName("Updated Display Name");
        metaObjectPutDTO.setAutogenId(false);
        metaObjectPutDTO.setAttributes(Set.of(new MetaObjectAttributePutDTO()));

        when(modelMapperConfig.modelMapper()).thenReturn(modelMapper);

        // Stub the mapping behavior
        when(modelMapper.map(any(), eq(MetaObjectDTO.class))).thenAnswer(invocation -> {
            MetaObject source = invocation.getArgument(0);
            MetaObjectDTO target = new MetaObjectDTO();
            target.setDescription(source.getDescription());
            target.setDbTable(source.getDbTable());
            // Map other fields as needed
            return target;
        });

        when(modelMapper.map(any(), eq(MetaObject.class))).thenAnswer(invocation -> {
            MetaObjectDTO source = invocation.getArgument(0);
            MetaObject target = new MetaObject();
            target.setDescription(source.getDescription());
            target.setDbTable(source.getDbTable());
            // Map other fields as needed
            return target;
        });
    }


    @Test
    void testUpdateMetaObjects_InvalidInput() {
        assertThrows(MetaDataRegistryException.class, () -> metaObjectService.updateMetaObjects(null, metaObjectPutDTO));
        assertThrows(MetaDataRegistryException.class, () -> metaObjectService.updateMetaObjects(metaObjectId, null));
    }

    @Test
    void testUpdateMetaObjects_MetaObjectNotFound() {
        when(metaObjectRepository.findById(metaObjectId)).thenReturn(Optional.empty());

        MetaDataRegistryException exception = assertThrows(MetaDataRegistryException.class,
                () -> metaObjectService.updateMetaObjects(metaObjectId, metaObjectPutDTO));

        assertEquals("MetaObject not found", exception.getMessage());
    }

    @Test
    void testUpdateMetaObjects_Conflict() {
        when(metaObjectRepository.findById(metaObjectId)).thenReturn(Optional.of(existingMetaObject));

        // Simulate conflict by making the updated data match the existing data
        metaObjectPutDTO.setDescription(existingMetaObject.getDescription());
        metaObjectPutDTO.setDbTable(existingMetaObject.getDbTable());
        metaObjectPutDTO.setDisplayName(existingMetaObject.getDisplayName());
        metaObjectPutDTO.setAutogenId(existingMetaObject.isAutogenId());
        metaObjectPutDTO.setAttributes(Set.of());

        MetaDataRegistryException exception = assertThrows(MetaDataRegistryException.class,
                () -> metaObjectService.updateMetaObjects(metaObjectId, metaObjectPutDTO));

        assertEquals("Failed to update MetaObject", exception.getMessage());
    }


    @Test
    void testUpdateMetaObjects_ExceptionHandling() {
        when(metaObjectRepository.findById(metaObjectId)).thenThrow(new RuntimeException("Unexpected error"));

        MetaDataRegistryException exception = assertThrows(MetaDataRegistryException.class,
                () -> metaObjectService.updateMetaObjects(metaObjectId, metaObjectPutDTO));

        assertEquals("Failed to update MetaObject", exception.getMessage());
    }
}

