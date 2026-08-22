package com.himaloyit.buildnation.ui.client.region;

import com.himaloyit.buildnation.ui.client.support.AuthorizedGatewayClient;
import com.himaloyit.buildnation.ui.dto.ApiResponseDTO;
import com.himaloyit.buildnation.ui.dto.PageResponseDTO;
import com.himaloyit.buildnation.ui.dto.region.CreateWardRequest;
import com.himaloyit.buildnation.ui.dto.region.UpdateWardRequest;
import com.himaloyit.buildnation.ui.dto.region.WardDTO;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

import java.util.UUID;

/** Talks to constituency-development-management's /api/v1/wards endpoints through the Gateway. */
@Component
public class WardClient {

    private final AuthorizedGatewayClient client;

    public WardClient(AuthorizedGatewayClient client) {
        this.client = client;
    }

    public PageResponseDTO<WardDTO> getAll(int page, int size) {
        return client.get("/api/v1/wards?page=" + page + "&size=" + size,
                new ParameterizedTypeReference<ApiResponseDTO<PageResponseDTO<WardDTO>>>() {
                });
    }

    public PageResponseDTO<WardDTO> getByUnion(UUID unionId, int page, int size) {
        return client.get("/api/v1/wards/by-union/" + unionId + "?page=" + page + "&size=" + size,
                new ParameterizedTypeReference<ApiResponseDTO<PageResponseDTO<WardDTO>>>() {
                });
    }

    public WardDTO getById(UUID id) {
        return client.get("/api/v1/wards/" + id,
                new ParameterizedTypeReference<ApiResponseDTO<WardDTO>>() {
                });
    }

    public WardDTO create(CreateWardRequest request) {
        return client.post("/api/v1/wards/create", request,
                new ParameterizedTypeReference<ApiResponseDTO<WardDTO>>() {
                });
    }

    public WardDTO update(UUID id, UpdateWardRequest request) {
        return client.put("/api/v1/wards/" + id, request,
                new ParameterizedTypeReference<ApiResponseDTO<WardDTO>>() {
                });
    }

    public void delete(UUID id) {
        client.delete("/api/v1/wards/" + id);
    }
}
