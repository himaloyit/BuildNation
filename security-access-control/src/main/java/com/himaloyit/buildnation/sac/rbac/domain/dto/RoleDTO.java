package com.himaloyit.buildnation.sac.rbac.domain.dto;

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
public class RoleDTO {
    private UUID roleId;
    private String roleCode;
    private String roleName;
    private String description;
    private Status status;
    private String createdBy;
    private LocalDateTime createdDate;
    private String updatedBy;
    private LocalDateTime updatedDate;
}
