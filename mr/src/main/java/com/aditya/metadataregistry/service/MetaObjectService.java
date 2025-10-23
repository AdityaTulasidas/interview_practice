package com.thomsonreuters.metadataregistry.service;

import com.thomsonreuters.dataconnect.dataintegration.model.entity.enums.OperationType;
import com.thomsonreuters.dataconnect.dataintegration.services.job.DataConnectClientService;
import com.thomsonreuters.dep.api.spring.ApiCriteria;
import com.thomsonreuters.dep.api.spring.ApiSupport;
import com.thomsonreuters.dep.api.spring.response.ApiCollection;
import com.thomsonreuters.dep.api.spring.response.ApiCollectionFactory;
import com.thomsonreuters.metadataregistry.configuration.ModelMapperConfig;
import com.thomsonreuters.metadataregistry.constants.Constants;
import com.thomsonreuters.metadataregistry.exceptionhandler.MetaDataRegistryException;
import com.thomsonreuters.metadataregistry.model.dto.*;
import com.thomsonreuters.metadataregistry.model.entity.MetaObject;
import com.thomsonreuters.metadataregistry.model.entity.MetaObjectAttribute;
import com.thomsonreuters.metadataregistry.model.entity.MetaObjectRelation;
import com.thomsonreuters.metadataregistry.model.entity.enums.DataType;

import com.thomsonreuters.metadataregistry.repository.DomainObjectRepository;
import com.thomsonreuters.metadataregistry.repository.DomainRepository;
import com.thomsonreuters.metadataregistry.repository.MetaObjectRelationRepository;
import com.thomsonreuters.metadataregistry.repository.MetaObjectRepository;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.thomsonreuters.metadataregistry.constants.Constants.*;
import static java.lang.Boolean.FALSE;

@Service
public class MetaObjectService {

    public static final Logger log = org.slf4j.LoggerFactory.getLogger(MetaObjectService.class);


    private final MetaObjectRepository metaObjectRepository;


    private final ModelMapperConfig modelMapperConfig;


    private final MetaAttributeService metaAttributeService;


    private final MetaObjectRelationRepository metaObjectRelationRepository;


    private final DataConnectClientService dataConnectClientService;

    private final ApiSupport apiSupport;

    private final ApiCollectionFactory apiCollection;
    private final DomainRepository domainRepository;


    private final DomainObjectRepository domainObjectRepository;

    @Autowired
    public MetaObjectService(MetaObjectRepository metaObjectRepository,
                             ModelMapperConfig modelMapperConfig,
                             MetaAttributeService metaAttributeService,
                             MetaObjectRelationRepository metaObjectRelationRepository,
                             DataConnectClientService dataConnectClientService, DomainRepository domainRepository, ApiCollectionFactory apiCollection, ApiSupport apiSupport,DomainObjectRepository domainObjectRepository) {
        this.metaObjectRepository = metaObjectRepository;
        this.modelMapperConfig = modelMapperConfig;
        this.metaAttributeService = metaAttributeService;
        this.metaObjectRelationRepository = metaObjectRelationRepository;
        this.dataConnectClientService = dataConnectClientService;
        this.domainRepository = domainRepository;
        this.apiCollection = apiCollection;
        this.apiSupport = apiSupport;

        this.domainObjectRepository = domainObjectRepository;
    }

