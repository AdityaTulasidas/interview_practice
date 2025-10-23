package com.thomsonreuters.metadataregistry.service;


import com.thomsonreuters.dataconnect.dataintegration.exceptionhandler.DataConnectClientException;
import com.thomsonreuters.dataconnect.dataintegration.model.entity.enums.ExecType;
import com.thomsonreuters.dataconnect.dataintegration.model.entity.enums.OperationType;
import com.thomsonreuters.dataconnect.dataintegration.services.job.DataConnectClientService;
import com.thomsonreuters.metadataregistry.exceptionhandler.MetaDataRegistryException;

import com.thomsonreuters.metadataregistry.model.dto.MetaObjectRelationDTO;
import com.thomsonreuters.metadataregistry.model.entity.MetaObject;
import com.thomsonreuters.metadataregistry.model.entity.MetaObjectRelation;
import com.thomsonreuters.metadataregistry.repository.MetaObjectRelationRepository;
import com.thomsonreuters.metadataregistry.repository.MetaObjectRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

import static com.thomsonreuters.metadataregistry.constants.Constants.META_OBJECT_RELATION_SYS_NAME;
import static com.thomsonreuters.metadataregistry.constants.Constants.META_OBJECT_SYS_NAME;
import static java.lang.Boolean.FALSE;


@Slf4j
@Service
public class MetaObjectRelationService {


    private final MetaObjectRelationRepository metaObjectRelationRepository;

    private  final DataConnectClientService dataConnectClientService;




    MetaObjectRepository metaObjectRepository;
    @Autowired
    public MetaObjectRelationService(MetaObjectRelationRepository metaObjectRelationRepository, DataConnectClientService dataConnectClientService, MetaObjectRepository metaObjectRepository) {
        this.metaObjectRelationRepository = metaObjectRelationRepository;
        this.dataConnectClientService = dataConnectClientService;
        this.metaObjectRepository = metaObjectRepository;
    }

    public String createMetaObjectRelationService(MetaObjectRelationDTO metaObjectRelationDTO) {
        try
        {
            Optional<MetaObject> parentObject=metaObjectRepository.findById(metaObjectRelationDTO.getParentObjectId());
            if(parentObject.isEmpty()){
                throw new MetaDataRegistryException("Parent Object does not exists", "INVALID_REQUEST");
            }
            Optional<MetaObject> childObject=metaObjectRepository.findById(metaObjectRelationDTO.getChildObjectId());
            if(childObject.isEmpty()){
                throw new MetaDataRegistryException("Child Object does not exists", "INVALID_REQUEST");
            }
            MetaObjectRelation metaObjectRelation=new MetaObjectRelation();
            metaObjectRelation.setRelationType(metaObjectRelationDTO.getRelationType()!=null? metaObjectRelationDTO.getRelationType().trim() :null);
            metaObjectRelation.setParentObjRelCol(StringUtils.trimToNull(metaObjectRelationDTO.getParentObjRelCol()));
            metaObjectRelation.setChildObjRelCol(StringUtils.trimToNull(metaObjectRelationDTO.getChildObjRelCol()));
            metaObjectRelation.setParentObject(parentObject.get());
            metaObjectRelation.setChildObject(childObject.get());
            metaObjectRelation.setSystemName(StringUtils.trimToNull(childObject.get().getSystemName()));
            metaObjectRelation.setCreatedAt(java.time.LocalDateTime.now());
            metaObjectRelation.setUpdatedAt(java.time.LocalDateTime.now());
            metaObjectRelation.setCreatedBy("Admin");
            metaObjectRelation.setUpdatedBy("Admin");
            metaObjectRelation.setDescription(StringUtils.trimToNull(metaObjectRelationDTO.getDescription()));
            String id=metaObjectRelationRepository.save(metaObjectRelation).getId().toString();
            log.info("Syncing created metaobject with ID to emea: {}", id);
            ArrayList<String> ids = new ArrayList<>();
            ids.add(id);
            dataConnectClientService.sendDataChanges(
                    OperationType.CREATE,
                    META_OBJECT_RELATION_SYS_NAME,
                    "",
                    "",
                    ids,
                    "",
                    LocalDateTime.now(),
                    FALSE
            );
            log.info("Successfully synced created metaobject with ID to emea: {}", id);
            return  id;
        }
        catch (DataIntegrityViolationException e)
        {
            throw new MetaDataRegistryException("MetaObject Relation already exists", "CONFLICT");
        }
        catch (DataConnectClientException e){
            log.error("Error syncing created metaobject with ID to emea: {}", e.getMessage());
            throw new MetaDataRegistryException("Error in Syncing data to EMEA", "INTERNAL_SERVER_ERROR");
        }
        catch (MetaDataRegistryException e)
        {
            throw e;
        }
    }
}
