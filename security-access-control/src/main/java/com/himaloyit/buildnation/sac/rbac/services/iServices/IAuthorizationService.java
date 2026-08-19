package com.himaloyit.buildnation.sac.rbac.services.iServices;

import com.himaloyit.buildnation.sac.rbac.domain.enums.HttpMethod;

import java.util.Set;

/*
 * Author: Rajib Kumer Ghosh
 *
 * Prompt-1 §4 — joins Principal -> PrincipalRole -> Role -> RolePermission -> Permission
 * (-> Resource). Backing implementation caches getPermissionCodes/getRoleCodes in
 * Redis (see RedisConfig), evicted from PrincipalRoleService/RolePermissionService on
 * assignment changes — DB is the source of truth, no permissions are embedded in the
 * JWT itself (Prompt-1 §5).
 */

public interface IAuthorizationService {

    boolean hasPermission(String principalCode, String permissionCode);

    boolean hasAccess(String principalCode, String apiPath, HttpMethod httpMethod);

    Set<String> getPermissionCodes(String principalCode);

    Set<String> getRoleCodes(String principalCode);
}