    public MetaObjectDTO createMetaObject(MetaObjectPostDTO metaObjectDto) throws MetaDataRegistryException{
        try {
            int seqNum = 1;
            MetaObject metaObject= modelMapperConfig.modelMapper().map(metaObjectDto, MetaObject.class);
            validateDomainObject(metaObject.getDomainObject());
            validateOneSourceDomain(metaObject.getOneSourceDomain().trim());
            checkForDuplicateAttributes(metaObjectDto);
            // Set metaObjectSysName from request payload
            metaObject.setSystemName(metaObjectDto.getDomainObject().trim()+"."+metaObjectDto.getBusinessName().trim());
            metaObject.setCreatedAt(java.time.LocalDateTime.now());
            metaObject.setUpdatedAt(java.time.LocalDateTime.now());
            metaObject.setCreatedBy(USER_NAME);
            metaObject.setUpdatedBy(USER_NAME);
            Set<MetaObjectAttribute> attributes = new HashSet<>();
            if (metaObjectDto.getAttributes() != null && !metaObjectDto.getAttributes().isEmpty()) {
                for (MetaObjectsMetaObjectAttributePostDTO attributeDto : metaObjectDto.getAttributes()) {
                    MetaObjectAttribute metaObjectAttribute = modelMapperConfig.modelMapper().map(attributeDto, MetaObjectAttribute.class);
                    metaObjectAttribute.setMetaObject(metaObject);
                    metaObjectAttribute.setDisplayName(metaObjectAttribute.getDisplayName()==null? metaObjectAttribute.getDbColumn().trim() : metaObjectAttribute.getDisplayName().trim());
                    metaObjectAttribute.setDescription(StringUtils.trimToNull(metaObjectAttribute.getDescription())==null? metaObjectAttribute.getDbColumn().trim() : metaObjectAttribute.getDescription().trim());
                    metaObjectAttribute.setSeqNum(seqNum++);
                    metaObjectAttribute.setCreatedAt(java.time.LocalDateTime.now());
                    metaObjectAttribute.setUpdatedAt(java.time.LocalDateTime.now());
                    metaObjectAttribute.setCreatedBy(USER_NAME);
                    metaObjectAttribute.setUpdatedBy(USER_NAME);
                    attributes.add(metaObjectAttribute);
                }

            }
            metaObject.setAttributes(attributes);
            MetaObject savedMetaObjectEntity = metaObjectRepository.save(metaObject);
            String savedId= String.valueOf(savedMetaObjectEntity.getSystemName());
            syncMetaObjectAcrossRegions(savedId, savedMetaObjectEntity.getSystemName(), OperationType.CREATE.toString());
            return modelMapperConfig.modelMapper().map(savedMetaObjectEntity, MetaObjectDTO.class);

        } catch (DataIntegrityViolationException e) {
            log.error("Error while creating MetaObject", e);
            throw new MetaDataRegistryException("MetaObject already exists", "CONFLICT");
        }catch (MetaDataRegistryException e){
            throw new MetaDataRegistryException(e.getMessage(), e.getCode());
        }
        catch (Exception e) {
            log.error("Error while creating MetaObject", e);
            throw new MetaDataRegistryException(META_OBJECT_CREATION_FAIL, INTERNAL_SERVER_ERROR);
        }
    }




    private void validateOneSourceDomain(String oneSourceDomain) throws MetaDataRegistryException{
        if(!domainRepository.existsBySystemName(oneSourceDomain.trim()))
            throw new MetaDataRegistryException("Invalid onesource_domain " + oneSourceDomain, INVALID_REQUEST);
    }

    public MetaObjectDTO getMetaObjectById(UUID id) {
        try {
            MetaObjectDTO metaObjectDTO = modelMapperConfig.modelMapper().map(metaObjectRepository.findById(id).orElseThrow(() -> new MetaDataRegistryException(META_OBJECT_NOT_FOUND, NOT_FOUND)), MetaObjectDTO.class);
            metaObjectDTO.setAttributes(getMetaAttributes(id));
            return metaObjectDTO;
        } catch (MetaDataRegistryException e) {
            throw e;
        } catch (Exception e) {
            throw new MetaDataRegistryException(META_OBJECT_FETCH_FAIL, INTERNAL_SERVER_ERROR);
        }
    }

    public List<MetaObjectDTO> getAllMetaObjects() {
        try {
            List<MetaObject> metaObjects = metaObjectRepository.findAll();
            List<MetaObjectDTO> metaObjectDTOs = new ArrayList<>();
            for (MetaObject metaObject : metaObjects) {
                MetaObjectDTO metaObjectDTO = modelMapperConfig.modelMapper().map(metaObject, MetaObjectDTO.class);
                Set<MetaObjectAttributeDTO> attributes = getMetaAttributes(metaObjectDTO.getId());
                metaObjectDTO.setAttributes(attributes);
                metaObjectDTOs.add(metaObjectDTO);
            }
            if (metaObjectDTOs.isEmpty()) {
                throw new MetaDataRegistryException("MetaObjects not found", NOT_FOUND);
            }
            return metaObjectDTOs;
        } catch (MetaDataRegistryException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error while fetching all MetaObjects", e);
            throw new MetaDataRegistryException("Failed to fetch all MetaObjects", INTERNAL_SERVER_ERROR);
        }
    }


