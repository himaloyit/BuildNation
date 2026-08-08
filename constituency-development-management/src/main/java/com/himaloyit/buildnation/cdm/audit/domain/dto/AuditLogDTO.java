package com.himaloyit.buildnation.cdm.audit.domain.dto;

import com.himaloyit.buildnation.cdm.audit.domain.enums.AuditAction;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
