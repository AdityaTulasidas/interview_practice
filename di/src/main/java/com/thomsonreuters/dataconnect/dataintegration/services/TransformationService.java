package com.thomsonreuters.dataconnect.dataintegration.services;

import com.thomsonreuters.dataconnect.dataintegration.configuration.ModelMapperConfig;
import com.thomsonreuters.dataconnect.dataintegration.dto.TransformationDTO;
import com.thomsonreuters.dataconnect.dataintegration.dto.TransformationFunctionParamDTO;
import com.thomsonreuters.dataconnect.dataintegration.exceptionhandler.DataSyncJobException;
import com.thomsonreuters.dataconnect.dataintegration.model.entity.TransformationFunction;
import com.thomsonreuters.dataconnect.dataintegration.model.entity.TransformationFunctionParam;
import com.thomsonreuters.dataconnect.dataintegration.repository.TransformParamRepository;
import com.thomsonreuters.dataconnect.dataintegration.repository.TransformationRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TransformationService {

    private final ModelMapperConfig modelMapperConfig;

    private final TransformationRepository transformationRepository;

    private final TransformParamRepository transformParamRepository;

    public TransformationService(ModelMapperConfig modelMapperConfig, TransformationRepository transformationRepository, TransformParamRepository transformParamRepository) {
        this.modelMapperConfig = modelMapperConfig;
        this.transformationRepository = transformationRepository;
        this.transformParamRepository = transformParamRepository;
    }

    public TransformationDTO getBuiltinTransformationById(String name) throws DataSyncJobException {
        TransformationFunction transformation = transformationRepository.findBySystemName(name)
                .orElseThrow(() -> new DataSyncJobException("Builtin Transformation not found with id: " + name, "NOT_FOUND"));
        TransformationDTO transformationDTO = modelMapperConfig.modelMapper().map(transformation, TransformationDTO.class);
        List<TransformationFunctionParam> param = transformParamRepository.findByTransformFuncId(name);
        List<TransformationFunctionParamDTO> param1 = new ArrayList<>();
        if (!param.isEmpty()) {
            for (TransformationFunctionParam p : param) {
                param1.add(modelMapperConfig.modelMapper().map(p, TransformationFunctionParamDTO.class));

            }
            transformationDTO.setBuiltinTransformationParam(param1);
        }else {
            transformationDTO.setBuiltinTransformationParam(new ArrayList<>());
        }
        return transformationDTO;
    }
}