package com.himaloyit.buildnation.cdm.audit.services.iServices;

import com.himaloyit.buildnation.cdm.audit.domain.dto.AuditLogDTO;
import com.himaloyit.buildnation.cdm.audit.domain.enums.AuditAction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface IAuditService {

    AuditLogDTO logAudit(String entityType, UUID entityId, AuditAction action, String oldValue, String newValue, String performedBy);
    AuditLogDTO getAuditLog(UUID id);
    Page<AuditLogDTO> getAllAuditLogs(Pageable pageable);
    Page<AuditLogDTO> getAuditLogsByEntity(String entityType, UUID entityId, Pageable pageable);
    Page<AuditLogDTO> getAuditLogsByEntityType(String entityType, Pageable pageable);
    Page<AuditLogDTO> getAuditLogsByPerformedBy(String performedBy, Pageable pageable);
}
