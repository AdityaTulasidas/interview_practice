package com.thomsonreuters.dataconnect.dataintegration.controllers;


import com.thomsonreuters.dataconnect.dataintegration.dto.TransformationDTO;
import com.thomsonreuters.dataconnect.dataintegration.exceptionhandler.DataSyncJobException;
import com.thomsonreuters.dataconnect.dataintegration.services.TransformationService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/transformation")
public class TransformationController {

    private final TransformationService transformationService;
    public TransformationController(TransformationService transformationService){

        this.transformationService = transformationService;
    }

    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successful response with transformation details",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = TransformationDTO.class),
                    examples = @ExampleObject(
                            value = "{\n  \"id\": 123,\n  \"system_name\": \"cd.replace_value\",\n  \"description\": \"Replace_value_transformation\",\n  \"display_name\": \"Replace Value\",\n  \"transform_type\": \"REPLACE\",\n  \"onesource_domain\": \"SAMPLE_DOMAIN\",\n  \"parameters\": [\n    {\n      \"param_name\": \"replace_value\",\n      \"param_value\": \"new_value\"\n    }\n  ]\n}"
                    )
            )
        )
    })
    @GetMapping("{system_name}")
    public ResponseEntity<Object> getTransformations(@PathVariable("system_name") String name) throws DataSyncJobException {
        TransformationDTO builtinTransformation=transformationService.getBuiltinTransformationById(name);
        return ResponseEntity.ok(builtinTransformation);
    }
}