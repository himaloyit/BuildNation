package com.himaloyit.buildnation.sac.rbac.domain.model;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/*
 * Author: Rajib Kumer Ghosh
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePrincipalRequest {

    private String principalName;

    @Email(message = "Email must be valid")
    private String email;

    private Boolean enabled;

    private Boolean accountNonLocked;

    private LocalDateTime expiresAt;

    private String ownerContact;

    private String description;
}
