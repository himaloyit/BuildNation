package com.himaloyit.buildnation.ui.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Base URL of the API Gateway (gateway-service-config) — the UI never calls
 * individual microservices directly, per Doc/Prompt/Build_Nation_Vaadin_UI_Prompt.docx §5.
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "gateway")
public class GatewayProperties {
    private String baseUrl = "http://localhost:8081";
}
