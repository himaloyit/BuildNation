package com.himaloyit.buildnation.sac.rbac.controllers.rest;

import com.himaloyit.buildnation.sac.rbac.domain.dto.PermissionDTO;
import com.himaloyit.buildnation.sac.rbac.domain.model.ApiResponse;
import com.himaloyit.buildnation.sac.rbac.domain.model.CreatePermissionRequest;
import com.himaloyit.buildnation.sac.rbac.domain.model.UpdatePermissionRequest;
import com.himaloyit.buildnation.sac.rbac.services.iServices.IPermissionService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/*
 * Author: Rajib Kumer Ghosh
 */

@RestController
@RequestMapping("/api/v1/permissions")
public class PermissionController {

    private final IPermissionService permissionService;

    public PermissionController(IPermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @PreAuthorize("hasAuthority('PERMISSION_permission:create')")
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<PermissionDTO>> createPermission(@Valid @RequestBody CreatePermissionRequest request) {
        PermissionDTO saved = permissionService.createPermission(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Permission created successfully", saved));
    }

    @PreAuthorize("hasAuthority('PERMISSION_permission:view')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PermissionDTO>> getPermissionById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success("Permission found", permissionService.getPermission(id)));
    }

    @PreAuthorize("hasAuthority('PERMISSION_permission:view')")
    @GetMapping
    public ResponseEntity<ApiResponse<Page<PermissionDTO>>> getAllPermissions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<PermissionDTO> permissions = permissionService.getAllPermissions(PageRequest.of(page, size));
        return ResponseEntity.ok(ApiResponse.success("Paged permissions retrieved", permissions));
    }

    @PreAuthorize("hasAuthority('PERMISSION_permission:update')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PermissionDTO>> updatePermission(
            @PathVariable UUID id,
            @RequestBody UpdatePermissionRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Permission updated successfully", permissionService.updatePermission(id, request)));
    }

    @PreAuthorize("hasAuthority('PERMISSION_permission:delete')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePermission(@PathVariable UUID id) {
        permissionService.deletePermission(id);
        return ResponseEntity.ok(ApiResponse.success("Permission deleted successfully", null));
    }
}
