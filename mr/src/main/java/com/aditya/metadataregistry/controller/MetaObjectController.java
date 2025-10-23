package com.thomsonreuters.metadataregistry.controller;

import com.thomsonreuters.metadataregistry.constants.Constants;


import com.thomsonreuters.dep.api.spring.annotations.Filter;
import com.thomsonreuters.dep.api.spring.annotations.Limit;
import com.thomsonreuters.dep.api.spring.annotations.Offset;
import com.thomsonreuters.dep.api.spring.annotations.Sort;
import com.thomsonreuters.metadataregistry.exceptionhandler.MetaDataRegistryException;
import com.thomsonreuters.metadataregistry.model.dto.*;
import com.thomsonreuters.metadataregistry.model.entity.MetaObject;
import com.thomsonreuters.metadataregistry.service.MetaObjectService;
import com.thomsonreuters.metadataregistry.utils.CommonUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/meta-objects")
@Slf4j
public class MetaObjectController {

    private final MetaObjectService metaObjectService;



    @Autowired
    public MetaObjectController(MetaObjectService metaObjectService) {
        this.metaObjectService = metaObjectService;
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = Constants.METAOBJECT_SUCESS_DECRIPTION,
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class))}),
            @ApiResponse(responseCode = "400", description = Constants.METAOBJECT_CREATION_ERROR,
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = Constants.METAOBJECT_CREATION_ERROR))}),
            @ApiResponse(responseCode = "500", description = Constants.METAOBJECT_CREATION_ERROR,
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class), examples = @ExampleObject(value = Constants.METAOBJECT_CREATION_ERROR))}),
            @ApiResponse(responseCode = "409", description = Constants.METAOBJECT_CONFLICT,
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class), examples = @ExampleObject(value = Constants.METAOBJECT_CONFLICT))})

    })

    @Operation(description = "Creates a new MetaObject")
    @PostMapping(consumes = "application/json")
    public ResponseEntity<Object> createMetaObject(@Valid @RequestBody MetaObjectPostDTO metaObject, BindingResult bindingResult) throws MetaDataRegistryException {

        Logger log = org.slf4j.LoggerFactory.getLogger(MetaObjectController.class);
        log.info("Creating MetaObject with name: {}", metaObject.getSystemName());
        if (bindingResult.hasErrors()) {
            log.error("Validation errors occurred while creating MetaObject: {}", bindingResult.getAllErrors());
            throw new MetaDataRegistryException(Constants.VALIDATION_ERROR, Constants.INVALID_REQUEST);
        }
        MetaObjectDTO createdMetaObject = metaObjectService.createMetaObject(metaObject);
        return new ResponseEntity<>(CommonUtils.generateResponse(String.valueOf(createdMetaObject.getId()), Constants.METAOBJECT_CREATION_SUCCESSFUL), HttpStatus.CREATED);

    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = Constants.METAOBJECT_SUCESS_DESCRIPTION,
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = MetaObject.class))}),
            @ApiResponse(responseCode = "404", description = Constants.METAOBJECT_NOT_FOUND,
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class), examples = @ExampleObject(value = Constants.METAOBJECT_RETRIEVAL_ERROR))}),
            @ApiResponse(responseCode = "500", description = Constants.METAOBJECT_RETRIEVAL_ERROR)
    })
    @Operation(description = "Gets a MetaObject by MetaObject ID")
    @GetMapping("/{id}")
    public ResponseEntity<Object> getMetaObjectById(@PathVariable UUID id) throws MetaDataRegistryException {

        MetaObjectDTO metaObject = metaObjectService.getMetaObjectById(id);
        return new ResponseEntity<>(metaObject, HttpStatus.OK);

    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = Constants.METAOBJECT_UPDATE_SUCCEFUL,
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class))}),
            @ApiResponse(responseCode = "404", description = Constants.METAOBJECT_NOT_FOUND,
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class), examples = @ExampleObject(value = Constants.METAOBJECT_UPDATE_ERROR_DESCRIPTIN))}),
            @ApiResponse(responseCode = "500", description = Constants.METAOBJECT_UPDATE_ERROR),
            @ApiResponse(responseCode = "400", description = Constants.VALIDATION_ERROR,
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = Constants.VALIDATION_ERROR))}),
            @ApiResponse(responseCode = "409", description = Constants.METAOBJECT_CONFLICT,
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class), examples = @ExampleObject(value = Constants.METAOBJECT_CONFLICT))})
    })
    @Operation(description = "Updates a MetaObject using its ID. " +
            "1. To modify an existing attribute, include the existing system_name. " +
            "2. Otherwise a new attribute will be created based on the given system_name " +
            "3. system_name of attribute cannot be skipped.")
    @PutMapping("/{id}")
    public ResponseEntity<String> updateMetaObject(@PathVariable UUID id, @Valid @RequestBody MetaObjectPutDTO metaObject, BindingResult bindingResult) throws MetaDataRegistryException {

        if (bindingResult.hasErrors()) {
            throw new MetaDataRegistryException(Constants.VALIDATION_ERROR, Constants.INVALID_REQUEST);
        }
        MetaObjectDTO updatedMetaObject = metaObjectService.updateMetaObjects(id, metaObject);
        return new ResponseEntity<>(Constants.METAOBJECT_UPDATE_SUCCEFUL + updatedMetaObject.getId(), HttpStatus.OK);
    }


    @Operation(summary = "Search meta objects", description = "Search for meta objects based on various parameters")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = Constants.META_OBJECT_RETRIVAL_SUCCESSFULL,
                    content = {@Content(mediaType = Constants.API_HEADER_CONTENT_JSON,
                            schema = @Schema(implementation = String.class))}),
            @ApiResponse(responseCode = "400", description = Constants.API_HEADER_BAD_REQUEST,
                    content = {@Content(mediaType = Constants.API_HEADER_CONTENT_JSON,
                            schema = @Schema(implementation = String.class), examples = @ExampleObject(value = "Invalid search parameters"))}),
            @ApiResponse(responseCode = "404", description = Constants.META_OBJECT_NOT_FOUND,
                    content = {@Content(mediaType = Constants.API_HEADER_CONTENT_JSON,
                            schema = @Schema(implementation = String.class), examples = @ExampleObject(value = Constants.NO_DATA_SOURCE_ENTRIES_FOUND))}),
            @ApiResponse(responseCode = "500", description = Constants.API_HEADER_SERVER_ERROR,
                    content = {@Content(mediaType = Constants.API_HEADER_CONTENT_JSON,
                            schema = @Schema(implementation = String.class), examples = @ExampleObject(value = "Error while processing the request"))})
    })

    @GetMapping
    public ResponseEntity<?> searchMetaObjects(
            @Offset Integer offset,
            @Limit Integer limit,
            @Sort String sort,
            @Filter String filter
    ) {

        // Validate offset and limit parameters
        // Validate pagination and sorting parameters
        if (offset != null && offset < 0) {
            throw new MetaDataRegistryException("Offset must be greater than or equal to 0.", "INVALID_REQUEST");
        }
        if (limit != null && limit <= 0) {
            throw new MetaDataRegistryException("Limit must be greater than 0.", "INVALID_REQUEST");
        }

        int page = offset != null ? offset : 0;
        int size = limit != null ? limit : 200;
        return metaObjectService.getAllMetaObjects(page, size, sort, filter);
    }

    @Operation(summary = "Update usage counter for a MetaObject", description = "Updates the usage counter for a specific MetaObject by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usage counter updated successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Map.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class), examples = @ExampleObject(value = "Invalid usage counter value"))}),
            @ApiResponse(responseCode = "404", description = "MetaObject not found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class), examples = @ExampleObject(value = "MetaObject not found"))})
    })

    @PatchMapping("/usage/{id}")
    public ResponseEntity<?> updateUsageCounter(
            @PathVariable("id") String id,
            @RequestBody Map<String, Object> payload) {
        // Validate payload and perform update
        if (!payload.containsKey("value") || !payload.containsKey("relative")) {
        throw new MetaDataRegistryException("Invalid payload structure.", "INVALID_REQUEST");
       }

         int value = (int) payload.get("value");
        boolean relative = (boolean) payload.get("relative");

    // Perform update
    int updatedCount = relative
            ? metaObjectService.updateUsageCounter(id, value > 0 ? 1 : -1)
            : metaObjectService.updateUsageCounterAbsolute(id, value);

    return ResponseEntity.ok().body(Map.of("status", "Success", "updated_usage_count", updatedCount));
    }

}