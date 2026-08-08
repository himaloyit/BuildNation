package com.himaloyit.buildnation.cdm.audit.domain.model;

import com.himaloyit.buildnation.cdm.audit.domain.enums.AuditAction;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateAuditLogRequest {

    @NotBlank(message = "Entity type is mandatory")
    private String entityType;

    @NotNull(message = "Entity id is mandatory")
    private UUID entityId;

    @NotNull(message = "Action is mandatory")
    private AuditAction action;

    private String oldValue;

    private String newValue;

    private String performedBy;
}
