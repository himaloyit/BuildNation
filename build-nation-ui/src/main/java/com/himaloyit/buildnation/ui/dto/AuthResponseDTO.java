package com.himaloyit.buildnation.ui.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Mirrors security-access-control's AuthResponse
 * (com.himaloyit.buildnation.sac.rbac.domain.model.AuthResponse).
 */
@Data
@NoArgsConstructor
public class AuthResponseDTO {
    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private long expiresIn;
    private PrincipalDTO principal;
    private List<String> roles;
    private List<String> permissions;
}
