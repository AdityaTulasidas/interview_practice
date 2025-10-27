package com.thomsonreuters.dataconnect.executionengine.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.thomsonreuters.dataconnect.executionengine.utils.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "meta_object_relation", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"system_name"})
})
public class MetaObjectRelation extends BaseEntity {
    // Primary Key
    @Id
    @JsonIgnore
    @GeneratedValue
    private UUID id;
    //Description of the relationship between two Syncobjects
    @Column(name = "description")
    @JsonProperty("description")
    private String description;

    @JsonProperty("system_name")
    @Column(name = "system_name", unique = true)
    private String systemName;

// Parent MetaObject
    @JsonProperty("parent_object")
    @ManyToOne
    @JoinColumn(name = "parent_object_id", nullable = false)
    private MetaObject parentObject;

    @JsonProperty("parent_obj_rel_col")
    @Column(name = "parent_obj_rel_col")
    private String parentObjRelCol;

    @JsonProperty("child_obj_rel_col")
    @Column(name = "child_obj_rel_col")
    private String childObjRelCol;

    // Child MetaObject
    @ManyToOne
    @JoinColumn(name = "child_object_id", nullable = false)
    @JsonProperty("child_object")
    private MetaObject childObject;

    // Type of relationship between two SyncObjects
    @Column(name = "relation_type")
    @JsonProperty("relation_type")
    private String relationType;



}