package com.himaloyit.buildnation.ui.dto.audit;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/** Mirrors com.himaloyit.buildnation.cdm.audit.domain.dto.AuditLogDTO. */
@Data
@NoArgsConstructor
public class AuditLogDTO {
    private UUID id;
    private String entityType;
    private UUID entityId;
    private AuditAction action;
    private String oldValue;
    private String newValue;
    private String performedBy;
    private LocalDateTime performedAt;
}
