package com.himaloyit.buildnation.ui.client.member;

import com.himaloyit.buildnation.ui.client.support.AuthorizedGatewayClient;
import com.himaloyit.buildnation.ui.dto.ApiResponseDTO;
import com.himaloyit.buildnation.ui.dto.member.CommunicationPreferenceDTO;
import com.himaloyit.buildnation.ui.dto.member.UpdateCommunicationPreferenceRequest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Talks to member-management's /api/v1/members/{id}/communication-preferences endpoints through
 * the Gateway. Every Member always has a preference record (created alongside it server-side), so
 * there is no create method here — only get and update.
 */
@Component
public class CommunicationPreferenceClient {

    private final AuthorizedGatewayClient client;

    public CommunicationPreferenceClient(AuthorizedGatewayClient client) {
        this.client = client;
    }

    public CommunicationPreferenceDTO getByMemberId(UUID memberId) {
        return client.get("/api/v1/members/" + memberId + "/communication-preferences",
                new ParameterizedTypeReference<ApiResponseDTO<CommunicationPreferenceDTO>>() {
                });
    }

    public CommunicationPreferenceDTO update(UUID memberId, UpdateCommunicationPreferenceRequest request) {
        return client.put("/api/v1/members/" + memberId + "/communication-preferences", request,
                new ParameterizedTypeReference<ApiResponseDTO<CommunicationPreferenceDTO>>() {
                });
    }
}
