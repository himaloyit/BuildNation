package com.himaloyit.buildnation.ui.client;

import com.himaloyit.buildnation.ui.dto.ApiResponseDTO;
import com.himaloyit.buildnation.ui.dto.AuthResponseDTO;
import com.himaloyit.buildnation.ui.dto.LoginRequestDTO;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

/**
 * Talks to security-access-control's /api/v1/auth/** endpoints through the API Gateway.
 * No auth logic lives here beyond the HTTP call — Spring Security's
 * {@code BackendAuthenticationProvider} decides what a failure means.
 */
@Component
public class AuthClient {

    private final RestClient restClient;

    public AuthClient(RestClient gatewayRestClient) {
        this.restClient = gatewayRestClient;
    }

    public AuthResponseDTO login(String email, String password) {
        try {
            ApiResponseDTO<AuthResponseDTO> response = restClient.post()
                    .uri("/api/v1/auth/login")
                    .body(new LoginRequestDTO(email, password))
                    .retrieve()
                    .body(new ParameterizedTypeReference<ApiResponseDTO<AuthResponseDTO>>() {
                    });
            if (response == null || response.getData() == null) {
                throw new AuthenticationServiceException("Empty response from authentication service");
            }
            return response.getData();
        } catch (HttpClientErrorException.Unauthorized | HttpClientErrorException.Forbidden e) {
            throw new BadCredentialsException("Invalid email or password", e);
        } catch (HttpClientErrorException.NotFound e) {
            throw new BadCredentialsException("Invalid email or password", e);
        } catch (HttpClientErrorException e) {
            throw new AuthenticationServiceException("Authentication service rejected the request: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new AuthenticationServiceException("Authentication service is unavailable", e);
        }
    }

    public void logout(String accessToken, String refreshToken) {
        try {
            RestClient.RequestHeadersSpec<?> request = restClient.post()
                    .uri("/api/v1/auth/logout")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
            if (refreshToken != null) {
                request = request.header("X-Refresh-Token", refreshToken);
            }
            request.retrieve().toBodilessEntity();
        } catch (Exception e) {
            // Best-effort: the local Spring Security session is cleared regardless of
            // whether the backend token blacklist call succeeds.
        }
    }
}