    public MetaRelationMetaModelDTO getMetaModel(UUID metaObjectId) {
        log.info("In getMetaModel for MetaObject ID: {}", metaObjectId);
        MetaRelationMetaModelDTO metaRelationMetaModelDTO = new MetaRelationMetaModelDTO();
        Set<MetaObjectRelation> relations = metaObjectRelationRepository.findByParentObjectId(metaObjectId);
        if (relations.isEmpty()) {
            throw new MetaDataRegistryException(META_OBJECT_NOT_FOUND, NOT_FOUND);
        }
        MetaObject parentObject = findMetaObjectById(metaObjectId);
        MetaObjectDTO parentObjectDTO = convertToMetaObjectDTO(parentObject);
        metaRelationMetaModelDTO.setParentObject(parentObjectDTO);
        metaRelationMetaModelDTO.setChildObjectRelations(relations.stream().map(this::convertToMetaObjectRelationDTO).collect(Collectors.toSet()));
        return metaRelationMetaModelDTO;
    }

    private MetaObject findMetaObjectById(UUID metaObjectId) {
        return metaObjectRepository.findById(metaObjectId)
                .orElseThrow(() -> new MetaDataRegistryException(META_OBJECT_NOT_FOUND, NOT_FOUND));
    }

    private MetaObjectDTO convertToMetaObjectDTO(MetaObject metaObject) {
        MetaObjectDTO dto = modelMapperConfig.modelMapper().map(metaObject, MetaObjectDTO.class);
        dto.setCreatedBy(metaObject.getCreatedBy());
        dto.setUpdatedBy(metaObject.getUpdatedBy());
        dto.setCreatedAt(metaObject.getCreatedAt());
        dto.setUpdatedAt(metaObject.getUpdatedAt());
        dto.setAttributes(getMetaAttributes(metaObject.getId()));
        return dto;
    }

    public Set<MetaObjectAttributeDTO> getMetaAttributes(UUID metaObjectId) {
        return metaAttributeService.getAttributes(metaObjectId);

    }

    private MetaRelationModelDTO convertToMetaObjectRelationDTO(MetaObjectRelation relation) {
        MetaRelationModelDTO metaRelationModelDTO= modelMapperConfig.modelMapper().map(relation, MetaRelationModelDTO.class);
        metaRelationModelDTO.getChildObject().getAttributes().forEach(attr ->{
            attr.setMetaObjectSysName(relation.getChildObject().getSystemName());
        });
        return metaRelationModelDTO;

    }


