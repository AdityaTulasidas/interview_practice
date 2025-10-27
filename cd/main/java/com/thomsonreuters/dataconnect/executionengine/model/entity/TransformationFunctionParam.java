package com.thomsonreuters.dataconnect.executionengine.model.entity;

import com.thomsonreuters.dataconnect.executionengine.utils.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


@EqualsAndHashCode(callSuper = true)
    @Entity
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
@Table(name = "transformation_function_param")
    public class TransformationFunctionParam extends BaseEntity {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Integer id;
        @Column(name = "transform_func_id", nullable = false)
        private String transformFuncId;
        @Column(name = "system_name", nullable = false, unique = true)
        private String systemName;
        @Column(name = "description")
        private String description;
        @Column(name = "display_name")
        private String displayName;



    }


