package com.aditya.ETLExecutionEngine.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MetaObjectRelation extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "parent_object_id")
    private MetaObject parentObject;

    @ManyToOne
    @JoinColumn(name = "child_object_id")
    private MetaObject childObject;

    private String relationType;
    private String parentObjRelCol;
    private String childObjRelCol;
}

