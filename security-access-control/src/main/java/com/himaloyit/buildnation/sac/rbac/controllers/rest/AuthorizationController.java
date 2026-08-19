package com.himaloyit.buildnation.sac.rbac.controllers.rest;

import com.himaloyit.buildnation.sac.rbac.domain.enums.HttpMethod;
import com.himaloyit.buildnation.sac.rbac.domain.model.ApiResponse;
import com.himaloyit.buildnation.sac.rbac.services.iServices.IAuthorizationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

/*
 * Author: Rajib Kumer Ghosh
 *
 * Read-only — directly exposes Prompt-1 §4's AuthorizationService questions.
 */

@RestController
@RequestMapping("/api/v1/authz")
public class AuthorizationController {

    private final IAuthorizationService authorizationService;

    public AuthorizationController(IAuthorizationService authorizationService) {
        this.authorizationService = authorizationService;
    }

    @GetMapping("/principals/{principalCode}/permissions")
    public ResponseEntity<ApiResponse<Set<String>>> getPermissions(@PathVariable String principalCode) {
        return ResponseEntity.ok(ApiResponse.success("Permissions retrieved", authorizationService.getPermissionCodes(principalCode)));
    }

    @GetMapping("/principals/{principalCode}/roles")
    public ResponseEntity<ApiResponse<Set<String>>> getRoles(@PathVariable String principalCode) {
        return ResponseEntity.ok(ApiResponse.success("Roles retrieved", authorizationService.getRoleCodes(principalCode)));
    }

    @GetMapping("/check")
    public ResponseEntity<ApiResponse<Boolean>> check(
            @RequestParam String principalCode,
            @RequestParam String permissionCode) {
        boolean result = authorizationService.hasPermission(principalCode, permissionCode);
        return ResponseEntity.ok(ApiResponse.success("Permission check result", result));
    }

    @GetMapping("/check-access")
    public ResponseEntity<ApiResponse<Boolean>> checkAccess(
            @RequestParam String principalCode,
            @RequestParam String apiPath,
            @RequestParam HttpMethod httpMethod) {
        boolean result = authorizationService.hasAccess(principalCode, apiPath, httpMethod);
        return ResponseEntity.ok(ApiResponse.success("Access check result", result));
    }
}
