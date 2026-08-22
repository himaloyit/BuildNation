package com.himaloyit.buildnation.ui.client.audit;

import com.himaloyit.buildnation.ui.client.support.AuthorizedGatewayClient;
import com.himaloyit.buildnation.ui.dto.ApiResponseDTO;
import com.himaloyit.buildnation.ui.dto.PageResponseDTO;
import com.himaloyit.buildnation.ui.dto.audit.AuditLogDTO;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Talks to constituency-development-management's /api/v1/audit-logs endpoints through the Gateway.
 * Read-only by design: the backend itself only exposes create/get/list (no PUT/DELETE — audit logs
 * are append-only), and nothing in this UI creates them either since no other backend service calls
 * {@code IAuditService.logAudit(...)} automatically yet (see [[cdm_service]] — that wiring is a
 * separate, larger cross-service task). This client only covers the read endpoints the list view uses.
 */
@Component
public class AuditLogClient {

    private final AuthorizedGatewayClient client;

    public AuditLogClient(AuthorizedGatewayClient client) {
        this.client = client;
    }

    public PageResponseDTO<AuditLogDTO> getAll(int page, int size) {
        return client.get("/api/v1/audit-logs?page=" + page + "&size=" + size,
                new ParameterizedTypeReference<ApiResponseDTO<PageResponseDTO<AuditLogDTO>>>() {
                });
    }

    public PageResponseDTO<AuditLogDTO> getByEntity(String entityType, UUID entityId, int page, int size) {
        return client.get("/api/v1/audit-logs/by-entity/" + entityType + "/" + entityId + "?page=" + page + "&size=" + size,
                new ParameterizedTypeReference<ApiResponseDTO<PageResponseDTO<AuditLogDTO>>>() {
                });
    }

    public PageResponseDTO<AuditLogDTO> getByEntityType(String entityType, int page, int size) {
        return client.get("/api/v1/audit-logs/by-entity-type/" + entityType + "?page=" + page + "&size=" + size,
                new ParameterizedTypeReference<ApiResponseDTO<PageResponseDTO<AuditLogDTO>>>() {
                });
    }

    public PageResponseDTO<AuditLogDTO> getByPerformedBy(String performedBy, int page, int size) {
        return client.get("/api/v1/audit-logs/by-performed-by/" + performedBy + "?page=" + page + "&size=" + size,
                new ParameterizedTypeReference<ApiResponseDTO<PageResponseDTO<AuditLogDTO>>>() {
                });
    }
}
