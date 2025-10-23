package com.thomsonreuters.metadataregistry.controller;

import com.thomsonreuters.metadataregistry.model.dto.DomainDTO;
import com.thomsonreuters.metadataregistry.model.dto.DomainObjectDTO;
import com.thomsonreuters.metadataregistry.model.dto.OnesourceRegionDTO;
import com.thomsonreuters.metadataregistry.model.dto.OnesourceDatabaseTypeDTO;
import com.thomsonreuters.metadataregistry.service.LookupTablesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/lookup-tables")
public class LookupTablesController {
    private final LookupTablesService lookupTablesService;

    @Autowired
    public LookupTablesController(LookupTablesService lookupTablesService) {
        this.lookupTablesService = lookupTablesService;
    }

    @GetMapping("/onesource-regions")
    public List<OnesourceRegionDTO> getAllOnesourceRegions() {
        return lookupTablesService.getAllOnesourceRegions();
    }

    @GetMapping("/domains")
    public List<DomainDTO> getAllDomains() {
        return lookupTablesService.getAllDomains();
    }

    @GetMapping("/domain-objects")
    public List<DomainObjectDTO> getAllDomainObjects() {
        return lookupTablesService.getAllDomainObjects();
    }

    @GetMapping("/datasources")
    public List<OnesourceDatabaseTypeDTO> getAllOnesourceDatabaseTypes() {
        return lookupTablesService.getAllOnesourceDatabaseTypes();
    }
}
