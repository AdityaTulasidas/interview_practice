package com.thomsonreuters.metadataregistry.service;

import com.thomsonreuters.metadataregistry.exceptionhandler.MetaDataRegistryException;
import com.thomsonreuters.metadataregistry.configuration.ModelMapperConfig;
import com.thomsonreuters.metadataregistry.model.dto.MetaObjectAttributeDTO;
import com.thomsonreuters.metadataregistry.model.entity.MetaObjectAttribute;
import com.thomsonreuters.metadataregistry.model.entity.MetaObject;
import com.thomsonreuters.metadataregistry.repository.MetaAttributeRepository;
import com.thomsonreuters.metadataregistry.repository.MetaObjectRepository;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import static com.thomsonreuters.metadataregistry.constants.Constants.*;


@Service
public class MetaAttributeService {

    public static final Logger log = org.slf4j.LoggerFactory.getLogger(MetaAttributeService.class);


    private final MetaAttributeRepository metaAttributeRepository;


    private final MetaObjectRepository metaObjectRepository;


    private final ModelMapperConfig modelMapperConfig;

    private static final String NAME ="Admin";

    @Autowired
    public MetaAttributeService(MetaAttributeRepository metaAttributeRepository, MetaObjectRepository metaObjectRepository, ModelMapperConfig modelMapperConfig) {
        this.metaAttributeRepository = metaAttributeRepository;
        this.metaObjectRepository = metaObjectRepository;
        this.modelMapperConfig = modelMapperConfig;
    }


    public Set<MetaObjectAttributeDTO> getAttributes(UUID id){
        try {
            if (id == null ) {
                throw new IllegalArgumentException("MetaObjectId cannot be null or empty");
            }
            MetaObject metaObject=metaObjectRepository.findById(id).orElseThrow(() -> new MetaDataRegistryException("MetaObject not found", NOT_FOUND));
            List<MetaObjectAttribute> metaObjectAttributes=metaAttributeRepository.findByMetaObject(metaObject);
            Set<MetaObjectAttributeDTO> metaObjectAttributesDto=new HashSet<>();
            if (!metaObjectAttributes.isEmpty()) {
                for(MetaObjectAttribute metaObjectAttribute:metaObjectAttributes) {
                    MetaObjectAttributeDTO metaObjectAttributeDTO = modelMapperConfig.modelMapper().map(metaObjectAttribute, MetaObjectAttributeDTO.class);
                    metaObjectAttributeDTO.setMetaObjectSysName(metaObject.getSystemName());
                    metaObjectAttributesDto.add(metaObjectAttributeDTO);
                }
            }
            return metaObjectAttributesDto;
        }catch (IllegalArgumentException e){
            throw new MetaDataRegistryException(e.getMessage(), "INVALID_REQUEST");
        }
        catch (MetaDataRegistryException e){
            throw e;
        }
        catch (Exception e) {
            throw new MetaDataRegistryException("Failed to fetch MetaAttributes by MetaObjectId", INTERNAL_SERVER_ERROR);
        }
    }


}