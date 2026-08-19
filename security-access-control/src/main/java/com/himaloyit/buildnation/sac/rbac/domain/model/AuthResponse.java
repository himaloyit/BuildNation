package com.himaloyit.buildnation.sac.rbac.domain.model;

import com.himaloyit.buildnation.sac.rbac.domain.dto.PrincipalDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/*
 * Author: Rajib Kumer Ghosh
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {

    private String accessToken;
    private String refreshToken;
    @Builder.Default
    private String tokenType = "Bearer";
    private long expiresIn;
    private PrincipalDTO principal;
    private List<String> roles;
    private List<String> permissions;
}
