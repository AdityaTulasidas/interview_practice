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
@Table(name = "transformation_function")
public class TransformationFunction extends BaseEntity {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "system_name", nullable = false, unique = true)
    private String systemName;
    @Column(name = "description")
    private String description;
    @Column(name = "display_name")
    private String displayName;
    @Column(name = "type", nullable = false)
    private String type;
    @Column(name = "onesource_domain")
    private String onesourceDomain;

}
