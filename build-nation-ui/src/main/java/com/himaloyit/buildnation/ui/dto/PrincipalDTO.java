package com.himaloyit.buildnation.ui.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Mirrors security-access-control's PrincipalDTO
 * (com.himaloyit.buildnation.sac.rbac.domain.dto.PrincipalDTO) — no password field.
 */
@Data
@NoArgsConstructor
public class PrincipalDTO implements Serializable {
    private UUID principalId;
    private String principalCode;
    private String principalName;
    private String principalType;
    private String email;
    private String credentialType;
    private boolean enabled;
    private boolean accountNonLocked;
    private LocalDateTime expiresAt;
    private LocalDateTime lastLoginAt;
    private String ownerContact;
    private String description;
    private String status;
    private String createdBy;
    private LocalDateTime createdDate;
    private String updatedBy;
    private LocalDateTime updatedDate;
}
