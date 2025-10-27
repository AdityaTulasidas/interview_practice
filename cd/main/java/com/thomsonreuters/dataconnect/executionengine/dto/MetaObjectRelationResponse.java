package com.thomsonreuters.dataconnect.executionengine.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MetaObjectRelationResponse {
    private List<MetaObjectRelationDTO> metaObjectRelations;

}