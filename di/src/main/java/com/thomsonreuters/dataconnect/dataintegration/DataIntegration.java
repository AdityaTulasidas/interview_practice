package com.thomsonreuters.dataconnect.dataintegration;

import com.thomsonreuters.dataconnect.dataintegration.configuration.DataIntegrationRegionConfig;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
@Slf4j
@SpringBootApplication
@EnableConfigurationProperties(DataIntegrationRegionConfig.class)
public class DataIntegration {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(DataIntegration.class);
        app.setBannerMode(Banner.Mode.OFF);
        MDC.put("logging.application.name","dataintegration");
        app.run(args);
    }

    @Bean
    public RestTemplateBuilder restTemplateBuilder() {
        return new RestTemplateBuilder();
    }

    // Add this bean configuration
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate(new HttpComponentsClientHttpRequestFactory());
    }
}
