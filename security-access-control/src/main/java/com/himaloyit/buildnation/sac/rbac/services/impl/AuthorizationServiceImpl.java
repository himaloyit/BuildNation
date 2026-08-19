package com.himaloyit.buildnation.sac.rbac.services.impl;

import com.himaloyit.buildnation.sac.rbac.domain.entities.Permission;
import com.himaloyit.buildnation.sac.rbac.domain.entities.Role;
import com.himaloyit.buildnation.sac.rbac.domain.enums.HttpMethod;
import com.himaloyit.buildnation.sac.rbac.domain.repositories.iRepositories.IPermissionRepository;
import com.himaloyit.buildnation.sac.rbac.domain.repositories.iRepositories.IRoleRepository;
import com.himaloyit.buildnation.sac.rbac.services.iServices.IAuthorizationService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/*
 * Author: Rajib Kumer Ghosh
 */

@Service
public class AuthorizationServiceImpl implements IAuthorizationService {

    private final IRoleRepository roleRepository;
    private final IPermissionRepository permissionRepository;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    public AuthorizationServiceImpl(IRoleRepository roleRepository, IPermissionRepository permissionRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }

    @Override
    public boolean hasPermission(String principalCode, String permissionCode) {
        return getPermissionCodes(principalCode).contains(permissionCode);
    }

    @Override
    public boolean hasAccess(String principalCode, String apiPath, HttpMethod httpMethod) {
        List<Permission> permissions = permissionRepository.findActivePermissionsByPrincipalCode(principalCode);
        return permissions.stream()
                .filter(p -> p.getResource() != null && p.getResource().getHttpMethod() == httpMethod)
                .anyMatch(p -> pathMatcher.match(p.getResource().getApiPath(), apiPath));
    }

    @Override
    @Cacheable(value = "principal-permissions", key = "#principalCode")
    public Set<String> getPermissionCodes(String principalCode) {
        return permissionRepository.findActivePermissionsByPrincipalCode(principalCode).stream()
                .map(Permission::getPermissionCode)
                .collect(Collectors.toSet());
    }

    @Override
    @Cacheable(value = "principal-roles", key = "#principalCode")
    public Set<String> getRoleCodes(String principalCode) {
        return roleRepository.findActiveRolesByPrincipalCode(principalCode).stream()
                .map(Role::getRoleCode)
                .collect(Collectors.toSet());
    }
}
