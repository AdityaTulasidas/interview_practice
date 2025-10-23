package com.thomsonreuters.metadataregistry.service;

    import com.thomsonreuters.metadataregistry.configuration.ModelMapperConfig;
    import com.thomsonreuters.metadataregistry.exceptionhandler.MetaDataRegistryException;
    import com.thomsonreuters.metadataregistry.model.dto.MetaObjectAttributeDTO;
    import com.thomsonreuters.metadataregistry.model.entity.MetaObject;
    import com.thomsonreuters.metadataregistry.model.entity.MetaObjectAttribute;
    import com.thomsonreuters.metadataregistry.repository.MetaAttributeRepository;
    import com.thomsonreuters.metadataregistry.repository.MetaObjectRepository;
    import org.junit.jupiter.api.BeforeEach;
    import org.junit.jupiter.api.Test;
    import org.mockito.InjectMocks;
    import org.mockito.Mock;
    import org.mockito.MockitoAnnotations;
    import org.modelmapper.ModelMapper;

    import java.util.*;

    import static org.junit.jupiter.api.Assertions.*;
    import static org.mockito.Mockito.*;

    class MetaAttributeServiceTest {

        @Mock
        private MetaAttributeRepository metaAttributeRepository;

        @Mock
        private MetaObjectRepository metaObjectRepository;

        @Mock
        private ModelMapperConfig modelMapperConfig;

        @InjectMocks
        private MetaAttributeService metaAttributeService;

        private MetaObject metaObject;
        private MetaObjectAttribute metaObjectAttribute;
        private MetaObjectAttributeDTO metaObjectAttributeDTO;

        @BeforeEach
        void setUp() {
            MockitoAnnotations.openMocks(this);


            // Initialize MetaObject
            UUID id = UUID.randomUUID();
            metaObject = new MetaObject();
            metaObject.setId(id);
            metaObject.setSystemName("TestSystemName");

            // Initialize MetaObjectAttribute
            metaObjectAttribute = new MetaObjectAttribute();
            metaObjectAttribute.setId(UUID.randomUUID());
            metaObjectAttribute.setMetaObject(metaObject);

            // Initialize MetaObjectAttributeDTO
            metaObjectAttributeDTO = new MetaObjectAttributeDTO();
            metaObjectAttributeDTO.setMetaObjectSysName("TestSystemName");
        }

        @Test
        void shouldReturnAttributes_whenValidIdProvided() {
            // Arrange
            ModelMapper mockModelMapper = mock(ModelMapper.class);
         when(modelMapperConfig.modelMapper()).thenReturn(mockModelMapper);
            UUID id=metaObject.getId();
            when(metaObjectRepository.findById(id)).thenReturn(Optional.of(metaObject));
            when(metaAttributeRepository.findByMetaObject(metaObject)).thenReturn(Collections.singletonList(metaObjectAttribute));
            when(mockModelMapper.map(metaObjectAttribute, MetaObjectAttributeDTO.class)).thenReturn(metaObjectAttributeDTO);

            // Act
            Set<MetaObjectAttributeDTO> result = metaAttributeService.getAttributes(id);

            // Assert
            assertNotNull(result);
            assertEquals(1, result.size());
            assertTrue(result.stream().anyMatch(dto -> "TestSystemName".equals(dto.getMetaObjectSysName())));
            verify(metaObjectRepository, times(1)).findById(id);
            verify(metaAttributeRepository, times(1)).findByMetaObject(metaObject);
            verify(mockModelMapper, times(1)).map(metaObjectAttribute, MetaObjectAttributeDTO.class);
        }

        @Test
        void shouldThrowException_whenIdIsNull() {
            // Act & Assert
            MetaDataRegistryException exception = assertThrows(MetaDataRegistryException.class, () -> metaAttributeService.getAttributes(null));
            assertEquals("MetaObjectId cannot be null or empty", exception.getMessage());
        }



        @Test
        void shouldThrowException_whenMetaObjectNotFound() {
            // Arrange
            UUID id=UUID.randomUUID();

            lenient().when(metaObjectRepository.findById(id)).thenReturn(Optional.empty());

            // Act & Assert
            MetaDataRegistryException exception = assertThrows(MetaDataRegistryException.class, () -> metaAttributeService.getAttributes(id));
            assertEquals("MetaObject not found", exception.getMessage());
            verify(metaObjectRepository, times(1)).findById(id);
        }

        @Test
        void shouldThrowException_whenUnexpectedErrorOccurs() {
            // Arrange
            UUID id = metaObject.getId();
            when(metaObjectRepository.findById(id)).thenThrow(new RuntimeException("Unexpected error"));

            // Act & Assert
            MetaDataRegistryException exception = assertThrows(MetaDataRegistryException.class, () -> metaAttributeService.getAttributes(id));
            assertEquals("Failed to fetch MetaAttributes by MetaObjectId", exception.getMessage());
            verify(metaObjectRepository, times(1)).findById(id);
        }
    }