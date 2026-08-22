package com.himaloyit.buildnation.ui.client.prj;

import com.himaloyit.buildnation.ui.client.support.AuthorizedGatewayClient;
import com.himaloyit.buildnation.ui.dto.ApiResponseDTO;
import com.himaloyit.buildnation.ui.dto.PageResponseDTO;
import com.himaloyit.buildnation.ui.dto.prj.CreateProjectRequest;
import com.himaloyit.buildnation.ui.dto.prj.ProjectDTO;
import com.himaloyit.buildnation.ui.dto.prj.ProjectStatus;
import com.himaloyit.buildnation.ui.dto.prj.UpdateProjectPriorityRequest;
import com.himaloyit.buildnation.ui.dto.prj.UpdateProjectRequest;
import com.himaloyit.buildnation.ui.dto.prj.UpdateProjectStatusRequest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

import java.util.UUID;

/** Talks to constituency-development-management's /api/v1/projects endpoints through the Gateway. */
@Component
public class ProjectClient {

    private final AuthorizedGatewayClient client;

    public ProjectClient(AuthorizedGatewayClient client) {
        this.client = client;
    }

    public PageResponseDTO<ProjectDTO> getAll(int page, int size) {
        return client.get("/api/v1/projects?page=" + page + "&size=" + size,
                new ParameterizedTypeReference<ApiResponseDTO<PageResponseDTO<ProjectDTO>>>() {
                });
    }

    public PageResponseDTO<ProjectDTO> getByCategory(UUID categoryId, int page, int size) {
        return client.get("/api/v1/projects/by-category/" + categoryId + "?page=" + page + "&size=" + size,
                new ParameterizedTypeReference<ApiResponseDTO<PageResponseDTO<ProjectDTO>>>() {
                });
    }

    public PageResponseDTO<ProjectDTO> getByStatus(ProjectStatus status, int page, int size) {
        return client.get("/api/v1/projects/by-status/" + status + "?page=" + page + "&size=" + size,
                new ParameterizedTypeReference<ApiResponseDTO<PageResponseDTO<ProjectDTO>>>() {
                });
    }

    public ProjectDTO getById(UUID id) {
        return client.get("/api/v1/projects/" + id,
                new ParameterizedTypeReference<ApiResponseDTO<ProjectDTO>>() {
                });
    }

    public ProjectDTO create(CreateProjectRequest request) {
        return client.post("/api/v1/projects/create", request,
                new ParameterizedTypeReference<ApiResponseDTO<ProjectDTO>>() {
                });
    }

    public ProjectDTO update(UUID id, UpdateProjectRequest request) {
        return client.put("/api/v1/projects/" + id, request,
                new ParameterizedTypeReference<ApiResponseDTO<ProjectDTO>>() {
                });
    }

    public ProjectDTO updateStatus(UUID id, ProjectStatus status) {
        return client.patch("/api/v1/projects/" + id + "/status", new UpdateProjectStatusRequest(status),
                new ParameterizedTypeReference<ApiResponseDTO<ProjectDTO>>() {
                });
    }

    public ProjectDTO updatePriority(UUID id, Integer priorityScore) {
        return client.patch("/api/v1/projects/" + id + "/priority", new UpdateProjectPriorityRequest(priorityScore),
                new ParameterizedTypeReference<ApiResponseDTO<ProjectDTO>>() {
                });
    }

    public PageResponseDTO<ProjectDTO> getPriorityQueue(UUID categoryId, int page, int size) {
        String uri = "/api/v1/projects/priority-queue?page=" + page + "&size=" + size
                + (categoryId == null ? "" : "&categoryId=" + categoryId);
        return client.get(uri, new ParameterizedTypeReference<ApiResponseDTO<PageResponseDTO<ProjectDTO>>>() {
        });
    }

    public int recalculatePriorityQueue(UUID categoryId) {
        String uri = "/api/v1/projects/priority-queue/recalculate" + (categoryId == null ? "" : "?categoryId=" + categoryId);
        return client.post(uri, null, new ParameterizedTypeReference<ApiResponseDTO<Integer>>() {
        });
    }

    public void delete(UUID id) {
        client.delete("/api/v1/projects/" + id);
    }
}
