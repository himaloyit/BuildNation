package com.himaloyit.buildnation.ui.client.region;

import com.himaloyit.buildnation.ui.client.support.AuthorizedGatewayClient;
import com.himaloyit.buildnation.ui.dto.ApiResponseDTO;
import com.himaloyit.buildnation.ui.dto.PageResponseDTO;
import com.himaloyit.buildnation.ui.dto.region.CreateVillageRequest;
import com.himaloyit.buildnation.ui.dto.region.UpdateVillageRequest;
import com.himaloyit.buildnation.ui.dto.region.VillageDTO;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

import java.util.UUID;

/** Talks to constituency-development-management's /api/v1/villages endpoints through the Gateway. */
@Component
public class VillageClient {

    private final AuthorizedGatewayClient client;

    public VillageClient(AuthorizedGatewayClient client) {
        this.client = client;
    }

    public PageResponseDTO<VillageDTO> getAll(int page, int size) {
        return client.get("/api/v1/villages?page=" + page + "&size=" + size,
                new ParameterizedTypeReference<ApiResponseDTO<PageResponseDTO<VillageDTO>>>() {
                });
    }

    public PageResponseDTO<VillageDTO> getByWard(UUID wardId, int page, int size) {
        return client.get("/api/v1/villages/by-ward/" + wardId + "?page=" + page + "&size=" + size,
                new ParameterizedTypeReference<ApiResponseDTO<PageResponseDTO<VillageDTO>>>() {
                });
    }

    public VillageDTO getById(UUID id) {
        return client.get("/api/v1/villages/" + id,
                new ParameterizedTypeReference<ApiResponseDTO<VillageDTO>>() {
                });
    }

    public VillageDTO create(CreateVillageRequest request) {
        return client.post("/api/v1/villages/create", request,
                new ParameterizedTypeReference<ApiResponseDTO<VillageDTO>>() {
                });
    }

    public VillageDTO update(UUID id, UpdateVillageRequest request) {
        return client.put("/api/v1/villages/" + id, request,
                new ParameterizedTypeReference<ApiResponseDTO<VillageDTO>>() {
                });
    }

    public void delete(UUID id) {
        client.delete("/api/v1/villages/" + id);
    }
}
