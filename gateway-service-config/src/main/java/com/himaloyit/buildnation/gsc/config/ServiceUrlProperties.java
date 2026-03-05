package com.himaloyit.buildnation.gsc.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "services.url")
public class ServiceUrlProperties {

    private String memberManagement;
    private String securityAccessControl;
}
