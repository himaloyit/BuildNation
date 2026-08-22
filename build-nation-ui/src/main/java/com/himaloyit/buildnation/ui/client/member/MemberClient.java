package com.himaloyit.buildnation.ui.client.member;

import com.himaloyit.buildnation.ui.client.support.AuthorizedGatewayClient;
import com.himaloyit.buildnation.ui.dto.ApiResponseDTO;
import com.himaloyit.buildnation.ui.dto.PageResponseDTO;
import com.himaloyit.buildnation.ui.dto.member.CreateMemberRequest;
import com.himaloyit.buildnation.ui.dto.member.MemberDTO;
import com.himaloyit.buildnation.ui.dto.member.MemberRole;
import com.himaloyit.buildnation.ui.dto.member.MemberStatus;
import com.himaloyit.buildnation.ui.dto.member.UpdateMemberRequest;
import com.himaloyit.buildnation.ui.dto.member.UpdateMemberRoleRequest;
import com.himaloyit.buildnation.ui.dto.member.UpdateMemberStatusRequest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

import java.util.UUID;

/** Talks to member-management's /api/v1/members endpoints through the Gateway. */
@Component
public class MemberClient {

    private final AuthorizedGatewayClient client;

    public MemberClient(AuthorizedGatewayClient client) {
        this.client = client;
    }

    public PageResponseDTO<MemberDTO> getAll(int page, int size) {
        return client.get("/api/v1/members?page=" + page + "&size=" + size,
                new ParameterizedTypeReference<ApiResponseDTO<PageResponseDTO<MemberDTO>>>() {
                });
    }

    public PageResponseDTO<MemberDTO> getByRole(MemberRole role, int page, int size) {
        return client.get("/api/v1/members/by-role/" + role + "?page=" + page + "&size=" + size,
                new ParameterizedTypeReference<ApiResponseDTO<PageResponseDTO<MemberDTO>>>() {
                });
    }

    public PageResponseDTO<MemberDTO> getByStatus(MemberStatus status, int page, int size) {
        return client.get("/api/v1/members/by-status/" + status + "?page=" + page + "&size=" + size,
                new ParameterizedTypeReference<ApiResponseDTO<PageResponseDTO<MemberDTO>>>() {
                });
    }

    public MemberDTO getById(UUID id) {
        return client.get("/api/v1/members/" + id,
                new ParameterizedTypeReference<ApiResponseDTO<MemberDTO>>() {
                });
    }

    public MemberDTO create(CreateMemberRequest request) {
        return client.post("/api/v1/members/create", request,
                new ParameterizedTypeReference<ApiResponseDTO<MemberDTO>>() {
                });
    }

    public MemberDTO update(UUID id, UpdateMemberRequest request) {
        return client.put("/api/v1/members/" + id, request,
                new ParameterizedTypeReference<ApiResponseDTO<MemberDTO>>() {
                });
    }

    public MemberDTO updateRole(UUID id, MemberRole role) {
        return client.patch("/api/v1/members/" + id + "/role", new UpdateMemberRoleRequest(role),
                new ParameterizedTypeReference<ApiResponseDTO<MemberDTO>>() {
                });
    }

    public MemberDTO updateStatus(UUID id, MemberStatus status) {
        return client.patch("/api/v1/members/" + id + "/status", new UpdateMemberStatusRequest(status),
                new ParameterizedTypeReference<ApiResponseDTO<MemberDTO>>() {
                });
    }

    public void delete(UUID id) {
        client.delete("/api/v1/members/" + id);
    }
}
