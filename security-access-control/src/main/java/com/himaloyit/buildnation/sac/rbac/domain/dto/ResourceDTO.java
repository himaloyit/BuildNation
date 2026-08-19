package com.himaloyit.buildnation.sac.rbac.domain.dto;

import com.himaloyit.buildnation.sac.rbac.domain.enums.HttpMethod;
import com.himaloyit.buildnation.sac.rbac.domain.enums.ResourceType;
import com.himaloyit.buildnation.sac.rbac.domain.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/*
 * Author: Rajib Kumer Ghosh
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResourceDTO {
    private UUID resourceId;
    private String resourceCode;
    private String resourceName;
    private String serviceName;
    private String apiPath;
    private HttpMethod httpMethod;
    private ResourceType resourceType;
    private Status status;
    private String createdBy;
    private LocalDateTime createdDate;
    private String updatedBy;
    private LocalDateTime updatedDate;
}
