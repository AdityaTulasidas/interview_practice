package com.aditya.dataconnect.executionengine.adapters;

import com.thomsonreuters.dataconnect.common.executioncontext.ExecutionContext;
import com.thomsonreuters.dataconnect.common.executioncontext.RegionalJobContext;
import com.aditya.dataconnect.executionengine.dto.MetaObjectDTO;
import com.aditya.dataconnect.executionengine.dto.MetaRelationMetaModelDTO;
import com.aditya.dataconnect.executionengine.dto.MetaRelationModelDTO;
import com.aditya.dataconnect.executionengine.exceptionhandler.DataSyncJobException;
import com.aditya.dataconnect.executionengine.model.entity.MetaObject;
import com.aditya.dataconnect.executionengine.model.entity.MetaObjectRelation;
import com.aditya.dataconnect.executionengine.model.entity.enums.ExecutionLeg;
import com.aditya.dataconnect.executionengine.model.pojo.Header;
import com.aditya.dataconnect.executionengine.repository.MetaObjectRelationRepository;
import com.aditya.dataconnect.executionengine.repository.MetaObjectRepository;
import com.aditya.dataconnect.executionengine.services.MetaModelClient;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

import static com.aditya.dataconnect.executionengine.model.entity.enums.ErrorConstant.JOB_READ_ERROR;
import static com.aditya.dataconnect.executionengine.model.entity.enums.ErrorConstant.METAOBJECT_NOT_FOUND;

@Component
@Slf4j
public class CommonAdaptersUtil {

    @Autowired
    protected MetaObjectRelationRepository metaObjectRelationRepository;

    @Autowired
    protected MetaObjectRepository metaObjectRepository;

    @Autowired
    protected ModelMapper modelMapper;

    @Autowired
    protected MetaModelClient metaModelClient;

    public static List<String> sanitizeAndValidateObjIds(List<?> objIds) throws DataSyncJobException {
        List<String> sanitizedList = new ArrayList<>();
        for (Object objId : objIds) {
            if (objId instanceof String) {
                String strId = ((String) objId).trim();
                // Add sanitized ID to the list
                sanitizedList.add(strId);
            } else if (objId instanceof Integer) {
                sanitizedList.add(String.valueOf(objId));
            } else if (objId instanceof UUID) {
                sanitizedList.add(objId.toString());
            } else if (objId instanceof Map) {
                Map<?, ?> mapObj = (Map<?, ?>) objId;
                for (Map.Entry<?, ?> entry : mapObj.entrySet()) {
                    if (!(entry.getKey() instanceof String)) {
                        throw new DataSyncJobException("Map key must be a String", "BAD_REQUEST");
                    }
                    String key = (String) entry.getKey();
                    Object value = entry.getValue();
                    if (value instanceof List) {
                        List<?> valueList = (List<?>) value;
                        for (Object val : valueList) {
                            String sanitizedVal;
                            if (val instanceof String) {
                                sanitizedVal = ((String) val).trim();

                            } else if (val instanceof Integer) {
                                sanitizedVal = String.valueOf(val);
                            } else if (val instanceof UUID) {
                                sanitizedVal = val.toString();
                            } else {
                                throw new DataSyncJobException("Unsupported DataUnit primary key type in map value", "BAD_REQUEST");
                            }
                            sanitizedList.add(key + ":" + sanitizedVal);
                        }
                    } else {
                        throw new DataSyncJobException("Map value must be a List", "BAD_REQUEST");
                    }
                }
            } else {
                throw new DataSyncJobException("Unsupported DataUnit primary key type", "BAD_REQUEST");
            }
        }
        return sanitizedList;
    }


    public MetaRelationMetaModelDTO fetchMetaRelation(UUID metaObjectId) throws DataSyncJobException {
        MetaRelationMetaModelDTO metaRelationMetaModelDTO = new MetaRelationMetaModelDTO();
        // Fetch MetaObjectRelation by parentObjectId
        Set<MetaObjectRelation> metaObjectRelations = metaObjectRelationRepository.findByParentObjectId(metaObjectId);
        if (metaObjectRelations != null && !metaObjectRelations.isEmpty()) {
            // If data exists, call getMetaModel
            metaRelationMetaModelDTO = getMetaModel(metaObjectId);
        } else {
            // If data does not exist, fetch MetaObject by ID and set it to parentObject
            MetaObject parentObject = metaObjectRepository.findMetaObjectById(metaObjectId);
            if (parentObject == null) {
                throw new DataSyncJobException(METAOBJECT_NOT_FOUND.getMessage(), METAOBJECT_NOT_FOUND.getCode());
            }
            MetaObjectDTO parentObjectDTO = modelMapper.map(parentObject, MetaObjectDTO.class);
            metaRelationMetaModelDTO.setParentObject(parentObjectDTO);
            // Set childObjectRelations to an empty list
            Set<MetaRelationModelDTO> childObjectRelations = new HashSet<>();
            metaRelationMetaModelDTO.setChildObjectRelations(childObjectRelations);
        }
        return metaRelationMetaModelDTO;
    }

    private MetaRelationMetaModelDTO getMetaModel(UUID metaObjectId) throws DataSyncJobException {
        try {
            MetaRelationMetaModelDTO metaModel = metaModelClient.getMetaRelationMetaModel(metaObjectId);
            if (metaModel == null) {
                throw new DataSyncJobException(METAOBJECT_NOT_FOUND.getMessage(), METAOBJECT_NOT_FOUND.getCode());
            }
            return metaModel;
        } catch (Exception e) {
            log.info("Error fetching meta model {}", e.getMessage());
            throw new DataSyncJobException(JOB_READ_ERROR.getMessage(), JOB_READ_ERROR.getCode());
        }
    }

    /*
    Example output:
       input/SOURCE_REGION/DOMAIN/JOB_SYS_NAME/<job exec id>/hierarchy_index/
       META_OBJECT_NAME/META_OBJECT_ID.csv
    */

    public Header generateHeader(RegionalJobContext regionalJobCtx) {
        Header header = new Header();
        header.setJobId(regionalJobCtx.getValue(RegionalJobContext.JOB_ID));
        header.setJobName(regionalJobCtx.getValue(RegionalJobContext.JOB_NAME));
        header.setJobExecId(regionalJobCtx.getValue(RegionalJobContext.EXEC_ID));
        header.setMetaObjectSysName(regionalJobCtx.getValue(RegionalJobContext.META_OBJECT_SYS_NAME));
        header.setExecLeg(ExecutionLeg.TARGET);
        return header;
    }

}
