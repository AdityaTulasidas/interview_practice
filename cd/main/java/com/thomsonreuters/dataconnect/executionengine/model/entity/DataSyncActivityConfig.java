package com.thomsonreuters.dataconnect.executionengine.model.entity;

import com.thomsonreuters.dataconnect.executionengine.utils.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "datasync_activity")
public class DataSyncActivityConfig extends BaseEntity {

    @Id
    @Column(name = "id", nullable = false)
    private int id;

    @Column(name = "activity_sys_name", nullable = false)
    private String activitySysName;

    @Column(name = "activity_id", nullable = false)
    private int activityId;

    @Column(name = "datasync_job_sys_name", nullable = false)
    private String datasyncJobSysName;

    @Column(name = "exec_type", nullable = false)
    private String execType;

    @Column(name = "exec_seq", nullable = false)
    private int execSeq;

    @Column(name = "event_type", nullable = false)
    private String eventType;

    @Column(name = "activity_type", nullable = false)
    private String activityType;
}