package com.himaloyit.buildnation.ui.security;

import com.himaloyit.buildnation.ui.client.AuthClient;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

/** Blacklists the access token server-side (security-access-control) on logout. */
@Component
public class BackendLogoutHandler implements LogoutHandler {

    private final AuthClient authClient;

    public BackendLogoutHandler(AuthClient authClient) {
        this.authClient = authClient;
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof AuthenticatedPrincipal principal) {
            authClient.logout(principal.getAccessToken(), principal.getRefreshToken());
        }
    }
}
