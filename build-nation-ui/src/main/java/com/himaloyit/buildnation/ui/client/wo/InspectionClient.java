package com.himaloyit.buildnation.ui.client.wo;

import com.himaloyit.buildnation.ui.client.support.AuthorizedGatewayClient;
import com.himaloyit.buildnation.ui.dto.ApiResponseDTO;
import com.himaloyit.buildnation.ui.dto.PageResponseDTO;
import com.himaloyit.buildnation.ui.dto.wo.CreateInspectionRequest;
import com.himaloyit.buildnation.ui.dto.wo.InspectionDTO;
import com.himaloyit.buildnation.ui.dto.wo.InspectionStatus;
import com.himaloyit.buildnation.ui.dto.wo.UpdateInspectionRequest;
import com.himaloyit.buildnation.ui.dto.wo.UpdateInspectionStatusRequest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

import java.util.UUID;

/** Talks to constituency-development-management's /api/v1/inspections endpoints through the Gateway. */
@Component
public class InspectionClient {

    private final AuthorizedGatewayClient client;

    public InspectionClient(AuthorizedGatewayClient client) {
        this.client = client;
    }

    public PageResponseDTO<InspectionDTO> getAll(int page, int size) {
        return client.get("/api/v1/inspections?page=" + page + "&size=" + size,
                new ParameterizedTypeReference<ApiResponseDTO<PageResponseDTO<InspectionDTO>>>() {
                });
    }

    public PageResponseDTO<InspectionDTO> getByWorkOrder(UUID workOrderId, int page, int size) {
        return client.get("/api/v1/inspections/by-work-order/" + workOrderId + "?page=" + page + "&size=" + size,
                new ParameterizedTypeReference<ApiResponseDTO<PageResponseDTO<InspectionDTO>>>() {
                });
    }

    public InspectionDTO getById(UUID id) {
        return client.get("/api/v1/inspections/" + id,
                new ParameterizedTypeReference<ApiResponseDTO<InspectionDTO>>() {
                });
    }

    public InspectionDTO create(CreateInspectionRequest request) {
        return client.post("/api/v1/inspections/create", request,
                new ParameterizedTypeReference<ApiResponseDTO<InspectionDTO>>() {
                });
    }

    public InspectionDTO update(UUID id, UpdateInspectionRequest request) {
        return client.put("/api/v1/inspections/" + id, request,
                new ParameterizedTypeReference<ApiResponseDTO<InspectionDTO>>() {
                });
    }

    public InspectionDTO updateStatus(UUID id, InspectionStatus status) {
        return client.patch("/api/v1/inspections/" + id + "/status", new UpdateInspectionStatusRequest(status),
                new ParameterizedTypeReference<ApiResponseDTO<InspectionDTO>>() {
                });
    }

    public void delete(UUID id) {
        client.delete("/api/v1/inspections/" + id);
    }
}
