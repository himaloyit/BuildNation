package com.himaloyit.buildnation.ui.client.region;

import com.himaloyit.buildnation.ui.client.support.AuthorizedGatewayClient;
import com.himaloyit.buildnation.ui.dto.ApiResponseDTO;
import com.himaloyit.buildnation.ui.dto.PageResponseDTO;
import com.himaloyit.buildnation.ui.dto.region.CreateUnionRequest;
import com.himaloyit.buildnation.ui.dto.region.UnionDTO;
import com.himaloyit.buildnation.ui.dto.region.UpdateUnionRequest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

import java.util.UUID;

/** Talks to constituency-development-management's /api/v1/unions endpoints through the Gateway. */
@Component
public class UnionClient {

    private final AuthorizedGatewayClient client;

    public UnionClient(AuthorizedGatewayClient client) {
        this.client = client;
    }

    public PageResponseDTO<UnionDTO> getAll(int page, int size) {
        return client.get("/api/v1/unions?page=" + page + "&size=" + size,
                new ParameterizedTypeReference<ApiResponseDTO<PageResponseDTO<UnionDTO>>>() {
                });
    }

    public PageResponseDTO<UnionDTO> getByUpazila(UUID upazilaId, int page, int size) {
        return client.get("/api/v1/unions/by-upazila/" + upazilaId + "?page=" + page + "&size=" + size,
                new ParameterizedTypeReference<ApiResponseDTO<PageResponseDTO<UnionDTO>>>() {
                });
    }

    public UnionDTO getById(UUID id) {
        return client.get("/api/v1/unions/" + id,
                new ParameterizedTypeReference<ApiResponseDTO<UnionDTO>>() {
                });
    }

    public UnionDTO create(CreateUnionRequest request) {
        return client.post("/api/v1/unions/create", request,
                new ParameterizedTypeReference<ApiResponseDTO<UnionDTO>>() {
                });
    }

    public UnionDTO update(UUID id, UpdateUnionRequest request) {
        return client.put("/api/v1/unions/" + id, request,
                new ParameterizedTypeReference<ApiResponseDTO<UnionDTO>>() {
                });
    }

    public void delete(UUID id) {
        client.delete("/api/v1/unions/" + id);
    }
}
