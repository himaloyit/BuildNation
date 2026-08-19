package com.himaloyit.buildnation.sac.rbac.services.impl;

import com.himaloyit.buildnation.sac.rbac.domain.dto.RoleDTO;
import com.himaloyit.buildnation.sac.rbac.domain.entities.Principal;
import com.himaloyit.buildnation.sac.rbac.domain.entities.PrincipalRole;
import com.himaloyit.buildnation.sac.rbac.domain.entities.Role;
import com.himaloyit.buildnation.sac.rbac.domain.enums.Status;
import com.himaloyit.buildnation.sac.rbac.domain.mapper.IRoleMapper;
import com.himaloyit.buildnation.sac.rbac.domain.repositories.iRepositories.IPrincipalRepository;
import com.himaloyit.buildnation.sac.rbac.domain.repositories.iRepositories.IPrincipalRoleRepository;
import com.himaloyit.buildnation.sac.rbac.domain.repositories.iRepositories.IRoleRepository;
import com.himaloyit.buildnation.sac.rbac.security.CurrentPrincipalContext;
import com.himaloyit.buildnation.sac.rbac.services.iServices.IPrincipalRoleService;
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
 * Assign/revoke only — no full CRUD, this is a join table (Prompt-1 §1 PrincipalRole).
 * Evicts the AuthorizationService's Redis-backed caches manually (keyed by
 * principalCode, not a direct method arg here, so declarative @CacheEvict SpEL
 * doesn't fit) since a role assignment changes both this principal's role list AND,
 * transitively, its permission list.
 */

@Slf4j
@Service
public class PrincipalRoleService implements IPrincipalRoleService {

    private final IPrincipalRoleRepository principalRoleRepository;
    private final IPrincipalRepository principalRepository;
    private final IRoleRepository roleRepository;
    private final IRoleMapper roleMapper;
    private final CacheManager cacheManager;

    public PrincipalRoleService(IPrincipalRoleRepository principalRoleRepository,
                                 IPrincipalRepository principalRepository,
                                 IRoleRepository roleRepository,
                                 IRoleMapper roleMapper,
                                 CacheManager cacheManager) {
        this.principalRoleRepository = principalRoleRepository;
        this.principalRepository = principalRepository;
        this.roleRepository = roleRepository;
        this.roleMapper = roleMapper;
        this.cacheManager = cacheManager;
    }

    @Override
    public List<RoleDTO> getRolesForPrincipal(UUID principalId) {
        return principalRoleRepository.findByPrincipal_PrincipalId(principalId).stream()
                .map(pr -> roleMapper.toDto(pr.getRole()))
                .collect(Collectors.toList());
    }

    @Override
    public RoleDTO assignRole(UUID principalId, UUID roleId) {
        Principal principal = principalRepository.findById(principalId)
                .orElseThrow(() -> new EntityNotFoundException("Principal not found with id: " + principalId));
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new EntityNotFoundException("Role not found with id: " + roleId));

        if (principalRoleRepository.existsByPrincipal_PrincipalIdAndRole_RoleId(principalId, roleId)) {
            throw new DuplicateCodeException("Role " + role.getRoleCode() + " is already assigned to this principal");
        }

        String caller = CurrentPrincipalContext.currentPrincipalCode();
        PrincipalRole principalRole = PrincipalRole.builder()
                .principal(principal)
                .role(role)
                .status(Status.ACTIVE)
                .createdBy(caller)
                .createdDate(LocalDateTime.now())
                .updatedBy(caller)
                .updatedDate(LocalDateTime.now())
                .build();
        principalRoleRepository.save(principalRole);
        evictPrincipalCaches(principal.getPrincipalCode());
        log.info("Role {} assigned to principal {}", role.getRoleCode(), principal.getPrincipalCode());
        return roleMapper.toDto(role);
    }

    @Override
    public void revokeRole(UUID principalId, UUID roleId) {
        Principal principal = principalRepository.findById(principalId)
                .orElseThrow(() -> new EntityNotFoundException("Principal not found with id: " + principalId));
        PrincipalRole principalRole = principalRoleRepository.findByPrincipal_PrincipalIdAndRole_RoleId(principalId, roleId)
                .orElseThrow(() -> new EntityNotFoundException("Role is not assigned to this principal"));

        principalRoleRepository.delete(principalRole);
        evictPrincipalCaches(principal.getPrincipalCode());
        log.info("Role revoked from principal {}", principal.getPrincipalCode());
    }

    private void evictPrincipalCaches(String principalCode) {
        evict("principal-roles", principalCode);
        evict("principal-permissions", principalCode);
    }

    private void evict(String cacheName, String key) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            cache.evict(key);
        }
    }
}
