package com.himaloyit.buildnation.sac.rbac.services.iServices;

import com.himaloyit.buildnation.sac.rbac.domain.dto.RoleDTO;

import java.util.List;
import java.util.UUID;

/*
 * Author: Rajib Kumer Ghosh
 */

public interface IPrincipalRoleService {

    List<RoleDTO> getRolesForPrincipal(UUID principalId);

    RoleDTO assignRole(UUID principalId, UUID roleId);

    void revokeRole(UUID principalId, UUID roleId);
}
