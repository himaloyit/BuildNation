package com.himaloyit.buildnation.cdm.audit.controllers.rest;

import com.himaloyit.buildnation.cdm.audit.domain.dto.AuditLogDTO;
import com.himaloyit.buildnation.cdm.audit.domain.model.CreateAuditLogRequest;
import com.himaloyit.buildnation.cdm.audit.services.iServices.IAuditService;
import com.himaloyit.buildnation.cdm.common.model.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/audit-logs")
public class AuditLogController {

    private final IAuditService iAuditService;

    public AuditLogController(IAuditService iAuditService) {
        this.iAuditService = iAuditService;
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<AuditLogDTO>> createAuditLog(@Valid @RequestBody CreateAuditLogRequest request) {
        AuditLogDTO saved = iAuditService.logAudit(request.getEntityType(), request.getEntityId(), request.getAction(),
                request.getOldValue(), request.getNewValue(), request.getPerformedBy());
        return ResponseEntity.ok(ApiResponse.success("Audit log created successfully", saved));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AuditLogDTO>> getAuditLogById(@PathVariable UUID id) {
        AuditLogDTO auditLog = iAuditService.getAuditLog(id);
        return ResponseEntity.ok(ApiResponse.success("Audit log found", auditLog));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<AuditLogDTO>>> getAllAuditLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<AuditLogDTO> pagedAuditLogs = iAuditService.getAllAuditLogs(PageRequest.of(page, size));
        return ResponseEntity.ok(ApiResponse.success("Paged audit logs retrieved", pagedAuditLogs));
    }

    @GetMapping("/by-entity/{entityType}/{entityId}")
    public ResponseEntity<ApiResponse<Page<AuditLogDTO>>> getAuditLogsByEntity(
            @PathVariable String entityType,
            @PathVariable UUID entityId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<AuditLogDTO> auditLogs = iAuditService.getAuditLogsByEntity(entityType, entityId, PageRequest.of(page, size));
        return ResponseEntity.ok(ApiResponse.success("Audit logs by entity retrieved", auditLogs));
    }

    @GetMapping("/by-entity-type/{entityType}")
    public ResponseEntity<ApiResponse<Page<AuditLogDTO>>> getAuditLogsByEntityType(
            @PathVariable String entityType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<AuditLogDTO> auditLogs = iAuditService.getAuditLogsByEntityType(entityType, PageRequest.of(page, size));
        return ResponseEntity.ok(ApiResponse.success("Audit logs by entity type retrieved", auditLogs));
    }

    @GetMapping("/by-performed-by/{performedBy}")
    public ResponseEntity<ApiResponse<Page<AuditLogDTO>>> getAuditLogsByPerformedBy(
            @PathVariable String performedBy,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<AuditLogDTO> auditLogs = iAuditService.getAuditLogsByPerformedBy(performedBy, PageRequest.of(page, size));
        return ResponseEntity.ok(ApiResponse.success("Audit logs by performer retrieved", auditLogs));
    }
}
