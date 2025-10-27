package com.thomsonreuters.dataconnect.dataintegration.model.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.thomsonreuters.dataconnect.dataintegration.model.entity.enums.DataType;
import com.thomsonreuters.dataconnect.dataintegration.utils.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
@Entity
@Table(name = "meta_object_attribute",uniqueConstraints = {
        @UniqueConstraint(columnNames = {"meta_object_sys_name","system_name"})})
@NoArgsConstructor
@AllArgsConstructor
public class MetaObjectAttribute extends BaseEntity {

    @Setter
    @Getter
    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "systemName")
    @JsonProperty("systemName")
    private String systemName;

    @Column(name = "data_type")
    @Enumerated(EnumType.STRING)
    @JsonProperty("data_type")
    private DataType dataType;

    @JsonProperty("db_column")
    @Column(name = "db_column")
    private String dbColumn;

    @JsonProperty("meta_object_sys_name")
    @ManyToOne
    @JoinColumn(name = "meta_object_sys_name", referencedColumnName = "system_name", nullable = false)
    @JsonIgnore
    private MetaObject metaObject;


    @JsonProperty("description")
    @Column(name = "description")
    private String description;

    @JsonProperty("display_name")
    @Column(name = "display_name")
    private String displayName;

    @JsonProperty("is_mandatory")
    @Column(name = "is_mandatory")
    private boolean isMandatory;

    @JsonProperty("is_primary")
    @Column(name = "is_primary")
    private boolean isPrimary;

    @JsonProperty("is_sys_attribute")
    @Column(name = "is_sys_attribute")
    private boolean isSysAttribute;

    @JsonProperty("logical_key")
    @Column(name = "logical_key")
    private int logicalKey;

    @JsonProperty("seq_num")
    @Column(name = "seq_num")
    private int seqNum;

    @Column(name = "is_event_enabled")
    @JsonProperty("is_event_enabled")
    private boolean isEventEnabled ;

    @Column(name = "order_by")
    @JsonProperty("order_by")
    private Integer orderBy;
}