    public MetaObjectDTO updateMetaObjects(UUID id, MetaObjectPutDTO metaObjectDto) {
        try {
            if (id == null || metaObjectDto == null) {
                throw new MetaDataRegistryException("MetaObject ID and request body cannot be null", Constants.INVALID_REQUEST);
            }
            // Fetch the existing MetaObject
            MetaObject existingMetaObject = metaObjectRepository.findById(id)
                    .orElseThrow(() -> new MetaDataRegistryException("MetaObject not found", NOT_FOUND));
            validateOneSourceDomain(metaObjectDto.getOneSourceDomain().trim());
            // Check if the existing MetaObject is the same as the request payload
            if (isSameMetaObject(existingMetaObject, metaObjectDto))
             {
                throw new MetaDataRegistryException("MetaObject already exists with same data", "CONFLICT");
            }
            // Update MetaObject fields
            updateMetaObjectFields(existingMetaObject, metaObjectDto);

            // Fetch existing attributes
            Set<MetaObjectAttribute> existingAttributes = existingMetaObject.getAttributes();
            int maxSeqNum = existingAttributes.stream()
                    .mapToInt(MetaObjectAttribute::getSeqNum)
                    .max()
                    .orElse(0);
            // Process incoming attributes
            Set<MetaObjectAttributePutDTO> incomingAttributes = metaObjectDto.getAttributes();

            if (incomingAttributes != null && !incomingAttributes.isEmpty()) {
                for (MetaObjectAttributePutDTO attributeDto : incomingAttributes) {
                    Optional<MetaObjectAttribute> existingAttributeOpt = existingAttributes.stream()
                            .filter(attr -> Objects.equals(attr.getSystemName(), attributeDto.getSystemName()))
                            .findFirst();

                    if (existingAttributeOpt.isPresent()) {

                        // Update existing attribute
                        MetaObjectAttribute existingAttribute = existingAttributeOpt.get();
                        if(attributeDto.getDescription()!=null){
                            existingAttribute.setDescription(attributeDto.getDescription());
                        }
                        existingAttribute.setDataType(attributeDto.getDataType() != null ? DataType.valueOf(attributeDto.getDataType().toString().trim()) : existingAttribute.getDataType());
                        existingAttribute.setMandatory(attributeDto.isMandatory());
                        existingAttribute.setDisplayName(attributeDto.getDisplayName());
                        existingAttribute.setPrimary(attributeDto.isPrimary());
                        existingAttribute.setUpdatedAt(java.time.LocalDateTime.now());
                        existingAttribute.setSysAttribute(attributeDto.isSysAttribute());
                        existingAttribute.setUpdatedBy(USER_NAME);
                        existingAttribute.setMetaObject(existingMetaObject);
                        existingAttribute.setOrderBy(attributeDto.getOrderBy());
                        existingAttribute.setEventEnabled(attributeDto.isEventEnabled());
                        existingAttribute.setLogicalKey(attributeDto.getLogicalKey());
                        existingAttribute.setDbColumn(attributeDto.getDbColumn());
                    } else {
                        // Add new attribute
                        MetaObjectAttribute newAttribute = modelMapperConfig.modelMapper().map(attributeDto, MetaObjectAttribute.class);
                        newAttribute.setMetaObject(existingMetaObject);
                        newAttribute.setSeqNum(++maxSeqNum);
                        newAttribute.setCreatedAt(java.time.LocalDateTime.now());
                        newAttribute.setUpdatedAt(java.time.LocalDateTime.now());
                        newAttribute.setCreatedBy(USER_NAME);
                        newAttribute.setUpdatedBy(USER_NAME);
                        existingAttributes.add(newAttribute);
                    }
                }
            }
            // Save updated MetaObject
            existingMetaObject.setAttributes(existingAttributes);
            existingMetaObject.setUpdatedAt(LocalDateTime.now());
            existingMetaObject.setUpdatedBy(USER_NAME);
            MetaObject savedMetaObject = metaObjectRepository.save(existingMetaObject);
            syncMetaObjectAcrossRegions(String.valueOf(savedMetaObject.getSystemName()), savedMetaObject.getSystemName(), OperationType.UPDATE.toString());
            return modelMapperConfig.modelMapper().map(savedMetaObject, MetaObjectDTO.class);
        } catch (MetaDataRegistryException e) {
            throw e;
        }
        catch (DataIntegrityViolationException e){
            log.error("Data integrity violation while updating MetaObject with id: {}", id, e);
            throw new MetaDataRegistryException("MetaObject or Attribute already exists with same data", CONFLICT);
        } catch(Exception e) {
            log.error("Error while updating MetaObject with id: {}", id, e);
            throw new MetaDataRegistryException("Failed to update MetaObject", INTERNAL_SERVER_ERROR);
        }
    }

