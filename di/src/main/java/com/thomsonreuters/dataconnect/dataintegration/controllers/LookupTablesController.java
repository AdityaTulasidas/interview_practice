package com.thomsonreuters.dataconnect.dataintegration.controllers;

import com.thomsonreuters.dataconnect.dataintegration.dto.TransformTypeDTO;
import com.thomsonreuters.dataconnect.dataintegration.dto.TransformationFunctionDTO;
import com.thomsonreuters.dataconnect.dataintegration.dto.TransformationParamGetDTO;
import com.thomsonreuters.dataconnect.dataintegration.services.LookupTableService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/lookup-tables")
public class LookupTablesController {

    private final LookupTableService lookupTableService;

    public LookupTablesController(LookupTableService lookupTableService) {
        this.lookupTableService = lookupTableService;
    }

    @GetMapping("/transformation-function-params/{transform_func_sys_name}")
    public ResponseEntity<List<TransformationParamGetDTO>> getTransformationFunctionParamsByTransformFuncId(@PathVariable("transform_func_sys_name") String transformFuncId) {
        List<TransformationParamGetDTO> params = lookupTableService.getTransformationFunctionParamsByTransformFuncId(transformFuncId);
        return ResponseEntity.ok(params);
    }
    @GetMapping("/transform-types")
    public List<TransformTypeDTO> getAllTransformTypes() {

        return lookupTableService.getAllTransformTypes();
    }
    @GetMapping("/transformation-functions/{transform_type}")
    public ResponseEntity<List<TransformationFunctionDTO>> getTransformationFunctionsByTransformType(@PathVariable("transform_type") String transformType) {
        List<TransformationFunctionDTO> functions = lookupTableService.getTransformationFunctionsByTransformType(transformType);
        return ResponseEntity.ok(functions);
    }
}
