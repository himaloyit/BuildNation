package com.himaloyit.buildnation.sac.rbac.services.iServices;

import com.himaloyit.buildnation.sac.rbac.domain.dto.PermissionDTO;

import java.util.List;
import java.util.UUID;

/*
 * Author: Rajib Kumer Ghosh
 */

public interface IRolePermissionService {

    List<PermissionDTO> getPermissionsForRole(UUID roleId);

    PermissionDTO assignPermission(UUID roleId, UUID permissionId);

    void revokePermission(UUID roleId, UUID permissionId);
}