    private void updateMetaObjectFields(MetaObject existingMetaObject, MetaObjectPutDTO metaObjectDto) {
        if(metaObjectDto.getDescription()!=null){
            existingMetaObject.setDescription(StringUtils.trimToNull(metaObjectDto.getDescription()));
        }
        if(metaObjectDto.getDisplayName()!=null){
            existingMetaObject.setDisplayName(StringUtils.trimToNull(metaObjectDto.getDisplayName()));
        }
        if(metaObjectDto.getDbTable()!=null){
            existingMetaObject.setDbTable(StringUtils.trimToNull(metaObjectDto.getDbTable()));
        }
        if(metaObjectDto.isAutogenId()!=existingMetaObject.isAutogenId()){
            existingMetaObject.setAutogenId(metaObjectDto.isAutogenId());
        }
        if(metaObjectDto.getOneSourceDomain()!=null){
            existingMetaObject.setOneSourceDomain(StringUtils.trimToNull(metaObjectDto.getOneSourceDomain()));
        }
        if(metaObjectDto.isEventEnabled()!=existingMetaObject.isEventEnabled()){
            existingMetaObject.setEventEnabled(metaObjectDto.isEventEnabled());
        }
        if(metaObjectDto.getSchema()!=null){
            existingMetaObject.setSchema(StringUtils.trimToNull(metaObjectDto.getSchema()));
        }
    }

