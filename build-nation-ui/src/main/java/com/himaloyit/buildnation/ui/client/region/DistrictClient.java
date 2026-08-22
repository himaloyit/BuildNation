package com.himaloyit.buildnation.ui.client.region;

import com.himaloyit.buildnation.ui.client.support.AuthorizedGatewayClient;
import com.himaloyit.buildnation.ui.dto.ApiResponseDTO;
import com.himaloyit.buildnation.ui.dto.PageResponseDTO;
import com.himaloyit.buildnation.ui.dto.region.CreateDistrictRequest;
import com.himaloyit.buildnation.ui.dto.region.DistrictDTO;
import com.himaloyit.buildnation.ui.dto.region.UpdateDistrictRequest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

import java.util.UUID;

/** Talks to constituency-development-management's /api/v1/districts endpoints through the Gateway. */
@Component
public class DistrictClient {

    private final AuthorizedGatewayClient client;

    public DistrictClient(AuthorizedGatewayClient client) {
        this.client = client;
    }

    public PageResponseDTO<DistrictDTO> getAll(int page, int size) {
        return client.get("/api/v1/districts?page=" + page + "&size=" + size,
                new ParameterizedTypeReference<ApiResponseDTO<PageResponseDTO<DistrictDTO>>>() {
                });
    }

    public DistrictDTO getById(UUID id) {
        return client.get("/api/v1/districts/" + id,
                new ParameterizedTypeReference<ApiResponseDTO<DistrictDTO>>() {
                });
    }

    public DistrictDTO create(CreateDistrictRequest request) {
        return client.post("/api/v1/districts/create", request,
                new ParameterizedTypeReference<ApiResponseDTO<DistrictDTO>>() {
                });
    }

    public DistrictDTO update(UUID id, UpdateDistrictRequest request) {
        return client.put("/api/v1/districts/" + id, request,
                new ParameterizedTypeReference<ApiResponseDTO<DistrictDTO>>() {
                });
    }

    public void delete(UUID id) {
        client.delete("/api/v1/districts/" + id);
    }
}
