package com.thomsonreuters.metadataregistry.configuration;

import com.thomsonreuters.dep.api.jpa.mapping.JPAFieldMapper;
import com.thomsonreuters.dep.api.spring.configuration.JPAFieldMapperConfiguration;
import com.thomsonreuters.metadataregistry.model.dto.MetaObjectDTO;
import com.thomsonreuters.metadataregistry.model.entity.DataSource;
import com.thomsonreuters.metadataregistry.model.entity.MetaObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class FieldMapperConfig {

    @Bean
    public JPAFieldMapperConfiguration jpaFieldMapperConfiguration() {
        JPAFieldMapperConfiguration configuration = new JPAFieldMapperConfiguration();
        configuration.addAllMappings(

                MetaObject.class, DataSource.class

        );
        log.info("Bean 'JPAFieldMapperConfiguration' loaded.");
        return configuration;

    }
}