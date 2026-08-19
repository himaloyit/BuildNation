package com.himaloyit.buildnation.sac.rbac.domain.repositories.iRepositories;

import com.himaloyit.buildnation.sac.rbac.domain.entities.RolePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/*
 * Author: Rajib Kumer Ghosh
 */

@Repository
public interface IRolePermissionRepository extends JpaRepository<RolePermission, UUID> {

    boolean existsByRole_RoleIdAndPermission_PermissionId(UUID roleId, UUID permissionId);

    Optional<RolePermission> findByRole_RoleIdAndPermission_PermissionId(UUID roleId, UUID permissionId);

    List<RolePermission> findByRole_RoleId(UUID roleId);
}
