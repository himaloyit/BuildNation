package com.himaloyit.buildnation.ui.client.wo;

import com.himaloyit.buildnation.ui.client.support.AuthorizedGatewayClient;
import com.himaloyit.buildnation.ui.dto.ApiResponseDTO;
import com.himaloyit.buildnation.ui.dto.PageResponseDTO;
import com.himaloyit.buildnation.ui.dto.wo.CreatePaymentRequest;
import com.himaloyit.buildnation.ui.dto.wo.PaymentDTO;
import com.himaloyit.buildnation.ui.dto.wo.PaymentStatus;
import com.himaloyit.buildnation.ui.dto.wo.UpdatePaymentStatusRequest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Talks to constituency-development-management's /api/v1/payments endpoints through the Gateway.
 * The backend has no PUT for Payment — only create, PATCH /{id}/status, and delete — so this
 * client deliberately has no update method.
 */
@Component
public class PaymentClient {

    private final AuthorizedGatewayClient client;

    public PaymentClient(AuthorizedGatewayClient client) {
        this.client = client;
    }

    public PageResponseDTO<PaymentDTO> getAll(int page, int size) {
        return client.get("/api/v1/payments?page=" + page + "&size=" + size,
                new ParameterizedTypeReference<ApiResponseDTO<PageResponseDTO<PaymentDTO>>>() {
                });
    }

    public PageResponseDTO<PaymentDTO> getByWorkOrder(UUID workOrderId, int page, int size) {
        return client.get("/api/v1/payments/by-work-order/" + workOrderId + "?page=" + page + "&size=" + size,
                new ParameterizedTypeReference<ApiResponseDTO<PageResponseDTO<PaymentDTO>>>() {
                });
    }

    public PaymentDTO getById(UUID id) {
        return client.get("/api/v1/payments/" + id,
                new ParameterizedTypeReference<ApiResponseDTO<PaymentDTO>>() {
                });
    }

    public PaymentDTO create(CreatePaymentRequest request) {
        return client.post("/api/v1/payments/create", request,
                new ParameterizedTypeReference<ApiResponseDTO<PaymentDTO>>() {
                });
    }

    public PaymentDTO updateStatus(UUID id, PaymentStatus status) {
        return client.patch("/api/v1/payments/" + id + "/status", new UpdatePaymentStatusRequest(status),
                new ParameterizedTypeReference<ApiResponseDTO<PaymentDTO>>() {
                });
    }

    public void delete(UUID id) {
        client.delete("/api/v1/payments/" + id);
    }
}
