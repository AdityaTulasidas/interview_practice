package com.thomsonreuters.metadataregistry.controller;

import com.thomsonreuters.dep.api.spring.response.ApiCollection;
import com.thomsonreuters.metadataregistry.constants.Constants;
import com.thomsonreuters.metadataregistry.exceptionhandler.MetaDataRegistryException;
import com.thomsonreuters.metadataregistry.model.dto.DataSourceDTO;
import com.thomsonreuters.metadataregistry.model.dto.DataSourceUpdateDTO;
import com.thomsonreuters.metadataregistry.model.entity.DataSource;
import com.thomsonreuters.metadataregistry.service.DataSourceCatalogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DataSourceCatalogControllerTest {

    @Mock
    private DataSourceCatalogService catalogService;

    @Mock
    private BindingResult bindingResult;

    @InjectMocks
    private DataSourceCatalogController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldCreateDataSource_WhenValidInputProvided() throws MetaDataRegistryException {
        // Arrange
        DataSourceDTO dataSourceDTO = new DataSourceDTO();
        when(bindingResult.hasErrors()).thenReturn(false);
        when(catalogService.saveDataSource(dataSourceDTO)).thenReturn(new DataSource());

        // Act
        ResponseEntity<Object> response = controller.createDataSource(dataSourceDTO, bindingResult);

        // Assert
        assertNotNull(response);
        assertEquals(201, response.getStatusCode().value());
        verify(catalogService, times(1)).saveDataSource(dataSourceDTO);
    }

    @Test
    void shouldThrowException_WhenValidationFailsOnCreate() {
        // Arrange
        DataSourceDTO dataSourceDTO = new DataSourceDTO();
        when(bindingResult.hasErrors()).thenReturn(true);

        // Act & Assert
        MetaDataRegistryException exception = assertThrows(MetaDataRegistryException.class,
                () -> controller.createDataSource(dataSourceDTO, bindingResult));
        assertEquals(Constants.VALIDATION_ERROR, exception.getMessage());
    }

    @Test
    void shouldReturnDataSource_WhenGetByIdIsCalled() throws MetaDataRegistryException {
        // Arrange
        UUID id = UUID.randomUUID();
        DataSourceDTO dataSourceDTO = new DataSourceDTO();
        when(catalogService.getDataSourceById(id)).thenReturn(Optional.of(dataSourceDTO));

        // Act
        ResponseEntity<Object> response = controller.getDataSourceById(id);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(dataSourceDTO, response.getBody());
        verify(catalogService, times(1)).getDataSourceById(id);
    }

    @Test
    void shouldThrowException_WhenDataSourceNotFoundById() {
        // Arrange
        UUID id = UUID.randomUUID();
        when(catalogService.getDataSourceById(id)).thenReturn(Optional.empty());

        // Act & Assert
        MetaDataRegistryException exception = assertThrows(MetaDataRegistryException.class,
                () -> controller.getDataSourceById(id));
        assertEquals(Constants.DATA_SOURCE_NOT_FOUND, exception.getMessage());
    }

    @Test
    void shouldUpdateDataSource_WhenValidInputProvided() throws MetaDataRegistryException {
        // Arrange
        UUID id = UUID.randomUUID();
        DataSourceUpdateDTO dataSourceDTO = new DataSourceUpdateDTO();
        when(bindingResult.hasErrors()).thenReturn(false);
        when(catalogService.updateDataSource(id, dataSourceDTO)).thenReturn(Optional.of(dataSourceDTO));

        // Act
        ResponseEntity<Object> response = controller.updateDataSource(id, dataSourceDTO, bindingResult);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        verify(catalogService, times(1)).updateDataSource(id, dataSourceDTO);
    }

    @Test
    void shouldThrowException_WhenValidationFailsOnUpdate() {
        // Arrange
        UUID id = UUID.randomUUID();
        DataSourceUpdateDTO dataSourceDTO = new DataSourceUpdateDTO();
        when(bindingResult.hasErrors()).thenReturn(true);

        // Act & Assert
        ResponseEntity<Object> response = controller.updateDataSource(id, dataSourceDTO, bindingResult);
        assertEquals(400, response.getStatusCode().value());
    }

    @Test
    void shouldThrowException_WhenDataSourceNotFoundOnUpdate() {
        // Arrange
        UUID id = UUID.randomUUID();
        DataSourceUpdateDTO dataSourceDTO = new DataSourceUpdateDTO();
        when(bindingResult.hasErrors()).thenReturn(false);
        when(catalogService.updateDataSource(id, dataSourceDTO)).thenReturn(Optional.empty());

        // Act & Assert
        MetaDataRegistryException exception = assertThrows(MetaDataRegistryException.class,
                () -> controller.updateDataSource(id, dataSourceDTO, bindingResult));
        assertEquals(Constants.DATA_SOURCE_NOT_FOUND, exception.getMessage());
    }
    @Test
    void testSearchDataSources_success() {
        ApiCollection<DataSource> mockCollection = mock(ApiCollection.class);
        when(mockCollection.getItems()).thenReturn(List.of(new DataSource()));

        ResponseEntity<?> mockResponse = ResponseEntity.ok(mockCollection);
        when(catalogService.dataSourceSearch(0, 10, null, null)).thenReturn((ResponseEntity) mockResponse);

        ResponseEntity<?> response = controller.searchDataSources(0, 10, null, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockCollection, response.getBody());
    }

    @Test
    void testSearchDataSources_invalidOffset() {
        MetaDataRegistryException exception = assertThrows(
                MetaDataRegistryException.class,
                () -> controller.searchDataSources(-1, 10, null, null)
        );
        assertEquals("Offset must be greater than or equal to 0.", exception.getMessage());
    }

    @Test
    void testSearchDataSources_invalidLimit() {
        MetaDataRegistryException exception = assertThrows(
                MetaDataRegistryException.class,
                () -> controller.searchDataSources(0, 0, null, null)
        );
        assertEquals("Limit must be greater than 0.", exception.getMessage());
    }

    @Test
    void testSearchDataSources_nullParams() {
        ApiCollection<DataSource> mockCollection = mock(ApiCollection.class);
        ResponseEntity<?> mockResponse = ResponseEntity.ok(mockCollection);
        when(catalogService.dataSourceSearch(0, 200, null, null)).thenReturn((ResponseEntity) mockResponse);

        ResponseEntity<?> response = controller.searchDataSources(null, null, null, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockCollection, response.getBody());
    }

}