    private boolean isSameMetaObject(MetaObject existingMetaObject, MetaObjectPutDTO metaObjectDto) {
        boolean matchFound = false;

        if((metaObjectDto.getDescription()==null || Objects.equals(metaObjectDto.getDescription(), existingMetaObject.getDescription())) &&
        existingMetaObject.getDbTable().equals(metaObjectDto.getDbTable()) &&
                (metaObjectDto.getDisplayName()==null || existingMetaObject.getDisplayName().equals(metaObjectDto.getDisplayName())) &&
                existingMetaObject.getOneSourceDomain().equals(metaObjectDto.getOneSourceDomain()) &&
                existingMetaObject.isEventEnabled() == metaObjectDto.isEventEnabled() &&
                (existingMetaObject.isAutogenId() == metaObjectDto.isAutogenId())) {


            for (MetaObjectAttribute existingAttr : existingMetaObject.getAttributes()) {
                for (MetaObjectAttributePutDTO dtoAttr : metaObjectDto.getAttributes()) {
                    if (Objects.equals(existingAttr.getDbColumn(), dtoAttr.getDbColumn()) &&
                            (dtoAttr.getDescription()==null || Objects.equals(existingAttr.getDescription(), dtoAttr.getDescription())) &&
                            Objects.equals(existingAttr.getDataType(), dtoAttr.getDataType()) &&
                            existingAttr.isMandatory() == dtoAttr.isMandatory() &&
                            existingAttr.isPrimary() == dtoAttr.isPrimary() &&
                            Objects.equals(existingAttr.getDisplayName(), dtoAttr.getDisplayName()) &&
                            existingAttr.isSysAttribute() == dtoAttr.isSysAttribute() &&
                            Objects.equals(existingAttr.getLogicalKey(), dtoAttr.getLogicalKey()) &&
                            existingAttr.isEventEnabled() == dtoAttr.isEventEnabled() &&
                            Objects.equals(existingAttr.getSystemName(), dtoAttr.getSystemName()) &&
                            Objects.equals(existingAttr.getOrderBy(), dtoAttr.getOrderBy())
                    ) {
                        matchFound = true;
                        break;
                    }
                }

            }
        }
        return matchFound;
    }
    public ResponseEntity<?> getAllMetaObjects(int page, int size, String sort, String filter) {
        try {
            ApiCriteria<MetaObject> criteria = apiSupport.getCriteriaHolder(MetaObject.class);
            final Specification<MetaObject> spec = criteria.getSpecification();
            Page<MetaObject> metaObjectPage = metaObjectRepository.findAll(spec, criteria.getPageable());
            ApiCollection<MetaObjectDTO> metaObjectResponseList = apiCollection.from(metaObjectPage).mapItems(metaObject -> {
                MetaObjectDTO metaObjectDTO=modelMapperConfig.modelMapper().map(metaObject, MetaObjectDTO.class);
                metaObjectDTO.getAttributes().forEach(attr -> attr.setMetaObjectSysName(metaObjectDTO.getSystemName()));
                return metaObjectDTO;
            });

            return ResponseEntity.status(HttpStatus.OK).body(metaObjectResponseList);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MetaDataRegistryException("Failed to fetch MetaObjects", "INTERNAL_SERVER_ERROR"));
        }
    }




    public int updateUsageCounter(String id, int value) {
        try {
            if (id == null || id.isEmpty()) {
                throw new MetaDataRegistryException("MetaObject ID cannot be null or empty", INVALID_REQUEST);
            }
            if (value != 1 && value != -1) {
                throw new MetaDataRegistryException("Invalid value. Use '1' for increment or '-1' for decrement.", INVALID_REQUEST);
            }
            UUID uuid = UUID.fromString(id); // Convert String to UUID
            MetaObject metaObject = metaObjectRepository.findById(uuid)
                    .orElseThrow(() -> new MetaDataRegistryException("MetaObject not found with ID: " + id, NOT_FOUND));

            if (value == 1) {
                metaObject.setUsageCount(metaObject.getUsageCount() + 1);
            } else if (value == -1) {
                if (metaObject.getUsageCount() > 0) {
                    metaObject.setUsageCount(metaObject.getUsageCount() - 1);
                } else {
                    throw new MetaDataRegistryException("Usage count cannot be negative.", INVALID_REQUEST);
                }
            }

            metaObjectRepository.save(metaObject);
            return metaObject.getUsageCount();
        } catch (MetaDataRegistryException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error while updating usage counter for MetaObject with ID: {}", id, e);
            throw new MetaDataRegistryException("Failed to update usage counter", INTERNAL_SERVER_ERROR);
        }
    }

    public int updateUsageCounterAbsolute(String id, int value) {
        try {
            UUID uuid = UUID.fromString(id);
            MetaObject metaObject = metaObjectRepository.findById(uuid)
                    .orElseThrow(() -> new MetaDataRegistryException("MetaObject not found with ID: " + id, NOT_FOUND));

            if (value < 0) {
                throw new MetaDataRegistryException("Usage count cannot be negative.", INVALID_REQUEST);
            }

            metaObject.setUsageCount(value);
            metaObjectRepository.save(metaObject);
            return metaObject.getUsageCount();
        } catch (MetaDataRegistryException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error while updating absolute usage counter for MetaObject with ID: {}", id, e);
            throw new MetaDataRegistryException("Failed to update absolute usage counter", INTERNAL_SERVER_ERROR);
        }
    }
    public void syncMetaObjectAcrossRegions(String id, String name, String operationType) {
        try {
            log.info("Syncing updated metaobject with ID to emea: {}", id);
            ArrayList<String> ids = new ArrayList<>();
            ids.add(id);

            dataConnectClientService.sendDataChanges(
                    OperationType.valueOf(operationType),
                    META_OBJECT_SYS_NAME,
                    "",
                    "",
                    ids,
                    "",
                   LocalDateTime.now(),
                    FALSE
            );
            log.info("Successfully synced updated metaobject with ID to emea: {}", id);
        } catch (Exception e) {
            log.error("Error syncing updated metaobject with ID to emea: {}", e.getMessage());
            throw new MetaDataRegistryException("Error in Syncing data to EMEA", INTERNAL_SERVER_ERROR);
        }
    }

    private void checkForDuplicateAttributes(MetaObjectPostDTO metaObjectDto) throws MetaDataRegistryException {
        Set<String> systemNames = new HashSet<>();
        for (MetaObjectsMetaObjectAttributePostDTO attributeDTO : metaObjectDto.getAttributes()) {
            if (!systemNames.add(attributeDTO.getSystemName())) {
                throw new MetaDataRegistryException("Duplicate system_name found for Meta object Attribute: " + attributeDTO.getSystemName(), "INVALID_REQUEST");
            }
        }
    }

    private void validateDomainObject(@NotNull @NotBlank @NotEmpty String domainObject) throws MetaDataRegistryException{
            if(!domainObjectRepository.existsBySystemName(domainObject)){
                throw new MetaDataRegistryException("Invalid domain_object " + domainObject, INVALID_REQUEST);
            }
    }

}
