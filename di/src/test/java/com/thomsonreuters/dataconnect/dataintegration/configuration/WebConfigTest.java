package com.thomsonreuters.dataconnect.dataintegration.configuration;

import org.junit.jupiter.api.Test;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class WebConfigTest {

    @Test
    void testConfigureMessageConverters() {
        WebConfig webConfig = new WebConfig();
        List<HttpMessageConverter<?>> converters = new ArrayList<>();

        webConfig.configureMessageConverters(converters);

        boolean containsMappingJackson2HttpMessageConverter = converters.stream()
                .anyMatch(converter -> converter instanceof MappingJackson2HttpMessageConverter);

        assertTrue(containsMappingJackson2HttpMessageConverter, "Converters should contain MappingJackson2HttpMessageConverter");
    }
}