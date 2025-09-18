package com.aditya.ETLExecutionEngine.model.entity;

import com.aditya.ETLExecutionEngine.model.enums.DataType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MetaObjectAttribute extends BaseEntity {

    private String name;
    private String dbColumnName;
    private DataType dataType;
    private boolean isPrimary;
    private boolean isNullable;
    private String defaultValue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meta_object_id")
    private MetaObject metaObject;
}

