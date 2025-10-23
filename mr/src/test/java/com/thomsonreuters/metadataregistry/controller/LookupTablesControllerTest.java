package com.thomsonreuters.metadataregistry.controller;

import com.thomsonreuters.metadataregistry.model.dto.DomainDTO;
import com.thomsonreuters.metadataregistry.model.dto.DomainObjectDTO;
import com.thomsonreuters.metadataregistry.model.dto.OnesourceRegionDTO;
import com.thomsonreuters.metadataregistry.model.dto.OnesourceDatabaseTypeDTO;
import com.thomsonreuters.metadataregistry.service.LookupTablesService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class LookupTablesControllerTest {
    @Mock
    private LookupTablesService lookupTablesService;

    @InjectMocks
    private LookupTablesController lookupTablesController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllOnesourceDatabaseTypesDirect() {
        OnesourceDatabaseTypeDTO dto = new OnesourceDatabaseTypeDTO();
        dto.setDbType("Oracle");
        dto.setJdbcDriver("oracle.jdbc.OracleDriver");
        dto.setDefaultPort(1521);
        dto.setJdbcTemplate("jdbc:oracle:thin:@//{host}:{port}/{service}");
        List<OnesourceDatabaseTypeDTO> dtos = Arrays.asList(dto);
        when(lookupTablesService.getAllOnesourceDatabaseTypes()).thenReturn(dtos);

        List<OnesourceDatabaseTypeDTO> result = lookupTablesController.getAllOnesourceDatabaseTypes();

        assertEquals(1, result.size());
        assertEquals("Oracle", result.get(0).getDbType());
        assertEquals("oracle.jdbc.OracleDriver", result.get(0).getJdbcDriver());
        assertEquals(1521, result.get(0).getDefaultPort());
        assertEquals("jdbc:oracle:thin:@//{host}:{port}/{service}", result.get(0).getJdbcTemplate());
    }

    @Test
    void testGetAllOnesourceRegionsDirect() {
        OnesourceRegionDTO dto = new OnesourceRegionDTO();
        dto.setId(1);
        dto.setSystemName("region1");
        dto.setDisplayName("Region 1");
        List<OnesourceRegionDTO> dtos = Arrays.asList(dto);
        when(lookupTablesService.getAllOnesourceRegions()).thenReturn(dtos);

        List<OnesourceRegionDTO> result = lookupTablesController.getAllOnesourceRegions();

        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getId());
        assertEquals("region1", result.get(0).getSystemName());
        assertEquals("Region 1", result.get(0).getDisplayName());
    }

    @Test
    void testGetAllDomainsDirect() {
        DomainDTO dto = new DomainDTO();
        dto.setId(1);
        dto.setName("Domain 1");
        List<DomainDTO> dtos = Arrays.asList(dto);
        when(lookupTablesService.getAllDomains()).thenReturn(dtos);

        List<DomainDTO> result = lookupTablesController.getAllDomains();

        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getId());
        assertEquals("Domain 1", result.get(0).getName());
    }

    @Test
    void testGetAllDomainTypesDirect() {
        DomainObjectDTO dto = new DomainObjectDTO();
        dto.setId(1);
        dto.setSystemName("systemName1");
        dto.setCreatedBy("user1");
        dto.setCreatedAt(java.time.LocalDateTime.now());
        dto.setUpdatedBy("user2");
        dto.setUpdatedAt(java.time.LocalDateTime.now());
        List<DomainObjectDTO> dtos = Arrays.asList(dto);
        when(lookupTablesService.getAllDomainObjects()).thenReturn(dtos);

        List<DomainObjectDTO> result = lookupTablesController.getAllDomainObjects();

        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getId());
        assertEquals("systemName1", result.get(0).getSystemName());
        assertEquals("user1", result.get(0).getCreatedBy());
        assertEquals("user2", result.get(0).getUpdatedBy());
    }
}
