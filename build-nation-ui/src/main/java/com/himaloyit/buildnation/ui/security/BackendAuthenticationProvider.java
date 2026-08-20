package com.himaloyit.buildnation.ui.security;

import com.himaloyit.buildnation.ui.client.AuthClient;
import com.himaloyit.buildnation.ui.dto.AuthResponseDTO;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Delegates authentication to security-access-control's /api/v1/auth/login instead of
 * checking any local credential store — this UI has none. On success the resulting
 * {@link Authentication}'s authorities mirror the backend's own convention
 * (ROLE_{roleCode}, PERMISSION_{permissionCode}) so UI-side hasAuthority() checks agree
 * with the backend, even though the backend remains the sole enforcement point.
 */
@Component
public class BackendAuthenticationProvider implements AuthenticationProvider {

    private final AuthClient authClient;

    public BackendAuthenticationProvider(AuthClient authClient) {
        this.authClient = authClient;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String email = authentication.getName();
        String password = String.valueOf(authentication.getCredentials());

        AuthResponseDTO response = authClient.login(email, password);

        AuthenticatedPrincipal principal = new AuthenticatedPrincipal(
                response.getPrincipal(),
                response.getAccessToken(),
                response.getRefreshToken(),
                response.getRoles(),
                response.getPermissions());

        List<GrantedAuthority> authorities = Stream.concat(
                        nullSafe(response.getRoles()).stream().map(role -> "ROLE_" + role),
                        nullSafe(response.getPermissions()).stream().map(permission -> "PERMISSION_" + permission))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        return new UsernamePasswordAuthenticationToken(principal, null, authorities);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }

    private static List<String> nullSafe(List<String> list) {
        return list == null ? List.of() : list;
    }
}
