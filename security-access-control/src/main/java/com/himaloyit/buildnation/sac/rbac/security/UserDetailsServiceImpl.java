package com.himaloyit.buildnation.sac.rbac.security;

import com.himaloyit.buildnation.sac.rbac.domain.entities.Principal;
import com.himaloyit.buildnation.sac.rbac.domain.repositories.iRepositories.IPrincipalRepository;
import com.himaloyit.buildnation.sac.rbac.services.iServices.IAuthorizationService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/*
 * Author: Rajib Kumer Ghosh
 *
 * Serves two call sites with one dual lookup: (a) DaoAuthenticationProvider during
 * login, invoked with the email the user typed, and (b) JwtAuthenticationFilter on
 * every subsequent request, invoked with the principalCode pulled from the JWT
 * subject. Tries principalCode first (the universal identifier for every
 * PrincipalType) then falls back to email (only present for principalType=USER).
 *
 * This also acts as the spec's "JwtAuthenticationConverter" (Prompt-1 §5/§6): builds
 * GrantedAuthority from both roles (ROLE_xxx) and permissions (PERMISSION_xxx),
 * resolved fresh from the DB via IAuthorizationService on every call.
 */

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final IPrincipalRepository principalRepository;
    private final IAuthorizationService authorizationService;

    public UserDetailsServiceImpl(IPrincipalRepository principalRepository, IAuthorizationService authorizationService) {
        this.principalRepository = principalRepository;
        this.authorizationService = authorizationService;
    }

    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        Principal principal = principalRepository.findByPrincipalCode(identifier)
                .or(() -> principalRepository.findByEmail(identifier))
                .orElseThrow(() -> new UsernameNotFoundException("Principal not found: " + identifier));

        List<GrantedAuthority> authorities = new ArrayList<>();
        authorizationService.getRoleCodes(principal.getPrincipalCode())
                .forEach(roleCode -> authorities.add(new SimpleGrantedAuthority("ROLE_" + roleCode)));
        authorizationService.getPermissionCodes(principal.getPrincipalCode())
                .forEach(permissionCode -> authorities.add(new SimpleGrantedAuthority("PERMISSION_" + permissionCode)));

        return org.springframework.security.core.userdetails.User.builder()
                .username(principal.getPrincipalCode())
                .password(principal.getPassword() != null ? principal.getPassword() : "")
                .authorities(authorities)
                .accountExpired(false)
                .accountLocked(!principal.isAccountNonLocked())
                .credentialsExpired(false)
                .disabled(!principal.isEnabled())
                .build();
    }
}
