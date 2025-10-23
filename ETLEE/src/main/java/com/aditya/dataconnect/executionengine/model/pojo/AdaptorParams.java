package com.aditya.dataconnect.executionengine.model.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.aditya.dataconnect.executionengine.dto.MetaObjectDTO;
import com.aditya.dataconnect.executionengine.dto.MetaRelationMetaModelDTO;
import com.aditya.dataconnect.executionengine.model.entity.enums.DatabaseVendor;
import lombok.*;

import java.util.List;

@Data
public class AdaptorParams {

    @JsonProperty("db_user")
    private String dbUser;
    @JsonProperty("db_type")
    private DatabaseVendor dbType;
    @JsonProperty("data_source")
    private String dataSource;
    @JsonProperty("Meta_Object_Attribute")
    private MetaRelationMetaModelDTO metaObjectRelations;
    @JsonProperty("Meta_Object")
    private MetaObjectDTO metaObject;
    @JsonProperty("parent_Meta_object")
    private List<MetaObjectDTO> parentMetaObject;
    @JsonProperty("child_Meta_object")
    private List<MetaObjectDTO> childMetaObject;
    @JsonProperty("select_statements")
    private List<String> selectStatements;
    @JsonProperty("SSM_key_parameter")
    private String ssmKeyParameter;

    @Override
    public String toString() {
        return "AdaptorParams{" +
                "db_user='" + dbUser + '\'' +
                ", db_type='" + dbType + '\'' +
                ", data_source='" + dataSource + '\'' +
                ", SSM_key_parameter='" + ssmKeyParameter + '\'' +
                ", " +
                '}';
    }
}