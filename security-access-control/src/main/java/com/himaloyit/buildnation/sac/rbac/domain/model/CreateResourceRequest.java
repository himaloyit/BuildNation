package com.himaloyit.buildnation.sac.rbac.domain.model;

import com.himaloyit.buildnation.sac.rbac.domain.enums.HttpMethod;
import com.himaloyit.buildnation.sac.rbac.domain.enums.ResourceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
 * Author: Rajib Kumer Ghosh
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateResourceRequest {

    @NotBlank(message = "Resource code is required")
    private String resourceCode;

    @NotBlank(message = "Resource name is required")
    private String resourceName;

    @NotBlank(message = "Service name is required")
    private String serviceName;

    @NotBlank(message = "API path is required")
    private String apiPath;

    @NotNull(message = "HTTP method is required")
    private HttpMethod httpMethod;

    @NotNull(message = "Resource type is required")
    private ResourceType resourceType;
}
