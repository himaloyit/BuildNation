package com.himaloyit.buildnation.ui.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
@EnableConfigurationProperties(GatewayProperties.class)
public class RestClientConfig {

    @Bean
    public RestClient gatewayRestClient(GatewayProperties gatewayProperties) {
        return RestClient.builder()
                .baseUrl(gatewayProperties.getBaseUrl())
                .build();
    }
}
