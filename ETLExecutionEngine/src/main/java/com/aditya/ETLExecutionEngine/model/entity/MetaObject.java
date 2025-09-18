package com.aditya.ETLExecutionEngine.model.entity;

import com.aditya.ETLExecutionEngine.model.enums.OnesourceDomain;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MetaObject extends BaseEntity {

    private String description;
    private String tableName;

    @Enumerated(EnumType.STRING)
    private OnesourceDomain oneSourceDomain;

    private String systemName;
    private String displayName;
    private boolean isAutogenId;
    private Integer usageCount;
    private boolean isEventEnabled;

    @OneToMany(mappedBy = "metaObject", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<MetaObjectAttribute> attributes = new HashSet<>();

    @OneToMany(mappedBy = "parentObject", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<MetaObjectRelation> childRelations = new HashSet<>();
}

