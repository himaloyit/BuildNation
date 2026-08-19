package com.himaloyit.buildnation.sac.rbac.controllers.rest;

import com.himaloyit.buildnation.sac.rbac.domain.dto.PrincipalDTO;
import com.himaloyit.buildnation.sac.rbac.domain.model.ApiResponse;
import com.himaloyit.buildnation.sac.rbac.domain.model.AuthResponse;
import com.himaloyit.buildnation.sac.rbac.domain.model.LoginRequest;
import com.himaloyit.buildnation.sac.rbac.domain.model.RefreshTokenRequest;
import com.himaloyit.buildnation.sac.rbac.domain.model.RegisterRequest;
import com.himaloyit.buildnation.sac.rbac.services.iServices.IAuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

/*
 * Author: Rajib Kumer Ghosh
 */

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final IAuthService authService;

    public AuthController(IAuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(HttpStatus.CREATED.value(), "Principal registered successfully", response));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Login successful", response));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        AuthResponse response = authService.refresh(request.getRefreshToken());
        return ResponseEntity.ok(ApiResponse.success("Token refreshed successfully", response));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @RequestHeader("Authorization") String authHeader,
            @RequestHeader(value = "X-Refresh-Token", required = false) String refreshToken) {
        String accessToken = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;
        authService.logout(accessToken, refreshToken);
        return ResponseEntity.ok(ApiResponse.success("Logged out successfully", null));
    }

    @GetMapping("/validate")
    public ResponseEntity<ApiResponse<PrincipalDTO>> validate(
            @RequestHeader("Authorization") String authHeader) {
        String accessToken = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;
        PrincipalDTO principalDTO = authService.validateToken(accessToken);
        return ResponseEntity.ok(ApiResponse.success("Token is valid", principalDTO));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<PrincipalDTO>> me(
            @AuthenticationPrincipal UserDetails userDetails) {
        PrincipalDTO principalDTO = authService.getCurrentUser(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Principal retrieved successfully", principalDTO));
    }
}
