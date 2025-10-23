package com.thomsonreuters.metadataregistry.service;

import com.thomsonreuters.metadataregistry.model.dto.DomainDTO;
import com.thomsonreuters.metadataregistry.model.dto.DomainObjectDTO;
import com.thomsonreuters.metadataregistry.model.dto.OnesourceDatabaseTypeDTO;
import com.thomsonreuters.metadataregistry.model.dto.OnesourceRegionDTO;
import com.thomsonreuters.metadataregistry.repository.DomainObjectRepository;
import com.thomsonreuters.metadataregistry.repository.DomainRepository;
import com.thomsonreuters.metadataregistry.repository.OnesourceDatabaseTypeRepository;
import com.thomsonreuters.metadataregistry.repository.OnesourceRegionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LookupTablesService {
    private final OnesourceRegionRepository onesourceRegionRepository;
    private final DomainRepository domainRepository;
    private final DomainObjectRepository domainObjectRepository;
    private final OnesourceDatabaseTypeRepository onesourceDatabaseTypeRepository;

    @Autowired
    public LookupTablesService(OnesourceRegionRepository onesourceRegionRepository, DomainRepository domainRepository, DomainObjectRepository domainObjectRepository, OnesourceDatabaseTypeRepository onesourceDatabaseTypeRepository) {
        this.onesourceRegionRepository = onesourceRegionRepository;
        this.domainRepository = domainRepository;
        this.domainObjectRepository = domainObjectRepository;
        this.onesourceDatabaseTypeRepository = onesourceDatabaseTypeRepository;
    }

    public List<OnesourceRegionDTO> getAllOnesourceRegions() {
        return onesourceRegionRepository.findAll().stream()
            .map(OnesourceRegionDTO::new)
            .collect(Collectors.toList());
    }

    public List<DomainDTO> getAllDomains() {
        return domainRepository.findAll().stream()
            .map(DomainDTO::new)
            .collect(Collectors.toList());
    }

    public List<DomainObjectDTO> getAllDomainObjects() {
        return domainObjectRepository.findAll().stream()
            .map(DomainObjectDTO::new)
            .collect(Collectors.toList());
    }

    public List<OnesourceDatabaseTypeDTO> getAllOnesourceDatabaseTypes() {
        return onesourceDatabaseTypeRepository.findAll().stream()
            .map(OnesourceDatabaseTypeDTO::new)
            .collect(Collectors.toList());
    }
}
