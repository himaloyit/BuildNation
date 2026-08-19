package com.himaloyit.buildnation.sac.rbac.services.impl;

import com.himaloyit.buildnation.sac.rbac.domain.dto.PermissionDTO;
import com.himaloyit.buildnation.sac.rbac.domain.entities.Permission;
import com.himaloyit.buildnation.sac.rbac.domain.entities.PrincipalRole;
import com.himaloyit.buildnation.sac.rbac.domain.entities.Role;
import com.himaloyit.buildnation.sac.rbac.domain.entities.RolePermission;
import com.himaloyit.buildnation.sac.rbac.domain.enums.Status;
import com.himaloyit.buildnation.sac.rbac.domain.mapper.IPermissionMapper;
import com.himaloyit.buildnation.sac.rbac.domain.repositories.iRepositories.IPermissionRepository;
import com.himaloyit.buildnation.sac.rbac.domain.repositories.iRepositories.IPrincipalRoleRepository;
import com.himaloyit.buildnation.sac.rbac.domain.repositories.iRepositories.IRolePermissionRepository;
import com.himaloyit.buildnation.sac.rbac.domain.repositories.iRepositories.IRoleRepository;
import com.himaloyit.buildnation.sac.rbac.security.CurrentPrincipalContext;
import com.himaloyit.buildnation.sac.rbac.services.iServices.IRolePermissionService;
import com.himaloyit.buildnation.sac.rbac.util.exceptions.DuplicateCodeException;
import com.himaloyit.buildnation.sac.rbac.util.exceptions.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/*
 * Author: Rajib Kumer Ghosh
 *
 * Assign/revoke only — no full CRUD, this is a join table (Prompt-1 §1 RolePermission).
 * A permission change on a Role affects every Principal holding that role, so this
 * evicts the "principal-permissions" cache for each of them (small fan-out query via
 * IPrincipalRoleRepository — acceptable at this scale, see the approved plan).
 */

@Slf4j
@Service
public class RolePermissionService implements IRolePermissionService {

    private final IRolePermissionRepository rolePermissionRepository;
    private final IRoleRepository roleRepository;
    private final IPermissionRepository permissionRepository;
    private final IPrincipalRoleRepository principalRoleRepository;
    private final IPermissionMapper permissionMapper;
    private final CacheManager cacheManager;

    public RolePermissionService(IRolePermissionRepository rolePermissionRepository,
                                  IRoleRepository roleRepository,
                                  IPermissionRepository permissionRepository,
                                  IPrincipalRoleRepository principalRoleRepository,
                                  IPermissionMapper permissionMapper,
                                  CacheManager cacheManager) {
        this.rolePermissionRepository = rolePermissionRepository;
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.principalRoleRepository = principalRoleRepository;
        this.permissionMapper = permissionMapper;
        this.cacheManager = cacheManager;
    }

    @Override
    public List<PermissionDTO> getPermissionsForRole(UUID roleId) {
        return rolePermissionRepository.findByRole_RoleId(roleId).stream()
                .map(rp -> permissionMapper.toDto(rp.getPermission()))
                .collect(Collectors.toList());
    }

    @Override
    public PermissionDTO assignPermission(UUID roleId, UUID permissionId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new EntityNotFoundException("Role not found with id: " + roleId));
        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new EntityNotFoundException("Permission not found with id: " + permissionId));

        if (rolePermissionRepository.existsByRole_RoleIdAndPermission_PermissionId(roleId, permissionId)) {
            throw new DuplicateCodeException("Permission " + permission.getPermissionCode() + " is already assigned to this role");
        }

        String caller = CurrentPrincipalContext.currentPrincipalCode();
        RolePermission rolePermission = RolePermission.builder()
                .role(role)
                .permission(permission)
                .status(Status.ACTIVE)
                .createdBy(caller)
                .createdDate(LocalDateTime.now())
                .updatedBy(caller)
                .updatedDate(LocalDateTime.now())
                .build();
        rolePermissionRepository.save(rolePermission);
        evictPermissionCacheForRoleHolders(roleId);
        log.info("Permission {} assigned to role {}", permission.getPermissionCode(), role.getRoleCode());
        return permissionMapper.toDto(permission);
    }

    @Override
    public void revokePermission(UUID roleId, UUID permissionId) {
        RolePermission rolePermission = rolePermissionRepository.findByRole_RoleIdAndPermission_PermissionId(roleId, permissionId)
                .orElseThrow(() -> new EntityNotFoundException("Permission is not assigned to this role"));

        rolePermissionRepository.delete(rolePermission);
        evictPermissionCacheForRoleHolders(roleId);
        log.info("Permission revoked from role: roleId={}", roleId);
    }

    private void evictPermissionCacheForRoleHolders(UUID roleId) {
        Cache cache = cacheManager.getCache("principal-permissions");
        if (cache == null) {
            return;
        }
        List<PrincipalRole> holders = principalRoleRepository.findByRole_RoleId(roleId);
        for (PrincipalRole holder : holders) {
            cache.evict(holder.getPrincipal().getPrincipalCode());
        }
    }
}
