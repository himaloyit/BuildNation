package com.himaloyit.buildnation.sac.rbac.services.iServices;

import com.himaloyit.buildnation.sac.rbac.domain.dto.RoleDTO;
import com.himaloyit.buildnation.sac.rbac.domain.model.CreateRoleRequest;
import com.himaloyit.buildnation.sac.rbac.domain.model.UpdateRoleRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

/*
 * Author: Rajib Kumer Ghosh
 */

public interface IRoleService {

    RoleDTO createRole(CreateRoleRequest request);

    RoleDTO getRole(UUID id);

    RoleDTO getRoleByCode(String roleCode);

    Page<RoleDTO> getAllRoles(Pageable pageable);

    RoleDTO updateRole(UUID id, UpdateRoleRequest request);

    void deleteRole(UUID id);
}
