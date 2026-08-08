package com.himaloyit.buildnation.cdm.controllers.rest;

import com.himaloyit.buildnation.cdm.domain.dto.ProjectDTO;
import com.himaloyit.buildnation.cdm.domain.enums.ProjectStatus;
import com.himaloyit.buildnation.cdm.domain.model.*;
import com.himaloyit.buildnation.cdm.services.iServices.IProjectService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/projects")
public class ProjectController {

    private final IProjectService iProjectService;

    public ProjectController(IProjectService iProjectService) {
        this.iProjectService = iProjectService;
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<ProjectDTO>> createProject(@Valid @RequestBody CreateProjectRequest request) {
        ProjectDTO saved = iProjectService.createProject(request);
        return ResponseEntity.ok(ApiResponse.success("Project created successfully", saved));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProjectDTO>> getProjectById(@PathVariable UUID id) {
        ProjectDTO project = iProjectService.getProject(id);
        return ResponseEntity.ok(ApiResponse.success("Project found", project));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<ProjectDTO>>> getAllProjects(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<ProjectDTO> pagedProjects = iProjectService.getAllProjects(PageRequest.of(page, size));
        return ResponseEntity.ok(ApiResponse.success("Paged projects retrieved", pagedProjects));
    }

    @GetMapping("/by-category/{categoryId}")
    public ResponseEntity<ApiResponse<Page<ProjectDTO>>> getProjectsByCategory(
            @PathVariable UUID categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<ProjectDTO> projects = iProjectService.getProjectsByCategory(categoryId, PageRequest.of(page, size));
        return ResponseEntity.ok(ApiResponse.success("Projects by category retrieved", projects));
    }

    @GetMapping("/by-status/{status}")
    public ResponseEntity<ApiResponse<Page<ProjectDTO>>> getProjectsByStatus(
            @PathVariable ProjectStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<ProjectDTO> projects = iProjectService.getProjectsByStatus(status, PageRequest.of(page, size));
        return ResponseEntity.ok(ApiResponse.success("Projects by status retrieved", projects));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProjectDTO>> updateProject(
            @PathVariable UUID id,
            @RequestBody UpdateProjectRequest request) {
        ProjectDTO updated = iProjectService.updateProject(id, request);
        return ResponseEntity.ok(ApiResponse.success("Project updated successfully", updated));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<ProjectDTO>> updateProjectStatus(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateProjectStatusRequest request) {
        ProjectDTO updated = iProjectService.updateProjectStatus(id, request);
        return ResponseEntity.ok(ApiResponse.success("Project status updated", updated));
    }

    @PatchMapping("/{id}/priority")
    public ResponseEntity<ApiResponse<ProjectDTO>> updateProjectPriority(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateProjectPriorityRequest request) {
        ProjectDTO updated = iProjectService.updateProjectPriority(id, request);
        return ResponseEntity.ok(ApiResponse.success("Project priority score updated", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProject(@PathVariable UUID id) {
        iProjectService.deleteProject(id);
        return ResponseEntity.ok(ApiResponse.success("Project deleted successfully", null));
    }

    @GetMapping("/priority-queue")
    public ResponseEntity<ApiResponse<Page<ProjectDTO>>> getPriorityQueue(
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<ProjectDTO> queue = iProjectService.getPriorityQueue(categoryId, PageRequest.of(page, size));
        return ResponseEntity.ok(ApiResponse.success("Priority queue retrieved", queue));
    }

    @PostMapping("/priority-queue/recalculate")
    public ResponseEntity<ApiResponse<Integer>> recalculatePriorityQueue(
            @RequestParam(required = false) UUID categoryId
    ) {
        int ranked = iProjectService.recalculatePriorityQueue(categoryId);
        return ResponseEntity.ok(ApiResponse.success("Priority queue recalculated", ranked));
    }
}
