package com.thomsonreuters.metadataregistry.model.entity;

import com.thomsonreuters.metadataregistry.utils.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "domain_type")
@Data
public class DomainType extends BaseEntity {
    @Id
    @GeneratedValue
    private int id;
    @Column(name = "type")
    private String type;
    @Column(name = "system_name")
    private String systemName;
    @Column(name = "is_system")
    private boolean isSystem;

}
