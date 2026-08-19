package com.himaloyit.buildnation.sac.rbac.controllers.rest;

import com.himaloyit.buildnation.sac.rbac.domain.dto.ResourceDTO;
import com.himaloyit.buildnation.sac.rbac.domain.model.ApiResponse;
import com.himaloyit.buildnation.sac.rbac.domain.model.CreateResourceRequest;
import com.himaloyit.buildnation.sac.rbac.domain.model.UpdateResourceRequest;
import com.himaloyit.buildnation.sac.rbac.services.iServices.IResourceService;
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
@RequestMapping("/api/v1/resources")
public class ResourceController {

    private final IResourceService resourceService;

    public ResourceController(IResourceService resourceService) {
        this.resourceService = resourceService;
    }

    @PreAuthorize("hasAuthority('PERMISSION_resource:create')")
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<ResourceDTO>> createResource(@Valid @RequestBody CreateResourceRequest request) {
        ResourceDTO saved = resourceService.createResource(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Resource created successfully", saved));
    }

    @PreAuthorize("hasAuthority('PERMISSION_resource:view')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ResourceDTO>> getResourceById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success("Resource found", resourceService.getResource(id)));
    }

    @PreAuthorize("hasAuthority('PERMISSION_resource:view')")
    @GetMapping
    public ResponseEntity<ApiResponse<Page<ResourceDTO>>> getAllResources(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<ResourceDTO> resources = resourceService.getAllResources(PageRequest.of(page, size));
        return ResponseEntity.ok(ApiResponse.success("Paged resources retrieved", resources));
    }

    @PreAuthorize("hasAuthority('PERMISSION_resource:view')")
    @GetMapping("/by-service/{serviceName}")
    public ResponseEntity<ApiResponse<Page<ResourceDTO>>> getResourcesByServiceName(
            @PathVariable String serviceName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<ResourceDTO> resources = resourceService.getResourcesByServiceName(serviceName, PageRequest.of(page, size));
        return ResponseEntity.ok(ApiResponse.success("Resources by service retrieved", resources));
    }

    @PreAuthorize("hasAuthority('PERMISSION_resource:update')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ResourceDTO>> updateResource(
            @PathVariable UUID id,
            @RequestBody UpdateResourceRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Resource updated successfully", resourceService.updateResource(id, request)));
    }

    @PreAuthorize("hasAuthority('PERMISSION_resource:delete')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteResource(@PathVariable UUID id) {
        resourceService.deleteResource(id);
        return ResponseEntity.ok(ApiResponse.success("Resource deleted successfully", null));
    }
}
