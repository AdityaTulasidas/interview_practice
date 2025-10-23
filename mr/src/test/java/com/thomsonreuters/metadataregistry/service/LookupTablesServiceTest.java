package com.thomsonreuters.metadataregistry.service;

import com.thomsonreuters.metadataregistry.model.dto.DomainDTO;
import com.thomsonreuters.metadataregistry.model.dto.DomainObjectDTO;
import com.thomsonreuters.metadataregistry.model.dto.OnesourceDatabaseTypeDTO;
import com.thomsonreuters.metadataregistry.model.dto.OnesourceRegionDTO;
import com.thomsonreuters.metadataregistry.model.entity.Domain;
import com.thomsonreuters.metadataregistry.model.entity.DomainObject;
import com.thomsonreuters.metadataregistry.model.entity.OnesourceRegion;
import com.thomsonreuters.metadataregistry.model.entity.OnesourceDatabaseType;
import com.thomsonreuters.metadataregistry.repository.DomainRepository;
import com.thomsonreuters.metadataregistry.repository.DomainObjectRepository;
import com.thomsonreuters.metadataregistry.repository.OnesourceRegionRepository;
import com.thomsonreuters.metadataregistry.repository.OnesourceDatabaseTypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class LookupTablesServiceTest {
    @Mock
    private OnesourceRegionRepository onesourceRegionRepository;
    @Mock
    private DomainRepository domainRepository;

    @Mock
    private OnesourceDatabaseTypeRepository onesourceDatabaseTypeRepository;
    @Mock
    private DomainObjectRepository domainObjectRepository;

    @InjectMocks
    private LookupTablesService lookupTablesService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllOnesourceRegions() {
        OnesourceRegion region = new OnesourceRegion();
        region.setId(1);
        region.setSystemName("region1");
        region.setDisplayName("Region 1");
        region.setCreatedBy("user1");
        region.setCreatedAt(java.time.LocalDateTime.now());
        region.setUpdatedBy("user2");
        region.setUpdatedAt(java.time.LocalDateTime.now());
        List<OnesourceRegion> regions = Arrays.asList(region);
        when(onesourceRegionRepository.findAll()).thenReturn(regions);

        List<OnesourceRegionDTO> dtos = lookupTablesService.getAllOnesourceRegions();
        assertEquals(1, dtos.size());
        assertEquals("region1", dtos.get(0).getSystemName());
        assertEquals("Region 1", dtos.get(0).getDisplayName());
        assertEquals("user1", dtos.get(0).getCreatedBy());
    }

    @Test
    void testGetAllDomains() {
        Domain domain = new Domain();
        domain.setId(2);
        domain.setName("Domain 2");
        domain.setTypeId(100);
        domain.setSystemName("systemDomain2");
        domain.setCreatedBy("user1");
        domain.setCreatedAt(java.time.LocalDateTime.now());
        domain.setUpdatedBy("user2");
        domain.setUpdatedAt(java.time.LocalDateTime.now());
        List<Domain> domains = Arrays.asList(domain);
        when(domainRepository.findAll()).thenReturn(domains);

        List<DomainDTO> dtos = lookupTablesService.getAllDomains();
        assertEquals(1, dtos.size());
        assertEquals("Domain 2", dtos.get(0).getName());
        assertEquals(100, dtos.get(0).getTypeId());
        assertEquals("systemDomain2", dtos.get(0).getSystemName());
    }

    @Test
    void testGetAllDomainObjects() {
        DomainObject type = new DomainObject();
        type.setId(3);
        type.setSystemName("systemType3");
        type.setObjectName("ObjectName3");
        type.setDomainSysName("DomainSysName3");
        type.setDescription("Description3");
        type.setCreatedBy("user1");
        type.setCreatedAt(java.time.LocalDateTime.now());
        type.setUpdatedBy("user2");
        type.setUpdatedAt(java.time.LocalDateTime.now());
        List<DomainObject> types = Arrays.asList(type);
        when(domainObjectRepository.findAll()).thenReturn(types);

        List<DomainObjectDTO> dtos = lookupTablesService.getAllDomainObjects();
        assertEquals(1, dtos.size());
        assertEquals(3, dtos.get(0).getId());
        assertEquals("systemType3", dtos.get(0).getSystemName());
        assertEquals("ObjectName3", dtos.get(0).getObjectName());
        assertEquals("DomainSysName3", dtos.get(0).getDomainSysName());
        assertEquals("Description3", dtos.get(0).getDescription());
    }

    @Test
    void testGetAllOnesourceDatabaseTypes() {
        OnesourceDatabaseType entity = new OnesourceDatabaseType();
        entity.setDbType("Oracle");
        entity.setJdbcDriver("oracle.jdbc.OracleDriver");
        entity.setDefaultPort(1521);
        entity.setJdbcTemplate("jdbc:oracle:thin:@//{host}:{port}/{service}");
        List<OnesourceDatabaseType> entities = Arrays.asList(entity);
        when(onesourceDatabaseTypeRepository.findAll()).thenReturn(entities);

        List<OnesourceDatabaseTypeDTO> dtos = lookupTablesService.getAllOnesourceDatabaseTypes();
        assertEquals(1, dtos.size());
        assertEquals("Oracle", dtos.get(0).getDbType());
        assertEquals("oracle.jdbc.OracleDriver", dtos.get(0).getJdbcDriver());
        assertEquals(1521, dtos.get(0).getDefaultPort());
        assertEquals("jdbc:oracle:thin:@//{host}:{port}/{service}", dtos.get(0).getJdbcTemplate());
    }


}
