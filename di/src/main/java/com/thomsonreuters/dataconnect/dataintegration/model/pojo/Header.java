package com.thomsonreuters.dataconnect.dataintegration.model.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.thomsonreuters.dataconnect.dataintegration.model.entity.enums.ExecutionLeg;
import lombok.Data;

import java.util.UUID;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Header {

    @JsonProperty("job_id")
    private UUID jobId;

    @JsonProperty("job_name")
    private String jobName;

    @JsonProperty("regional_exec_id")
    private UUID regionalExecId;

    @JsonProperty("job_exec_id")
    private UUID jobExecId;

    private String source;

    private String target;

    @JsonProperty("meta_object_id")
    private String metaObjectId;

    @JsonProperty("meta_object_sys_name")
    private String metaObjectSysName;

    @JsonProperty("exec_leg")
    private ExecutionLeg execLeg; // SOURCE, TARGET

    @JsonProperty("exec_type")
    private String execType; // REAL_TIME, BATCH

}
