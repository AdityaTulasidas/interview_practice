package com.thomsonreuters.metadataregistry.configuration;

import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Configuration;

/**
 * This class is used to configure the Swagger UI for the DBAdaptor API. Such as adding
 * the title, description, version.
 */
@Configuration
public class SwaggerConfiguration {

    public OpenAPI metadataRegistryOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("MetadataRegistry API")
                        .description("MetadataRegistry API implemented with Spring Boot RESTful service and documented using springdoc-openapi and Swagger UI")
                        .version("v0.0.1"));

    }
}
