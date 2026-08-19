package com.himaloyit.buildnation.sac.rbac.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/*
 * Author: Rajib Kumer Ghosh
 *
 * Resolves the calling principal's code for stamping createdBy/updatedBy on RBAC
 * entities. The JWT filter already resolves every authenticated request's identity
 * into SecurityContextHolder keyed by principalCode (see UserDetailsServiceImpl), so
 * this is real caller identity, not a placeholder — falls back to "system" only when
 * there is no authenticated caller (e.g. the startup seeder).
 */
public final class CurrentPrincipalContext {

    private static final String SYSTEM = "system";

    private CurrentPrincipalContext() {
    }

    public static String currentPrincipalCode() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            return SYSTEM;
        }
        return authentication.getName();
    }
}
