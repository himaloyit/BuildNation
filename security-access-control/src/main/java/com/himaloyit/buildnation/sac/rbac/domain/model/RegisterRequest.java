package com.himaloyit.buildnation.sac.rbac.domain.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
 * Author: Rajib Kumer Ghosh
 *
 * Public self-registration — always builds a principalType=USER Principal. Other
 * principal types are admin-provisioned via POST /api/v1/principals instead.
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "Full name is required")
    private String fullName;

    @Email(message = "Email must be valid")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    /** Optional seeded Role code (e.g. "ADMIN"). Defaults to "VIEWER" if omitted. */
    private String roleCode;
}
