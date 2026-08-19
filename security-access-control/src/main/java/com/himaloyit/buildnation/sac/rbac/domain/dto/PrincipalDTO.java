package com.himaloyit.buildnation.sac.rbac.domain.dto;

import com.himaloyit.buildnation.sac.rbac.domain.enums.CredentialType;
import com.himaloyit.buildnation.sac.rbac.domain.enums.PrincipalType;
import com.himaloyit.buildnation.sac.rbac.domain.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/*
 * Author: Rajib Kumer Ghosh
 *
 * No password field — never exposed via API.
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PrincipalDTO {
    private UUID principalId;
    private String principalCode;
    private String principalName;
    private PrincipalType principalType;
    private String email;
    private CredentialType credentialType;
    private boolean enabled;
    private boolean accountNonLocked;
    private LocalDateTime expiresAt;
    private LocalDateTime lastLoginAt;
    private String ownerContact;
    private String description;
    private Status status;
    private String createdBy;
    private LocalDateTime createdDate;
    private String updatedBy;
    private LocalDateTime updatedDate;
}
