package com.himaloyit.buildnation.sac.rbac.domain.dto;

import com.himaloyit.buildnation.sac.rbac.domain.enums.Action;
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
public class PermissionDTO {
    private UUID permissionId;
    private String permissionCode;
    private String permissionName;
    private Action action;
    private UUID resourceId;
    private String resourceCode;
    private Status status;
    private String createdBy;
    private LocalDateTime createdDate;
    private String updatedBy;
    private LocalDateTime updatedDate;
}
