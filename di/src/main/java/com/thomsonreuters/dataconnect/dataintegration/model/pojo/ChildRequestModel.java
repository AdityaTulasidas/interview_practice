package com.thomsonreuters.dataconnect.dataintegration.model.pojo;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.thomsonreuters.dataconnect.dataintegration.model.entity.enums.ExecType;
import com.thomsonreuters.dataconnect.dataintegration.model.entity.enums.OperationType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChildRequestModel {

    @JsonProperty("meta_object_name")
    private String metaObjectName;

    @JsonProperty("customer_tenant_sys_name")
    private String customerTenantSysName;

    @JsonProperty("operation_type")
    private OperationType operationType;

    @JsonProperty("exec_type")
    private ExecType execType;

    @JsonProperty("request_data_unit_list")
    private RequestBodyDataUnitList requestDataUnitList;

    @JsonProperty("source_tenant_id")
    private String sourceTenantId;

    @JsonProperty("client_id")
    private String clientId;

    @JsonProperty("when_changed")
    private LocalDateTime whenChanged;

    @JsonProperty("is_event_enabled")
    private boolean isEventEnabled;

    @JsonProperty("parent_meta_object_sys_name")
    private String parentMetaObjectSysName;
}