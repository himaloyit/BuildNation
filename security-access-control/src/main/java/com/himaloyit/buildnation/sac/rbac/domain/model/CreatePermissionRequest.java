package com.himaloyit.buildnation.sac.rbac.domain.model;

import com.himaloyit.buildnation.sac.rbac.domain.enums.Action;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/*
 * Author: Rajib Kumer Ghosh
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatePermissionRequest {

    @NotBlank(message = "Permission code is required")
    private String permissionCode;

    @NotBlank(message = "Permission name is required")
    private String permissionName;

    @NotNull(message = "Action is required")
    private Action action;

    @NotNull(message = "Resource id is required")
    private UUID resourceId;
}
