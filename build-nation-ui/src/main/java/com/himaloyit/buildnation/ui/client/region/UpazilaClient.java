package com.himaloyit.buildnation.ui.client.region;

import com.himaloyit.buildnation.ui.client.support.AuthorizedGatewayClient;
import com.himaloyit.buildnation.ui.dto.ApiResponseDTO;
import com.himaloyit.buildnation.ui.dto.PageResponseDTO;
import com.himaloyit.buildnation.ui.dto.region.CreateUpazilaRequest;
import com.himaloyit.buildnation.ui.dto.region.UpazilaDTO;
import com.himaloyit.buildnation.ui.dto.region.UpdateUpazilaRequest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

import java.util.UUID;

/** Talks to constituency-development-management's /api/v1/upazilas endpoints through the Gateway. */
@Component
public class UpazilaClient {

    private final AuthorizedGatewayClient client;

    public UpazilaClient(AuthorizedGatewayClient client) {
        this.client = client;
    }

    public PageResponseDTO<UpazilaDTO> getAll(int page, int size) {
        return client.get("/api/v1/upazilas?page=" + page + "&size=" + size,
                new ParameterizedTypeReference<ApiResponseDTO<PageResponseDTO<UpazilaDTO>>>() {
                });
    }

    public PageResponseDTO<UpazilaDTO> getByDistrict(UUID districtId, int page, int size) {
        return client.get("/api/v1/upazilas/by-district/" + districtId + "?page=" + page + "&size=" + size,
                new ParameterizedTypeReference<ApiResponseDTO<PageResponseDTO<UpazilaDTO>>>() {
                });
    }

    public UpazilaDTO getById(UUID id) {
        return client.get("/api/v1/upazilas/" + id,
                new ParameterizedTypeReference<ApiResponseDTO<UpazilaDTO>>() {
                });
    }

    public UpazilaDTO create(CreateUpazilaRequest request) {
        return client.post("/api/v1/upazilas/create", request,
                new ParameterizedTypeReference<ApiResponseDTO<UpazilaDTO>>() {
                });
    }

    public UpazilaDTO update(UUID id, UpdateUpazilaRequest request) {
        return client.put("/api/v1/upazilas/" + id, request,
                new ParameterizedTypeReference<ApiResponseDTO<UpazilaDTO>>() {
                });
    }

    public void delete(UUID id) {
        client.delete("/api/v1/upazilas/" + id);
    }
}
