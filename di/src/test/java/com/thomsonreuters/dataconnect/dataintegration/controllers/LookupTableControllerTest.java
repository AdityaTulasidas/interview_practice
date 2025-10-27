package com.thomsonreuters.dataconnect.dataintegration.controllers;

import com.thomsonreuters.dataconnect.dataintegration.dto.TransformationFunctionDTO;
import com.thomsonreuters.dataconnect.dataintegration.dto.TransformationParamGetDTO;
import com.thomsonreuters.dataconnect.dataintegration.dto.TransformTypeDTO;
import com.thomsonreuters.dataconnect.dataintegration.model.entity.TransformationFunction;
import com.thomsonreuters.dataconnect.dataintegration.model.entity.TransformationType;
import com.thomsonreuters.dataconnect.dataintegration.services.LookupTableService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LookupTableControllerTest {

    @Mock
    private LookupTableService lookupTableService;

    @InjectMocks
    private LookupTablesController lookupTableController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetTransformationFunctionParamsByTransformFuncId() {
        String transformFuncId = "func1";
        TransformationParamGetDTO param1 = new TransformationParamGetDTO();
        param1.setId(1);
        param1.setSystemName("paramSystem1");
        param1.setDescription("desc1");
        param1.setDisplayName("display1");
        TransformationParamGetDTO param2 = new TransformationParamGetDTO();
        param2.setId(2);
        param2.setSystemName("paramSystem2");
        param2.setDescription("desc2");
        param2.setDisplayName("display2");
        List<TransformationParamGetDTO> mockParams = Arrays.asList(param1, param2);
        when(lookupTableService.getTransformationFunctionParamsByTransformFuncId(transformFuncId)).thenReturn(mockParams);

        ResponseEntity<List<TransformationParamGetDTO>> response = lookupTableController.getTransformationFunctionParamsByTransformFuncId(transformFuncId);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockParams, response.getBody());
    }

    @Test
    void testGetAllTransformTypes() {
        TransformationType entity1 = new TransformationType();
        entity1.setId(1);
        entity1.setSystemName("system1");
        entity1.setDescription("desc1");
        entity1.setDisplayName("display1");
        TransformTypeDTO type1 = new TransformTypeDTO(entity1);
        TransformationType entity2 = new TransformationType();
        entity2.setId(2);
        entity2.setSystemName("system2");
        entity2.setDescription("desc2");
        entity2.setDisplayName("display2");
        TransformTypeDTO type2 = new TransformTypeDTO(entity2);
        List<TransformTypeDTO> mockTypes = Arrays.asList(type1, type2);
        when(lookupTableService.getAllTransformTypes()).thenReturn(mockTypes);

        List<TransformTypeDTO> result = lookupTableController.getAllTransformTypes();
        assertEquals(mockTypes, result);
    }

    @Test
    void testGetTransformationFunctionsByTransformType() {
        String transformType = "type1";
        TransformationFunction entity1 = new TransformationFunction();
        entity1.setId(1);
        entity1.setSystemName("funcSystem1");
        entity1.setDescription("funcDesc1");
        entity1.setDisplayName("funcDisplay1");
        entity1.setOnesourceDomain(null);
        TransformationFunctionDTO func1 = new TransformationFunctionDTO(entity1);
        TransformationFunction entity2 = new TransformationFunction();
        entity2.setId(2);
        entity2.setSystemName("funcSystem2");
        entity2.setDescription("funcDesc2");
        entity2.setDisplayName("funcDisplay2");
        entity2.setOnesourceDomain(null);
        TransformationFunctionDTO func2 = new TransformationFunctionDTO(entity2);
        List<TransformationFunctionDTO> mockFunctions = Arrays.asList(func1, func2);
        when(lookupTableService.getTransformationFunctionsByTransformType(transformType)).thenReturn(mockFunctions);

        ResponseEntity<List<TransformationFunctionDTO>> response = lookupTableController.getTransformationFunctionsByTransformType(transformType);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockFunctions, response.getBody());
    }
}
