package com.himaloyit.buildnation.sac.rbac.domain.model;

import com.himaloyit.buildnation.sac.rbac.domain.enums.Action;
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
public class UpdatePermissionRequest {

    private String permissionName;
    private Action action;
    private UUID resourceId;
}
