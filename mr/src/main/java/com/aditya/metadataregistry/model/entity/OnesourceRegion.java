package com.thomsonreuters.metadataregistry.model.entity;

import com.thomsonreuters.metadataregistry.utils.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "onesource_region")
@Data
public class OnesourceRegion extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name="id")
    private int id;
    @Column(name="system_name")
    private String systemName;
    @Column(name="display_name")
    private String displayName;
    @Column(name="description")
    private String description;
}
