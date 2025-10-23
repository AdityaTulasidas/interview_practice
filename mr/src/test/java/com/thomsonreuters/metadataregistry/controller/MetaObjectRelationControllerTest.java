package com.thomsonreuters.metadataregistry.controller;

import com.thomsonreuters.metadataregistry.exceptionhandler.MetaDataRegistryException;
import com.thomsonreuters.metadataregistry.model.dto.MetaObjectRelationDTO;
import com.thomsonreuters.metadataregistry.service.MetaObjectRelationService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MetaObjectRelationControllerTest {

    @Mock
    private MetaObjectRelationService metaObjectRelationService;

    @Mock
    private BindingResult bindingResult;

    @InjectMocks
    private MetaObjectRelationController metaObjectRelationController;

    public MetaObjectRelationControllerTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldCreateMetaAttribute_WhenValidRequest() throws Exception {
        MetaObjectRelationDTO metaObjectRelationDTO = new MetaObjectRelationDTO();
        String id = "relation-id";

        when(bindingResult.hasErrors()).thenReturn(false);
        when(metaObjectRelationService.createMetaObjectRelationService(metaObjectRelationDTO)).thenReturn(id);

        ResponseEntity<Object> response = metaObjectRelationController.createMetaObjectRelation(metaObjectRelationDTO, bindingResult);

        assertEquals(201, response.getStatusCode().value());
        verify(metaObjectRelationService, times(1)).createMetaObjectRelationService(metaObjectRelationDTO);
    }

    @Test
    void shouldThrowException_WhenValidationFailsDuringUpdate() {
        MetaObjectRelationDTO metaObjectRelationDTO = new MetaObjectRelationDTO();

        when(bindingResult.hasErrors()).thenReturn(true);

        assertThrows(MetaDataRegistryException.class, () -> {
            metaObjectRelationController.createMetaObjectRelation(metaObjectRelationDTO, bindingResult);
        });
    }
}