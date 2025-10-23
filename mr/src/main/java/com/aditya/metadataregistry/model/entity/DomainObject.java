package com.thomsonreuters.metadataregistry.model.entity;

import com.thomsonreuters.metadataregistry.utils.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "domain_object")
@Data
public class DomainObject extends BaseEntity {
    @Id
    @GeneratedValue
    private int id;
    @Column(name = "system_name")
    private String systemName;
    @Column(name = "object_name")
    private String objectName;
    @Column(name = "domain_sys_name")
    private String domainSysName;
    @Column(name = "description")
    private String description;

}