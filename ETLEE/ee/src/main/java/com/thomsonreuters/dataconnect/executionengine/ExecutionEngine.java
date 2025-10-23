package com.thomsonreuters.dataconnect.executionengine;


import com.thomsonreuters.dataconnect.executionengine.configuration.DataIntegrationRegionConfig;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
@Slf4j
@SpringBootApplication
@EnableConfigurationProperties(DataIntegrationRegionConfig.class)
public class ExecutionEngine {

    public static void main(String[] args)  {
        SpringApplication app = new SpringApplication(ExecutionEngine.class);
        app.setBannerMode(Banner.Mode.OFF);
        MDC.put("logging.application.name","executionengine");
        app.run(args);
    }

    @Bean
    public RestTemplateBuilder restTemplateBuilder() {
        return new RestTemplateBuilder();
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        RestTemplate restTemplate = builder.build();
        log.info("Bean 'RestTemplate' loaded.");
        return restTemplate;
    }
}
