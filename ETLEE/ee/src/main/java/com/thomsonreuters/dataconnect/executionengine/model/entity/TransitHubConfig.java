package com.thomsonreuters.dataconnect.executionengine.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "transithub_config")
@Setter
@Getter
public class TransitHubConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "domain_object", nullable = false)
    private String domainObject;

    @Column(name = "meta_object_sys_name", nullable = false)
    private String metaObjectSysName;

    @Column(name = "publisher_id", nullable = false)
    private String publisherId;

    @Column(name = "subscription_key", nullable = false)
    private String subscriptionKey;

    @Column(name = "private_key", nullable = false)
    private String privateKey;

    @Column(name = "issuer", nullable = false)
    private String issuer;


}