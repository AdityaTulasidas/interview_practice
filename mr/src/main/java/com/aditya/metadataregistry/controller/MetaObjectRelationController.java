package com.thomsonreuters.metadataregistry.controller;

import com.thomsonreuters.metadataregistry.constants.Constants;

import com.thomsonreuters.metadataregistry.exceptionhandler.MetaDataRegistryException;
import com.thomsonreuters.metadataregistry.model.dto.MetaObjectRelationDTO;
import com.thomsonreuters.metadataregistry.model.dto.MetaRelationMetaModelDTO;
import com.thomsonreuters.metadataregistry.model.entity.MetaObjectRelation;
import com.thomsonreuters.metadataregistry.service.MetaObjectRelationService;
import com.thomsonreuters.metadataregistry.service.MetaObjectService;
import com.thomsonreuters.metadataregistry.utils.CommonUtils;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/meta-object-relations")
public class MetaObjectRelationController {

    private final MetaObjectService metaObjectService;
    private final MetaObjectRelationService metaObjectRelationService;
    @Autowired
    public MetaObjectRelationController(MetaObjectService metaObjectService, MetaObjectRelationService metaObjectRelationService) {
        this.metaObjectService = metaObjectService;
        this.metaObjectRelationService = metaObjectRelationService;
    }

    @ApiResponse(responseCode = "201", content = @Content(schema = @Schema(implementation = MetaObjectRelation.class)), description = "MetaObject Relation retrieved successfully")
    @ApiResponse(responseCode = "400", content = @Content(mediaType = "appcation/json",
            examples = @ExampleObject(value = "Error retrieving MetaObject Relation")),description = Constants.METAOBJECTRELATION_RETRIEVAL_ERROR)
    @ApiResponse(responseCode = "500", content = @Content(mediaType = "application/json",
            examples = @ExampleObject(value = Constants.METAOBJECTRELATION_CREATION_ERROR)),description = Constants.METAOBJECTRELATION_SERVER_ERROR_DESCRIPTION)
    @PostMapping( consumes = "application/json",produces = "application/json")

    public ResponseEntity<Object> createMetaObjectRelation(@Valid @RequestBody MetaObjectRelationDTO metaObjectRelationdto, BindingResult bindingResult) throws MetaDataRegistryException {
        if(bindingResult.hasErrors()) {
            throw new MetaDataRegistryException(Constants.VALIDATION_ERROR, "INVALID_REQUEST");
        }
        String id = metaObjectRelationService.createMetaObjectRelationService(metaObjectRelationdto);
        return new ResponseEntity<>(CommonUtils.generateResponse(id,Constants.METAOBJECTRELATION_CREATION_SUCCESSFUL), HttpStatus.CREATED);
        }
    @GetMapping(value = "/meta-model/{id}", produces = "application/json")
    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = MetaObjectRelationDTO.class)), description = Constants.METARELATION_SUCESS)
    @ApiResponse(responseCode = "404", content = @Content(
            examples = @ExampleObject(value = Constants.METAOBJECT_CREATION_ERROR)), description = Constants.METADATAMANAGER_RETRIEVAL_ERROR)

    @ApiResponse(responseCode = "500", content = @Content(mediaType = "application/json",
            examples = @ExampleObject(value = Constants.METAOBJECT_CREATION_ERROR)), description = Constants.METADATAMANAGER_SERVER_ERROR_DESCRIPTION)
    public ResponseEntity<Object> getMetaModel(@PathVariable("id") UUID metaObjectId) throws MetaDataRegistryException {

        MetaRelationMetaModelDTO metaObjectRelationDTO = metaObjectService.getMetaModel(metaObjectId);
        return ResponseEntity.ok(metaObjectRelationDTO);

    }


}
