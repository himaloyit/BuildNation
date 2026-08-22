package com.himaloyit.buildnation.ui.client.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.himaloyit.buildnation.ui.dto.ApiResponseDTO;
import com.himaloyit.buildnation.ui.security.AuthenticatedPrincipal;
import com.vaadin.flow.spring.security.AuthenticationContext;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.io.IOException;

/**
 * Shared plumbing for every backend API client called from the UI: attaches the
 * signed-in user's access token as a Bearer header and translates a non-2xx
 * {@code ApiResponse} body into a {@link GatewayApiException} carrying the
 * backend's own error message, so views can show it directly rather than a raw
 * HTTP status.
 */
@Component
public class AuthorizedGatewayClient {

    private final RestClient restClient;
    private final AuthenticationContext authenticationContext;
    private final ObjectMapper objectMapper;

    public AuthorizedGatewayClient(RestClient gatewayRestClient, AuthenticationContext authenticationContext,
                                    ObjectMapper objectMapper) {
        this.restClient = gatewayRestClient;
        this.authenticationContext = authenticationContext;
        this.objectMapper = objectMapper;
    }

    public <T> T get(String uri, ParameterizedTypeReference<ApiResponseDTO<T>> typeRef) {
        return unwrap(restClient.get().uri(uri).header(HttpHeaders.AUTHORIZATION, bearer()), typeRef);
    }

    public <T> T post(String uri, Object body, ParameterizedTypeReference<ApiResponseDTO<T>> typeRef) {
        return unwrap(restClient.post().uri(uri).header(HttpHeaders.AUTHORIZATION, bearer()).body(body), typeRef);
    }

    public <T> T put(String uri, Object body, ParameterizedTypeReference<ApiResponseDTO<T>> typeRef) {
        return unwrap(restClient.put().uri(uri).header(HttpHeaders.AUTHORIZATION, bearer()).body(body), typeRef);
    }

    public <T> T patch(String uri, Object body, ParameterizedTypeReference<ApiResponseDTO<T>> typeRef) {
        return unwrap(restClient.patch().uri(uri).header(HttpHeaders.AUTHORIZATION, bearer()).body(body), typeRef);
    }

    public void delete(String uri) {
        unwrap(restClient.delete().uri(uri).header(HttpHeaders.AUTHORIZATION, bearer()),
                new ParameterizedTypeReference<ApiResponseDTO<Void>>() {
                });
    }

    private String bearer() {
        String token = authenticationContext.getAuthenticatedUser(AuthenticatedPrincipal.class)
                .map(AuthenticatedPrincipal::getAccessToken)
                .orElseThrow(() -> new IllegalStateException("No authenticated user in session"));
        return "Bearer " + token;
    }

    private <T> T unwrap(RestClient.RequestHeadersSpec<?> request, ParameterizedTypeReference<ApiResponseDTO<T>> typeRef) {
        ApiResponseDTO<T> body = request.retrieve()
                .onStatus(HttpStatusCode::isError, (req, res) -> {
                    throw new GatewayApiException(res.getStatusCode().value(), extractMessage(res));
                })
                .body(typeRef);
        return body == null ? null : body.getData();
    }

    private String extractMessage(ClientHttpResponse response) {
        try (var body = response.getBody()) {
            ApiResponseDTO<?> error = objectMapper.readValue(body, ApiResponseDTO.class);
            if (error.getMessage() != null && !error.getMessage().isBlank()) {
                return error.getMessage();
            }
        } catch (IOException | RuntimeException ignored) {
            // fall through to the generic message below
        }
        try {
            return "Request failed with status " + response.getStatusCode();
        } catch (IOException e) {
            return "Request failed";
        }
    }
}
