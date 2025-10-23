package com.thomsonreuters.metadataregistry.controller;

import com.thomsonreuters.dep.api.spring.response.ApiCollection;
import com.thomsonreuters.metadataregistry.model.entity.MetaObject;
import com.thomsonreuters.metadataregistry.exceptionhandler.MetaDataRegistryException;
import com.thomsonreuters.metadataregistry.model.dto.MetaObjectDTO;
import com.thomsonreuters.metadataregistry.model.dto.MetaObjectPostDTO;
import com.thomsonreuters.metadataregistry.model.dto.MetaObjectPutDTO;
import com.thomsonreuters.metadataregistry.model.dto.MetaRelationMetaModelDTO;
import com.thomsonreuters.metadataregistry.service.MetaObjectService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.UUID;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MetaObjectControllerTest {

    @Mock
    private MetaObjectService metaObjectService;

    @Mock
    private BindingResult bindingResult;

    @InjectMocks
    private MetaObjectController metaObjectController;

    @InjectMocks
    private MetaObjectRelationController metaObjectRelationController;

    public MetaObjectControllerTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldCreateMetaObject_WhenValidRequest() throws MetaDataRegistryException {
        MetaObjectPostDTO metaObjectPostDTO = new MetaObjectPostDTO();
        MetaObjectDTO metaObjectDTO = new MetaObjectDTO();
        metaObjectDTO.setId(UUID.randomUUID());

        when(bindingResult.hasErrors()).thenReturn(false);
        when(metaObjectService.createMetaObject(metaObjectPostDTO)).thenReturn(metaObjectDTO);

        ResponseEntity<Object> response = metaObjectController.createMetaObject(metaObjectPostDTO, bindingResult);

        assertEquals(201, response.getStatusCode().value());
        verify(metaObjectService, times(1)).createMetaObject(metaObjectPostDTO);
    }

    @Test
    void shouldThrowException_WhenValidationErrorsOccur() {
        MetaObjectPostDTO metaObjectPostDTO = new MetaObjectPostDTO();

        when(bindingResult.hasErrors()).thenReturn(true);

        assertThrows(MetaDataRegistryException.class, () -> {
            metaObjectController.createMetaObject(metaObjectPostDTO, bindingResult);
        });
    }

    @Test
    void shouldGetMetaObjectById_WhenValidIdProvided() throws MetaDataRegistryException {
        UUID id = UUID.randomUUID();
        MetaObjectDTO metaObjectDTO = new MetaObjectDTO();
        metaObjectDTO.setId(id);

        when(metaObjectService.getMetaObjectById(id)).thenReturn(metaObjectDTO);

        ResponseEntity<Object> response = metaObjectController.getMetaObjectById(id);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(metaObjectDTO, response.getBody());
        verify(metaObjectService, times(1)).getMetaObjectById(id);
    }


    @Test
    void shouldThrowException_WhenValidationErrorsOccurDuringUpdate() {
        UUID id = UUID.randomUUID();
        MetaObjectPutDTO metaObjectPostDTO = new MetaObjectPutDTO();

        when(bindingResult.hasErrors()).thenReturn(true);

        assertThrows(MetaDataRegistryException.class, () -> {
            metaObjectController.updateMetaObject(id, metaObjectPostDTO, bindingResult);
        });
    }

    @Test
    void shouldGetMetaModel_WhenValidIdProvided() throws MetaDataRegistryException {
        UUID id = UUID.randomUUID();
        MetaRelationMetaModelDTO metaObjectDTO = new MetaRelationMetaModelDTO();

        when(metaObjectService.getMetaModel(id)).thenReturn(metaObjectDTO);

        ResponseEntity<Object> response = metaObjectRelationController.getMetaModel(id);

        assertEquals(200, response.getStatusCode().value());
        verify(metaObjectService, times(1)).getMetaModel(id);
    }

    @Test
    void testSearchMetaObjects_success() {
        ApiCollection<MetaObject> mockCollection = mock(ApiCollection.class);
        when(mockCollection.getItems()).thenReturn(List.of(new MetaObject()));

        ResponseEntity<?> mockResponse = ResponseEntity.ok(mockCollection);

        when(metaObjectService.getAllMetaObjects(0, 10, null, null)).thenReturn((ResponseEntity) mockResponse);


        ResponseEntity<?> response = metaObjectController.searchMetaObjects(0, 10, null, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockCollection, response.getBody());
    }
    @Test
    void testSearchMetaObjects_ValidRequest() {
        ResponseEntity<String> expectedResponse = ResponseEntity.ok("Success");
        when(metaObjectService.getAllMetaObjects(0, 10, "+display_name", "\"display_name\" eq 'Test'"))
                .thenReturn((ResponseEntity) expectedResponse);

        ResponseEntity<?> response = metaObjectController.searchMetaObjects(0, 10, "+display_name", "\"display_name\" eq 'Test'");
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testSearchMetaObjects_InvalidOffset() {
        MetaDataRegistryException exception = assertThrows(MetaDataRegistryException.class, () ->
                metaObjectController.searchMetaObjects(-1, 10, null, null));
        assertEquals("Offset must be greater than or equal to 0.", exception.getMessage());
    }

    @Test
    void testSearchMetaObjects_InvalidLimit() {
        MetaDataRegistryException exception = assertThrows(MetaDataRegistryException.class, () ->
                metaObjectController.searchMetaObjects(0, 0, null, null));
        assertEquals("Limit must be greater than 0.", exception.getMessage());
    }


    @Test
    void shouldReturnSuccessResponse_WhenUsageCounterIsUpdatedRelatively() {
        // Arrange
        UUID id = UUID.randomUUID();
        String idAsString = id.toString();
        Map<String, Object> payload = Map.of("value", 10, "relative", true);
        int updatedCount = 15;

        when(metaObjectService.updateUsageCounter(idAsString,1)).thenReturn(updatedCount);

        // Act
        ResponseEntity<?> response = metaObjectController.updateUsageCounter(idAsString, payload);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof Map);
        Map<?, ?> responseBody = (Map<?, ?>) response.getBody();
        assertEquals("Success", responseBody.get("status"));
        assertEquals(updatedCount, responseBody.get("updated_usage_count"));
        verify(metaObjectService, times(1)).updateUsageCounter(idAsString, 1);
    }

    @Test
    void shouldReturnSuccessResponse_WhenUsageCounterIsUpdatedAbsolutely() {
        // Arrange
        UUID id = UUID.randomUUID();
        String idAsString = id.toString();
        Map<String, Object> payload = Map.of("value", 20, "relative", false);
        int updatedCount = 20;

        when(metaObjectService.updateUsageCounterAbsolute(idAsString, 20)).thenReturn(updatedCount);

        // Act
        ResponseEntity<?> response = metaObjectController.updateUsageCounter(idAsString, payload);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof Map);
        Map<?, ?> responseBody = (Map<?, ?>) response.getBody();
        assertEquals("Success", responseBody.get("status"));
        assertEquals(updatedCount, responseBody.get("updated_usage_count"));
        verify(metaObjectService, times(1)).updateUsageCounterAbsolute(idAsString, 20);
    }

    @Test
    void shouldReturnBadRequest_WhenPayloadIsInvalid() {
        // Arrange
        UUID id = UUID.randomUUID();
        String idAsString = id.toString();
        Map<String, Object> payload = Map.of("invalidKey", 10);

        // Act
        MetaDataRegistryException exception = assertThrows(MetaDataRegistryException.class, () ->
                metaObjectController.updateUsageCounter(idAsString, payload)
        );

        // Assert
        assertEquals("Invalid payload structure.", exception.getMessage());
        verify(metaObjectService, never()).updateUsageCounter(anyString(), anyInt());
        verify(metaObjectService, never()).updateUsageCounterAbsolute(anyString(), anyInt());
    }



}