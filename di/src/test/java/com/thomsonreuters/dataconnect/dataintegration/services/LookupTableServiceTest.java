package com.thomsonreuters.dataconnect.dataintegration.services;

import com.thomsonreuters.dataconnect.dataintegration.configuration.ModelMapperConfig;
import com.thomsonreuters.dataconnect.dataintegration.dto.TransformationFunctionDTO;
import com.thomsonreuters.dataconnect.dataintegration.dto.TransformationParamGetDTO;
import com.thomsonreuters.dataconnect.dataintegration.dto.TransformTypeDTO;
import com.thomsonreuters.dataconnect.dataintegration.model.entity.TransformationFunction;
import com.thomsonreuters.dataconnect.dataintegration.model.entity.TransformationFunctionParam;
import com.thomsonreuters.dataconnect.dataintegration.model.entity.TransformationType;
import com.thomsonreuters.dataconnect.dataintegration.repository.TransformParamRepository;
import com.thomsonreuters.dataconnect.dataintegration.repository.TransformTypeRepository;
import com.thomsonreuters.dataconnect.dataintegration.repository.TransformationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.modelmapper.ModelMapper;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LookupTableServiceTest {
    @Mock
    private TransformParamRepository transformParamRepository;
    @Mock
    private ModelMapperConfig modelMapperConfig;
    @Mock
    private TransformTypeRepository transformTypeRepository;
    @Mock
    private TransformationRepository transformationRepository;

    @InjectMocks
    private LookupTableService lookupTableService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ModelMapper modelMapper = mock(ModelMapper.class);
        when(modelMapperConfig.modelMapper()).thenReturn(modelMapper);
    }

    @Test
    void testGetTransformationFunctionParamsByTransformFuncId() {
        TransformationFunctionParam param1 = new TransformationFunctionParam();
        param1.setId(1);
        TransformationFunctionParam param2 = new TransformationFunctionParam();
        param2.setId(2);
        List<TransformationFunctionParam> params = Arrays.asList(param1, param2);
        when(transformParamRepository.findByTransformFuncId("func1")).thenReturn(params);
        TransformationParamGetDTO dto1 = new TransformationParamGetDTO();
        TransformationParamGetDTO dto2 = new TransformationParamGetDTO();
        when(modelMapperConfig.modelMapper().map(param1, TransformationParamGetDTO.class)).thenReturn(dto1);
        when(modelMapperConfig.modelMapper().map(param2, TransformationParamGetDTO.class)).thenReturn(dto2);

        List<TransformationParamGetDTO> result = lookupTableService.getTransformationFunctionParamsByTransformFuncId("func1");
        assertEquals(2, result.size());
        assertTrue(result.contains(dto1));
        assertTrue(result.contains(dto2));
    }

    @Test
    void testGetAllTransformTypes() {
        TransformationType type1 = new TransformationType();
        type1.setId(1);
        TransformationType type2 = new TransformationType();
        type2.setId(2);
        List<TransformationType> types = Arrays.asList(type1, type2);
        when(transformTypeRepository.findAll()).thenReturn(types);
        TransformTypeDTO dto1 = new TransformTypeDTO(type1);
        TransformTypeDTO dto2 = new TransformTypeDTO(type2);
        // No need to mock constructor mapping
        List<TransformTypeDTO> result = lookupTableService.getAllTransformTypes();
        assertEquals(2, result.size());
        assertEquals(type1.getId(), result.get(0).getId());
        assertEquals(type2.getId(), result.get(1).getId());
    }

    @Test
    void testGetTransformationFunctionsByTransformType() {
        TransformationFunction func1 = new TransformationFunction();
        func1.setId(1);
        TransformationFunction func2 = new TransformationFunction();
        func2.setId(2);
        List<TransformationFunction> functions = Arrays.asList(func1, func2);
        when(transformationRepository.findByType("typeA")).thenReturn(functions);
        TransformationFunctionDTO dto1 = new TransformationFunctionDTO(func1);
        TransformationFunctionDTO dto2 = new TransformationFunctionDTO(func2);
        // No need to mock constructor mapping
        List<TransformationFunctionDTO> result = lookupTableService.getTransformationFunctionsByTransformType("typeA");
        assertEquals(2, result.size());
        assertEquals(func1.getId(), result.get(0).getId());
        assertEquals(func2.getId(), result.get(1).getId());
    }
}
