package com.himaloyit.buildnation.sac.rbac.services.iServices;

import com.himaloyit.buildnation.sac.rbac.domain.dto.PermissionDTO;
import com.himaloyit.buildnation.sac.rbac.domain.model.CreatePermissionRequest;
import com.himaloyit.buildnation.sac.rbac.domain.model.UpdatePermissionRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

/*
 * Author: Rajib Kumer Ghosh
 */

public interface IPermissionService {

    PermissionDTO createPermission(CreatePermissionRequest request);

    PermissionDTO getPermission(UUID id);

    Page<PermissionDTO> getAllPermissions(Pageable pageable);

    PermissionDTO updatePermission(UUID id, UpdatePermissionRequest request);

    void deletePermission(UUID id);
}
