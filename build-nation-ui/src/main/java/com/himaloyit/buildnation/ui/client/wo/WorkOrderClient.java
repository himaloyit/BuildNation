package com.himaloyit.buildnation.ui.client.wo;

import com.himaloyit.buildnation.ui.client.support.AuthorizedGatewayClient;
import com.himaloyit.buildnation.ui.dto.ApiResponseDTO;
import com.himaloyit.buildnation.ui.dto.PageResponseDTO;
import com.himaloyit.buildnation.ui.dto.wo.CreateWorkOrderRequest;
import com.himaloyit.buildnation.ui.dto.wo.UpdateWorkOrderRequest;
import com.himaloyit.buildnation.ui.dto.wo.UpdateWorkOrderStatusRequest;
import com.himaloyit.buildnation.ui.dto.wo.WorkOrderDTO;
import com.himaloyit.buildnation.ui.dto.wo.WorkOrderStatus;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

import java.util.UUID;

/** Talks to constituency-development-management's /api/v1/work-orders endpoints through the Gateway. */
@Component
public class WorkOrderClient {

    private final AuthorizedGatewayClient client;

    public WorkOrderClient(AuthorizedGatewayClient client) {
        this.client = client;
    }

    public PageResponseDTO<WorkOrderDTO> getAll(int page, int size) {
        return client.get("/api/v1/work-orders?page=" + page + "&size=" + size,
                new ParameterizedTypeReference<ApiResponseDTO<PageResponseDTO<WorkOrderDTO>>>() {
                });
    }

    public PageResponseDTO<WorkOrderDTO> getByProject(UUID projectId, int page, int size) {
        return client.get("/api/v1/work-orders/by-project/" + projectId + "?page=" + page + "&size=" + size,
                new ParameterizedTypeReference<ApiResponseDTO<PageResponseDTO<WorkOrderDTO>>>() {
                });
    }

    public PageResponseDTO<WorkOrderDTO> getByContractor(UUID contractorId, int page, int size) {
        return client.get("/api/v1/work-orders/by-contractor/" + contractorId + "?page=" + page + "&size=" + size,
                new ParameterizedTypeReference<ApiResponseDTO<PageResponseDTO<WorkOrderDTO>>>() {
                });
    }

    public WorkOrderDTO getById(UUID id) {
        return client.get("/api/v1/work-orders/" + id,
                new ParameterizedTypeReference<ApiResponseDTO<WorkOrderDTO>>() {
                });
    }

    public WorkOrderDTO create(CreateWorkOrderRequest request) {
        return client.post("/api/v1/work-orders/create", request,
                new ParameterizedTypeReference<ApiResponseDTO<WorkOrderDTO>>() {
                });
    }

    public WorkOrderDTO update(UUID id, UpdateWorkOrderRequest request) {
        return client.put("/api/v1/work-orders/" + id, request,
                new ParameterizedTypeReference<ApiResponseDTO<WorkOrderDTO>>() {
                });
    }

    public WorkOrderDTO updateStatus(UUID id, WorkOrderStatus status) {
        return client.patch("/api/v1/work-orders/" + id + "/status", new UpdateWorkOrderStatusRequest(status),
                new ParameterizedTypeReference<ApiResponseDTO<WorkOrderDTO>>() {
                });
    }

    public void delete(UUID id) {
        client.delete("/api/v1/work-orders/" + id);
    }
}
