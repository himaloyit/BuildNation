package com.himaloyit.buildnation.sac.rbac.controllers.rest;

import com.himaloyit.buildnation.sac.rbac.domain.dto.PermissionDTO;
import com.himaloyit.buildnation.sac.rbac.domain.dto.RoleDTO;
import com.himaloyit.buildnation.sac.rbac.domain.model.ApiResponse;
import com.himaloyit.buildnation.sac.rbac.domain.model.CreateRoleRequest;
import com.himaloyit.buildnation.sac.rbac.domain.model.UpdateRoleRequest;
import com.himaloyit.buildnation.sac.rbac.services.iServices.IRolePermissionService;
import com.himaloyit.buildnation.sac.rbac.services.iServices.IRoleService;
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
@RequestMapping("/api/v1/roles")
public class RoleController {

    private final IRoleService roleService;
    private final IRolePermissionService rolePermissionService;

    public RoleController(IRoleService roleService, IRolePermissionService rolePermissionService) {
        this.roleService = roleService;
        this.rolePermissionService = rolePermissionService;
    }

    @PreAuthorize("hasAuthority('PERMISSION_role:create')")
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<RoleDTO>> createRole(@Valid @RequestBody CreateRoleRequest request) {
        RoleDTO saved = roleService.createRole(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Role created successfully", saved));
    }

    @PreAuthorize("hasAuthority('PERMISSION_role:view')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RoleDTO>> getRoleById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success("Role found", roleService.getRole(id)));
    }

    @PreAuthorize("hasAuthority('PERMISSION_role:view')")
    @GetMapping("/by-code/{roleCode}")
    public ResponseEntity<ApiResponse<RoleDTO>> getRoleByCode(@PathVariable String roleCode) {
        return ResponseEntity.ok(ApiResponse.success("Role found", roleService.getRoleByCode(roleCode)));
    }

    @PreAuthorize("hasAuthority('PERMISSION_role:view')")
    @GetMapping
    public ResponseEntity<ApiResponse<Page<RoleDTO>>> getAllRoles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<RoleDTO> roles = roleService.getAllRoles(PageRequest.of(page, size));
        return ResponseEntity.ok(ApiResponse.success("Paged roles retrieved", roles));
    }

    @PreAuthorize("hasAuthority('PERMISSION_role:update')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<RoleDTO>> updateRole(
            @PathVariable UUID id,
            @RequestBody UpdateRoleRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Role updated successfully", roleService.updateRole(id, request)));
    }

    @PreAuthorize("hasAuthority('PERMISSION_role:delete')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteRole(@PathVariable UUID id) {
        roleService.deleteRole(id);
        return ResponseEntity.ok(ApiResponse.success("Role deleted successfully", null));
    }

    @PreAuthorize("hasAuthority('PERMISSION_role:view')")
    @GetMapping("/{roleId}/permissions")
    public ResponseEntity<ApiResponse<List<PermissionDTO>>> getPermissionsForRole(@PathVariable UUID roleId) {
        return ResponseEntity.ok(ApiResponse.success("Permissions retrieved", rolePermissionService.getPermissionsForRole(roleId)));
    }

    @PreAuthorize("hasAuthority('PERMISSION_role:update')")
    @PostMapping("/{roleId}/permissions/{permissionId}")
    public ResponseEntity<ApiResponse<PermissionDTO>> assignPermission(@PathVariable UUID roleId, @PathVariable UUID permissionId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Permission assigned successfully", rolePermissionService.assignPermission(roleId, permissionId)));
    }

    @PreAuthorize("hasAuthority('PERMISSION_role:update')")
    @DeleteMapping("/{roleId}/permissions/{permissionId}")
    public ResponseEntity<ApiResponse<Void>> revokePermission(@PathVariable UUID roleId, @PathVariable UUID permissionId) {
        rolePermissionService.revokePermission(roleId, permissionId);
        return ResponseEntity.ok(ApiResponse.success("Permission revoked successfully", null));
    }
}
