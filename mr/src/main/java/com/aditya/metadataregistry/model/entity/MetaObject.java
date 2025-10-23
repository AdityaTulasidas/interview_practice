package com.thomsonreuters.metadataregistry.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.thomsonreuters.metadataregistry.utils.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import com.thomsonreuters.dep.api.jpa.mapping.annotations.ApiClass;
import com.thomsonreuters.dep.api.jpa.mapping.annotations.ApiField;

import java.util.*;

@ApiClass(MetaObject.class)
@Entity
@Setter
@Getter
@Table(name = "meta_object", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"db_table","onesource_domain"}),
})

@AllArgsConstructor
public class MetaObject extends BaseEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "description")
    @JsonProperty("description")
    private String description;

    @Column(name = "db_table")
    @JsonProperty("db_table")
    private String dbTable;

    @Column(name = "schema")
    @JsonProperty("schema")
    private String schema;

    @Column(name = "display_name")
    @JsonProperty("display_name")
    @ApiField("displayName")
    private String displayName;

    @ApiField("oneSourceDomain")
    @JsonProperty("onesource_domain")
    @Column(name = "onesource_domain")
    private String oneSourceDomain;


    @JsonProperty("system_name")
    @Column(name = "system_name")
    private String systemName;

    @JsonProperty("domain_object")
    @Column(name = "domain_object")
    private String domainObject;

    @Column(name = "usage_count")
    @JsonProperty("usage_count")
    private int usageCount = 0;

    @Column(name = "is_autogen_id")
    @JsonProperty("is_autogen_id")
    private boolean isAutogenId;

    @Column(name = "is_event_enabled")
    @JsonProperty("is_event_enabled")
    private boolean isEventEnabled ;


    @JsonIgnore
    @OneToMany(mappedBy = "metaObject", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<MetaObjectAttribute> attributes = new HashSet<>();



    @JsonIgnore
    @OneToMany(mappedBy = "parentObject", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<MetaObjectRelation> childRelations= new HashSet<>();


    public MetaObject() {
        this.childRelations = new HashSet<>();

    }




// Getters and Setters


}