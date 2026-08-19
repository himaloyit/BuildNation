package com.himaloyit.buildnation.sac.rbac.services.impl;

import com.himaloyit.buildnation.sac.rbac.domain.dto.PrincipalDTO;
import com.himaloyit.buildnation.sac.rbac.domain.entities.Principal;
import com.himaloyit.buildnation.sac.rbac.domain.entities.Role;
import com.himaloyit.buildnation.sac.rbac.domain.enums.CredentialType;
import com.himaloyit.buildnation.sac.rbac.domain.enums.PrincipalType;
import com.himaloyit.buildnation.sac.rbac.domain.enums.Status;
import com.himaloyit.buildnation.sac.rbac.domain.mapper.IPrincipalMapper;
import com.himaloyit.buildnation.sac.rbac.domain.model.AuthResponse;
import com.himaloyit.buildnation.sac.rbac.domain.model.LoginRequest;
import com.himaloyit.buildnation.sac.rbac.domain.model.RegisterRequest;
import com.himaloyit.buildnation.sac.rbac.domain.repositories.iRepositories.IPrincipalRepository;
import com.himaloyit.buildnation.sac.rbac.domain.repositories.iRepositories.IRoleRepository;
import com.himaloyit.buildnation.sac.rbac.security.JwtTokenProvider;
import com.himaloyit.buildnation.sac.rbac.services.iServices.IAuthService;
import com.himaloyit.buildnation.sac.rbac.services.iServices.IAuthorizationService;
import com.himaloyit.buildnation.sac.rbac.services.iServices.IPrincipalRoleService;
import com.himaloyit.buildnation.sac.rbac.util.exceptions.DuplicateCodeException;
import com.himaloyit.buildnation.sac.rbac.util.exceptions.EntityNotFoundException;
import com.himaloyit.buildnation.sac.rbac.util.exceptions.InvalidTokenException;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Set;

/*
 * Author: Rajib Kumer Ghosh
 */

@Service
public class AuthServiceImpl implements IAuthService {

    private static final String DEFAULT_ROLE_CODE = "VIEWER";

    private final IPrincipalRepository principalRepository;
    private final IRoleRepository roleRepository;
    private final IPrincipalMapper principalMapper;
    private final IPrincipalRoleService principalRoleService;
    private final IAuthorizationService authorizationService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    public AuthServiceImpl(IPrincipalRepository principalRepository,
                            IRoleRepository roleRepository,
                            IPrincipalMapper principalMapper,
                            IPrincipalRoleService principalRoleService,
                            IAuthorizationService authorizationService,
                            PasswordEncoder passwordEncoder,
                            JwtTokenProvider jwtTokenProvider,
                            AuthenticationManager authenticationManager) {
        this.principalRepository = principalRepository;
        this.roleRepository = roleRepository;
        this.principalMapper = principalMapper;
        this.principalRoleService = principalRoleService;
        this.authorizationService = authorizationService;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public AuthResponse register(RegisterRequest request) {
        if (principalRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateCodeException("Principal already exists with email: " + request.getEmail());
        }

        LocalDateTime now = LocalDateTime.now();
        Principal principal = Principal.builder()
                .principalCode(request.getEmail())
                .principalName(request.getFullName())
                .principalType(PrincipalType.USER)
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .credentialType(CredentialType.PASSWORD)
                .enabled(true)
                .accountNonLocked(true)
                .status(Status.ACTIVE)
                .createdBy("system")
                .createdDate(now)
                .updatedBy("system")
                .updatedDate(now)
                .build();

        Principal saved = principalRepository.save(principal);

        String roleCode = request.getRoleCode() != null ? request.getRoleCode() : DEFAULT_ROLE_CODE;
        Role role = roleRepository.findByRoleCode(roleCode)
                .orElseThrow(() -> new EntityNotFoundException("Role not found with code: " + roleCode));
        principalRoleService.assignRole(saved.getPrincipalId(), role.getRoleId());

        return buildAuthResponse(saved);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Invalid email or password");
        }

        Principal principal = principalRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new EntityNotFoundException("Principal not found with email: " + request.getEmail()));

        principal.setLastLoginAt(LocalDateTime.now());
        principalRepository.save(principal);

        return buildAuthResponse(principal);
    }

    @Override
    public AuthResponse refresh(String refreshToken) {
        String principalCode = jwtTokenProvider.getPrincipalCodeFromRefreshToken(refreshToken);
        if (principalCode == null) {
            throw new InvalidTokenException("Invalid or expired refresh token");
        }

        Principal principal = principalRepository.findByPrincipalCode(principalCode)
                .orElseThrow(() -> new EntityNotFoundException("Principal not found with code: " + principalCode));

        // Rotate: delete old, issue new
        jwtTokenProvider.deleteRefreshToken(refreshToken);

        return buildAuthResponse(principal);
    }

    @Override
    public void logout(String accessToken, String refreshToken) {
        jwtTokenProvider.blacklistAccessToken(accessToken);
        if (refreshToken != null && !refreshToken.isBlank()) {
            jwtTokenProvider.deleteRefreshToken(refreshToken);
        }
    }

    @Override
    public PrincipalDTO validateToken(String accessToken) {
        if (!jwtTokenProvider.isAccessTokenValid(accessToken)) {
            throw new InvalidTokenException("Token is invalid or has been revoked");
        }
        String principalCode = jwtTokenProvider.getPrincipalCodeFromToken(accessToken);
        Principal principal = principalRepository.findByPrincipalCode(principalCode)
                .orElseThrow(() -> new EntityNotFoundException("Principal not found with code: " + principalCode));
        return principalMapper.toDto(principal);
    }

    @Override
    @Cacheable(value = "principals-by-code", key = "#principalCode")
    public PrincipalDTO getCurrentUser(String principalCode) {
        Principal principal = principalRepository.findByPrincipalCode(principalCode)
                .orElseThrow(() -> new EntityNotFoundException("Principal not found with code: " + principalCode));
        return principalMapper.toDto(principal);
    }

    private AuthResponse buildAuthResponse(Principal principal) {
        PrincipalDTO principalDTO = principalMapper.toDto(principal);
        String accessToken = jwtTokenProvider.generateAccessToken(principal.getPrincipalCode());
        String refreshToken = jwtTokenProvider.generateRefreshToken(principal.getPrincipalCode());
        Set<String> roleCodes = authorizationService.getRoleCodes(principal.getPrincipalCode());
        Set<String> permissionCodes = authorizationService.getPermissionCodes(principal.getPrincipalCode());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(3600L)
                .principal(principalDTO)
                .roles(new ArrayList<>(roleCodes))
                .permissions(new ArrayList<>(permissionCodes))
                .build();
    }
}
