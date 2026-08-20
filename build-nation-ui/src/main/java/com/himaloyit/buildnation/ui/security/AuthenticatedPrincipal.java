package com.himaloyit.buildnation.ui.security;

import com.himaloyit.buildnation.ui.dto.PrincipalDTO;

import java.io.Serializable;
import java.util.List;

/**
 * Spring Security {@code Authentication} principal for a BuildNation user logged in
 * through this UI. Carries the JWT issued by security-access-control so downstream
 * API clients (Phase 5+) can attach it as a Bearer token — the UI never authenticates
 * requests itself, it only forwards what security-access-control already vouched for.
 */
public class AuthenticatedPrincipal implements Serializable {

    private final PrincipalDTO principal;
    private final String accessToken;
    private final String refreshToken;
    private final List<String> roles;
    private final List<String> permissions;

    public AuthenticatedPrincipal(PrincipalDTO principal, String accessToken, String refreshToken,
                                   List<String> roles, List<String> permissions) {
        this.principal = principal;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.roles = roles;
        this.permissions = permissions;
    }

    public PrincipalDTO getPrincipal() {
        return principal;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public List<String> getRoles() {
        return roles;
    }

    public List<String> getPermissions() {
        return permissions;
    }

    /** Spring Security uses the principal's string form as the authenticated name in some places. */
    @Override
    public String toString() {
        return principal.getPrincipalCode();
    }
}
