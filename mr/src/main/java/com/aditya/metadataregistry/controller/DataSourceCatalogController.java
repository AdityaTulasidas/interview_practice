package com.thomsonreuters.metadataregistry.controller;

import com.thomsonreuters.dep.api.spring.annotations.Filter;
import com.thomsonreuters.dep.api.spring.annotations.Limit;
import com.thomsonreuters.dep.api.spring.annotations.Offset;
import com.thomsonreuters.dep.api.spring.annotations.Sort;
import com.thomsonreuters.metadataregistry.constants.Constants;
import com.thomsonreuters.metadataregistry.exceptionhandler.MetaDataRegistryException;
import com.thomsonreuters.metadataregistry.model.dto.DataSourceDTO;
import com.thomsonreuters.metadataregistry.model.dto.DataSourceUpdateDTO;
import com.thomsonreuters.metadataregistry.model.entity.DataSource;
import com.thomsonreuters.metadataregistry.service.DataSourceCatalogService;
import com.thomsonreuters.metadataregistry.utils.ApiCollectionResponse;
import com.thomsonreuters.metadataregistry.utils.CommonUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/datasources")
public class DataSourceCatalogController {


    private final DataSourceCatalogService catalogService;
    @Autowired
    public DataSourceCatalogController(DataSourceCatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @Operation(summary = "create data source", description = Constants.API_HEADER_CREATE)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = Constants.API_HEADER_SUCCESS,
                    content = {@Content(mediaType = Constants.API_HEADER_CONTENT_JSON,
                            schema = @Schema(implementation = String.class))}),
            @ApiResponse(responseCode = "400", description = Constants.VALIDATION_ERROR,
                    content = {@Content(mediaType = Constants.API_HEADER_CONTENT_JSON,
                            schema = @Schema(implementation = String.class))}),
            @ApiResponse(responseCode = "409", description = Constants.DATA_SOURCE_ALREADY_EXISTS,
                    content = {@Content(mediaType = Constants.API_HEADER_CONTENT_JSON, schema = @Schema(implementation = String.class), examples = @ExampleObject(value = Constants.DATA_SOURCE_ALREADY_EXISTS))}),
            @ApiResponse(responseCode = "500", description = Constants.API_HEADER_SERVER_ERROR,
                    content = {@Content(mediaType = Constants.API_HEADER_CONTENT_JSON,
                            schema = @Schema(implementation = String.class))})
    })
    @PostMapping(consumes = "application/json")
    public ResponseEntity<Object> createDataSource(@Valid @RequestBody DataSourceDTO dataSourceDTO,BindingResult bindingResult) throws MetaDataRegistryException {

        if (bindingResult.hasErrors()) {
            throw new MetaDataRegistryException(Constants.VALIDATION_ERROR, "INVALID_REQUEST");
        }
        catalogService.trimDataSource(dataSourceDTO);
        DataSource dataSource = catalogService.saveDataSource(dataSourceDTO);
        return new ResponseEntity<>(CommonUtils.generateResponse(String.valueOf(dataSource.getId()), Constants.DATA_SOURCE_CREATED_SUCCESSFULLY), HttpStatus.CREATED);
    }

    @Operation(summary = "get data source by Id", description = Constants.API_HEADER_GET_BY_ID)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = Constants.API_HEADER_RETRIEVAL_SUCCESS,
                    content = {@Content(mediaType = Constants.API_HEADER_CONTENT_JSON,
                            schema = @Schema(implementation = DataSource.class))}),
            @ApiResponse(responseCode = "204", description = Constants.API_HEADER_NO_CONTENT),
            @ApiResponse(responseCode = "400", description = Constants.API_HEADER_INVALID_REQUEST,
                    content = {@Content(mediaType = Constants.API_HEADER_CONTENT_JSON,
                            schema = @Schema(implementation = String.class), examples = @ExampleObject(value = Constants.API_HEADER_INVALID_ID_REQUEST))}),
            @ApiResponse(responseCode = "404", description = Constants.API_HEADER_NOT_FOUND,
                    content = {@Content(mediaType = Constants.API_HEADER_CONTENT_JSON,
                            schema = @Schema(implementation = String.class), examples = @ExampleObject(value = Constants.DATA_SOURCE_NOT_FOUND))}),
            @ApiResponse(responseCode = "500", description = Constants.API_HEADER_SERVER_ERROR,
                    content = {@Content(mediaType = Constants.API_HEADER_CONTENT_JSON,
                            schema = @Schema(implementation = String.class), examples = @ExampleObject(value = Constants.ERROR_RETRIEVING_DATA_SOURCE_ENTRIES))})
    })
    @GetMapping("/{id}")
    public ResponseEntity<Object> getDataSourceById(@PathVariable UUID id) throws MetaDataRegistryException{
        if (id == null || StringUtils.isBlank(id.toString())) {
            throw new MetaDataRegistryException(Constants.DATA_STORE_ID_CANNOT_BE_NULL, Constants.BAD_REQUEST);
        }
        Optional<DataSourceDTO> dataSourceCatalogDto = catalogService.getDataSourceById(id);
        return dataSourceCatalogDto.map(catalog -> ResponseEntity.ok((Object) catalog))
                .orElseThrow(() ->
                        new MetaDataRegistryException(Constants.DATA_SOURCE_NOT_FOUND, Constants.NOT_FOUND));
    }

    @Operation(summary = "update data source", description = "Update all data source catalog entries")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = Constants.DATA_SOURCE_UPDATED_SUCCESSFULLY,
                    content = {@Content(mediaType = Constants.API_HEADER_CONTENT_JSON,
                            schema = @Schema(implementation = DataSource.class))}),
            @ApiResponse(responseCode = "204", description = Constants.API_HEADER_NO_CONTENT),
            @ApiResponse(responseCode = "400", description = Constants.API_HEADER_BAD_REQUEST,
                    content = {@Content(mediaType = Constants.API_HEADER_CONTENT_JSON,
                            schema = @Schema(implementation = String.class), examples = @ExampleObject(value = "DataStoreId and DataSourceCatalogDTO cannot be null"))}),
            @ApiResponse(responseCode = "404", description = Constants.API_HEADER_NOT_FOUND,
                    content = {@Content(mediaType = Constants.API_HEADER_CONTENT_JSON,
                            schema = @Schema(implementation = String.class), examples = @ExampleObject(value = Constants.DATA_SOURCE_NOT_FOUND))}),
            @ApiResponse(responseCode = "409", description = Constants.DATA_SOURCE_ID_ALREADY_EXISTS,
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class), examples = @ExampleObject(value = Constants.DATA_SOURCE_ID_ALREADY_EXISTS))}),
            @ApiResponse(responseCode = "500", description = Constants.API_HEADER_SERVER_ERROR,
                    content = {@Content(mediaType = Constants.API_HEADER_CONTENT_JSON,
                            schema = @Schema(implementation = String.class), examples = @ExampleObject(value = Constants.ERROR_UPDATING_DATA_SOURCE))})
    })
    @PutMapping(path = "/{id}", consumes = Constants.API_HEADER_CONTENT_JSON, produces = Constants.API_HEADER_CONTENT_JSON)
    public ResponseEntity<Object> updateDataSource(@PathVariable UUID id, @Valid @RequestBody DataSourceUpdateDTO dataSourceDTO, BindingResult bindingResult) throws MetaDataRegistryException{
        if (bindingResult.hasErrors()) {
            Map<String, String> errorMessage = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error ->
                    errorMessage.put(error.getField(), error.getDefaultMessage()));
            return ResponseEntity.badRequest().body(errorMessage);
        }
        catalogService.trimDataSource(dataSourceDTO);

        Optional<DataSourceUpdateDTO> updatedDataSourceDTO = catalogService.updateDataSource(id, dataSourceDTO);
        if (updatedDataSourceDTO.isEmpty()) {
            throw new MetaDataRegistryException(Constants.DATA_SOURCE_NOT_FOUND, Constants.CUSTOM_MESSAGE);
        }
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.TEXT_PLAIN)
                .body(Constants.DATA_SOURCE_UPDATED_SUCCESSFULLY);
    }

    @Operation(summary = "Search data sources", description = "Search for data sources based on various parameters")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = Constants.DATA_SOURCE_RETRIVAL_SUCCESSFULLY,
                    content = {@Content(mediaType = Constants.API_HEADER_CONTENT_JSON,
                            schema = @Schema(implementation = ApiCollectionResponse.class))}),
            @ApiResponse(responseCode = "400", description = Constants.API_HEADER_BAD_REQUEST,
                    content = {@Content(mediaType = Constants.API_HEADER_CONTENT_JSON,
                            schema = @Schema(implementation = String.class), examples = @ExampleObject(value = "Invalid search parameters"))}),
            @ApiResponse(responseCode = "404", description = Constants.API_HEADER_NOT_FOUND,
                    content = {@Content(mediaType = Constants.API_HEADER_CONTENT_JSON,
                            schema = @Schema(implementation = String.class), examples = @ExampleObject(value = Constants.NO_DATA_SOURCE_ENTRIES_FOUND))}),
            @ApiResponse(responseCode = "500", description = Constants.API_HEADER_SERVER_ERROR,
                    content = {@Content(mediaType = Constants.API_HEADER_CONTENT_JSON,
                            schema = @Schema(implementation = String.class), examples = @ExampleObject(value = "Error while processing the request"))})
    })

    @GetMapping
    public ResponseEntity<?> searchDataSources(
            @Offset Integer offset,
            @Limit Integer limit,
            @Sort String sort,
            @Filter String filter
    ) {

        // Validate pagination and sorting parameters
        if (offset != null && offset < 0) {
            throw new MetaDataRegistryException("Offset must be greater than or equal to 0.", "INVALID_REQUEST");
        }
        if (limit != null && limit <= 0) {
            throw new MetaDataRegistryException("Limit must be greater than 0.", "INVALID_REQUEST");
        }
        // Validate and process pagination and sorting
        int page = offset != null ? offset : 0;
        int size = limit != null ? limit : 200;
        return catalogService.dataSourceSearch(page, size, sort, filter);
    }


    /**
     * Get DataSource based on recursive filtering of up to 4 fields.
     * If no match is found with all fields, it tries combinations of 3, then 2.
     */
    @GetMapping("/filter")
    public ResponseEntity<DataSource> getFilteredDataSource(
            @RequestParam(required = false) String regionalTenantId,
            @RequestParam(required = false) String domain,
            @RequestParam(required = false) String onesourceRegion,
            @RequestParam(required = false) String domainObjectSysName) {

        DataSource result = catalogService.getFilteredDataSource(
                regionalTenantId, domain, onesourceRegion, domainObjectSysName);

        if (result != null) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

}