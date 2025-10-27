package com.thomsonreuters.dataconnect.dataintegration.services;

import com.thomsonreuters.dataconnect.dataintegration.configuration.ModelMapperConfig;
import com.thomsonreuters.dataconnect.dataintegration.dto.TransformTypeDTO;
import com.thomsonreuters.dataconnect.dataintegration.dto.TransformationFunctionDTO;
import com.thomsonreuters.dataconnect.dataintegration.dto.TransformationParamGetDTO;
import com.thomsonreuters.dataconnect.dataintegration.repository.TransformParamRepository;
import com.thomsonreuters.dataconnect.dataintegration.repository.TransformTypeRepository;
import com.thomsonreuters.dataconnect.dataintegration.repository.TransformationRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LookupTableService {
    private final TransformParamRepository transformParamRepository;
    private final TransformTypeRepository transformTypeRepository;
    private final ModelMapperConfig modelMapperConfig;
    private final TransformationRepository transformationRepository;

    public LookupTableService(TransformParamRepository transformParamRepository, ModelMapperConfig modelMapperConfig , TransformTypeRepository transformTypeRepository, TransformationRepository transformationRepository) {
        this.transformParamRepository = transformParamRepository;
        this.modelMapperConfig = modelMapperConfig;
        this.transformTypeRepository = transformTypeRepository;
        this.transformationRepository = transformationRepository;
    }

    public List<TransformationParamGetDTO> getTransformationFunctionParamsByTransformFuncId(String transformFuncId) {
        return transformParamRepository.findByTransformFuncId(transformFuncId)
                .stream()
                .map(param -> modelMapperConfig.modelMapper().map(param, TransformationParamGetDTO.class))
                .collect(Collectors.toList());
    }

    public List<TransformTypeDTO> getAllTransformTypes() {
        return transformTypeRepository.findAll().stream()
                .map(TransformTypeDTO::new)
                .collect(Collectors.toList());
    }

    public List<TransformationFunctionDTO> getTransformationFunctionsByTransformType(String transformType) {
        return transformationRepository.findByType(transformType)
                .stream()
                .map(TransformationFunctionDTO::new)
                .collect(Collectors.toList());
    }
}
