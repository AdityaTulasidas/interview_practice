package com.thomsonreuters.metadataregistry.model.entity;

import com.thomsonreuters.metadataregistry.utils.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "domain")
@Data
public class Domain extends BaseEntity {

    @Id
    @GeneratedValue
    private int id;
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "type_id", nullable = false)
    private int typeId;
    @Column(name = "system_name", unique = true, nullable = false)
    private String systemName;
    @Column(name = "is_system")
    private boolean isSystem;
}
