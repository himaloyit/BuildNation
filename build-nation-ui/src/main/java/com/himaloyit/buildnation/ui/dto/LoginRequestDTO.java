package com.himaloyit.buildnation.ui.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Mirrors security-access-control's LoginRequest
 * (com.himaloyit.buildnation.sac.rbac.domain.model.LoginRequest).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequestDTO {
    private String email;
    private String password;
}
