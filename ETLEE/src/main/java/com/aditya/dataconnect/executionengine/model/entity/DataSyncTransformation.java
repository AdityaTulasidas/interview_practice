package com.aditya.dataconnect.executionengine.model.entity;


import com.aditya.dataconnect.executionengine.utils.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.UUID;
@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "datasync_transformation")
public class DataSyncTransformation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "execution_seq", nullable = false)
    private int executionSeq;

    @Column(name = "exec_leg", nullable = false, length = 50)
    private String execLeg;

    @Column(name = "type", nullable = false, length = 50)
    private String type;

    @Column(name = "function_params", nullable = false, length = 500)
    private String functionParams;

    @Column(name = "datasync_job_sys_name", nullable = false, length = 255)
    private String datasyncJobSysName;

    @Column(name = "region", nullable = false, length = 50)
    private String region;

    @Column(name = "transform_func_sys_name", nullable = false, length = 255)
    private String transformFuncSysName;

}