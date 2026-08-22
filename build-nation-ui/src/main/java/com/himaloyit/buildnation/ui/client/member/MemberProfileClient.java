package com.himaloyit.buildnation.ui.client.member;

import com.himaloyit.buildnation.ui.client.support.AuthorizedGatewayClient;
import com.himaloyit.buildnation.ui.dto.ApiResponseDTO;
import com.himaloyit.buildnation.ui.dto.member.MemberProfileDTO;
import com.himaloyit.buildnation.ui.dto.member.UpdateMemberProfileRequest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Talks to member-management's /api/v1/members/{id}/profile endpoints through the Gateway. Every
 * Member always has a profile (created alongside it server-side), so there is no create method here
 * — only get and update.
 */
@Component
public class MemberProfileClient {

    private final AuthorizedGatewayClient client;

    public MemberProfileClient(AuthorizedGatewayClient client) {
        this.client = client;
    }

    public MemberProfileDTO getByMemberId(UUID memberId) {
        return client.get("/api/v1/members/" + memberId + "/profile",
                new ParameterizedTypeReference<ApiResponseDTO<MemberProfileDTO>>() {
                });
    }

    public MemberProfileDTO update(UUID memberId, UpdateMemberProfileRequest request) {
        return client.put("/api/v1/members/" + memberId + "/profile", request,
                new ParameterizedTypeReference<ApiResponseDTO<MemberProfileDTO>>() {
                });
    }
}
