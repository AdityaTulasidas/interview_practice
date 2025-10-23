package com.thomsonreuters.metadataregistry.controller;


import com.thomsonreuters.metadataregistry.exceptionhandler.MetaDataRegistryException;
import com.thomsonreuters.metadataregistry.model.dto.MetaObjectDTO;
import com.thomsonreuters.metadataregistry.model.dto.MetaObjectPostDTO;
import com.thomsonreuters.metadataregistry.service.MetaObjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

 class MetaObjectAttributeControllerTest {

    @Mock
    private MetaObjectService metaObjectService;

    @Mock
    private BindingResult bindingResult;

    @InjectMocks
    private MetaObjectController metaObjectController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldCreateMetaAttribute_WhenValidRequest() throws Exception {
        MetaObjectDTO metaObjectDTO = new MetaObjectDTO();
        MetaObjectPostDTO metaObjectPostDTO = new MetaObjectPostDTO();
        when(bindingResult.hasErrors()).thenReturn(false);
        when(metaObjectService.createMetaObject(any(MetaObjectPostDTO.class))).thenReturn(metaObjectDTO);

        ResponseEntity<Object> response = metaObjectController.createMetaObject(metaObjectPostDTO, bindingResult);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(metaObjectService, times(1)).createMetaObject(any(MetaObjectPostDTO.class));
    }

    @Test
    void shouldGetMetaAttributeById_WhenValidIdProvided() throws MetaDataRegistryException {
        UUID id = UUID.randomUUID();
        MetaObjectDTO metaObjectDTO = new MetaObjectDTO();
        when(metaObjectService.getMetaObjectById(id)).thenReturn(metaObjectDTO);

        ResponseEntity<Object> response = metaObjectController.getMetaObjectById(id);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(metaObjectService, times(1)).getMetaObjectById(id);
    }



}