package com.himaloyit.buildnation.sac.rbac.domain.model;

import com.himaloyit.buildnation.sac.rbac.domain.enums.CredentialType;
import com.himaloyit.buildnation.sac.rbac.domain.enums.PrincipalType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/*
 * Author: Rajib Kumer Ghosh
 *
 * Admin provisioning entry point for every principal type — including USER (an admin
 * can create a human principal directly here too). Public self-registration for
 * humans goes through /api/v1/auth/register instead, which internally builds a
 * Principal the same way.
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatePrincipalRequest {

    @NotBlank(message = "Principal code is required")
    private String principalCode;

    @NotBlank(message = "Principal name is required")
    private String principalName;

    @NotNull(message = "Principal type is required")
    private PrincipalType principalType;

    @Email(message = "Email must be valid")
    private String email;

    private String password;

    @NotNull(message = "Credential type is required")
    private CredentialType credentialType;

    private LocalDateTime expiresAt;

    private String ownerContact;

    private String description;
}
