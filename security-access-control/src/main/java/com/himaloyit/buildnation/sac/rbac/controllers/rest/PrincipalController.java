package com.himaloyit.buildnation.sac.rbac.controllers.rest;

import com.himaloyit.buildnation.sac.rbac.domain.dto.PrincipalDTO;
import com.himaloyit.buildnation.sac.rbac.domain.dto.RoleDTO;
import com.himaloyit.buildnation.sac.rbac.domain.enums.PrincipalType;
import com.himaloyit.buildnation.sac.rbac.domain.model.ApiResponse;
import com.himaloyit.buildnation.sac.rbac.domain.model.CreatePrincipalRequest;
import com.himaloyit.buildnation.sac.rbac.domain.model.UpdatePrincipalRequest;
import com.himaloyit.buildnation.sac.rbac.domain.model.UpdatePrincipalStatusRequest;
import com.himaloyit.buildnation.sac.rbac.services.iServices.IPrincipalRoleService;
import com.himaloyit.buildnation.sac.rbac.services.iServices.IPrincipalService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/*
 * Author: Rajib Kumer Ghosh
 */

@RestController
@RequestMapping("/api/v1/principals")
public class PrincipalController {

    private final IPrincipalService principalService;
    private final IPrincipalRoleService principalRoleService;

    public PrincipalController(IPrincipalService principalService, IPrincipalRoleService principalRoleService) {
        this.principalService = principalService;
        this.principalRoleService = principalRoleService;
    }

    @PreAuthorize("hasAuthority('PERMISSION_principal:create')")
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<PrincipalDTO>> createPrincipal(@Valid @RequestBody CreatePrincipalRequest request) {
        PrincipalDTO saved = principalService.createPrincipal(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Principal created successfully", saved));
    }

    @PreAuthorize("hasAuthority('PERMISSION_principal:view')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PrincipalDTO>> getPrincipalById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success("Principal found", principalService.getPrincipal(id)));
    }

    @PreAuthorize("hasAuthority('PERMISSION_principal:view')")
    @GetMapping("/by-code/{principalCode}")
    public ResponseEntity<ApiResponse<PrincipalDTO>> getPrincipalByCode(@PathVariable String principalCode) {
        return ResponseEntity.ok(ApiResponse.success("Principal found", principalService.getPrincipalByCode(principalCode)));
    }

    @PreAuthorize("hasAuthority('PERMISSION_principal:view')")
    @GetMapping
    public ResponseEntity<ApiResponse<Page<PrincipalDTO>>> getAllPrincipals(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<PrincipalDTO> principals = principalService.getAllPrincipals(PageRequest.of(page, size));
        return ResponseEntity.ok(ApiResponse.success("Paged principals retrieved", principals));
    }

    @PreAuthorize("hasAuthority('PERMISSION_principal:view')")
    @GetMapping("/by-type/{type}")
    public ResponseEntity<ApiResponse<Page<PrincipalDTO>>> getPrincipalsByType(
            @PathVariable PrincipalType type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<PrincipalDTO> principals = principalService.getPrincipalsByType(type, PageRequest.of(page, size));
        return ResponseEntity.ok(ApiResponse.success("Principals by type retrieved", principals));
    }

    @PreAuthorize("hasAuthority('PERMISSION_principal:update')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PrincipalDTO>> updatePrincipal(
            @PathVariable UUID id,
            @Valid @RequestBody UpdatePrincipalRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Principal updated successfully", principalService.updatePrincipal(id, request)));
    }

    @PreAuthorize("hasAuthority('PERMISSION_principal:update')")
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<PrincipalDTO>> updatePrincipalStatus(
            @PathVariable UUID id,
            @Valid @RequestBody UpdatePrincipalStatusRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Principal status updated", principalService.updatePrincipalStatus(id, request)));
    }

    @PreAuthorize("hasAuthority('PERMISSION_principal:delete')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePrincipal(@PathVariable UUID id) {
        principalService.deletePrincipal(id);
        return ResponseEntity.ok(ApiResponse.success("Principal deleted successfully", null));
    }

    @PreAuthorize("hasAuthority('PERMISSION_principal:view')")
    @GetMapping("/{principalId}/roles")
    public ResponseEntity<ApiResponse<List<RoleDTO>>> getRolesForPrincipal(@PathVariable UUID principalId) {
        return ResponseEntity.ok(ApiResponse.success("Roles retrieved", principalRoleService.getRolesForPrincipal(principalId)));
    }

    @PreAuthorize("hasAuthority('PERMISSION_principal:update')")
    @PostMapping("/{principalId}/roles/{roleId}")
    public ResponseEntity<ApiResponse<RoleDTO>> assignRole(@PathVariable UUID principalId, @PathVariable UUID roleId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Role assigned successfully", principalRoleService.assignRole(principalId, roleId)));
    }

    @PreAuthorize("hasAuthority('PERMISSION_principal:update')")
    @DeleteMapping("/{principalId}/roles/{roleId}")
    public ResponseEntity<ApiResponse<Void>> revokeRole(@PathVariable UUID principalId, @PathVariable UUID roleId) {
        principalRoleService.revokeRole(principalId, roleId);
        return ResponseEntity.ok(ApiResponse.success("Role revoked successfully", null));
    }
}
