package com.himaloyit.buildnation.ui.client.contractor;

import com.himaloyit.buildnation.ui.client.support.AuthorizedGatewayClient;
import com.himaloyit.buildnation.ui.dto.ApiResponseDTO;
import com.himaloyit.buildnation.ui.dto.PageResponseDTO;
import com.himaloyit.buildnation.ui.dto.contractor.ContractorDTO;
import com.himaloyit.buildnation.ui.dto.contractor.ContractorStatus;
import com.himaloyit.buildnation.ui.dto.contractor.ContractorType;
import com.himaloyit.buildnation.ui.dto.contractor.CreateContractorRequest;
import com.himaloyit.buildnation.ui.dto.contractor.UpdateContractorRequest;
import com.himaloyit.buildnation.ui.dto.contractor.UpdateContractorStatusRequest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

import java.util.UUID;

/** Talks to constituency-development-management's /api/v1/contractors endpoints through the Gateway. */
@Component
public class ContractorClient {

    private final AuthorizedGatewayClient client;

    public ContractorClient(AuthorizedGatewayClient client) {
        this.client = client;
    }

    public PageResponseDTO<ContractorDTO> getAll(int page, int size) {
        return client.get("/api/v1/contractors?page=" + page + "&size=" + size,
                new ParameterizedTypeReference<ApiResponseDTO<PageResponseDTO<ContractorDTO>>>() {
                });
    }

    public PageResponseDTO<ContractorDTO> getByType(ContractorType type, int page, int size) {
        return client.get("/api/v1/contractors/by-type/" + type + "?page=" + page + "&size=" + size,
                new ParameterizedTypeReference<ApiResponseDTO<PageResponseDTO<ContractorDTO>>>() {
                });
    }

    public PageResponseDTO<ContractorDTO> getByStatus(ContractorStatus status, int page, int size) {
        return client.get("/api/v1/contractors/by-status/" + status + "?page=" + page + "&size=" + size,
                new ParameterizedTypeReference<ApiResponseDTO<PageResponseDTO<ContractorDTO>>>() {
                });
    }

    public ContractorDTO getById(UUID id) {
        return client.get("/api/v1/contractors/" + id,
                new ParameterizedTypeReference<ApiResponseDTO<ContractorDTO>>() {
                });
    }

    public ContractorDTO create(CreateContractorRequest request) {
        return client.post("/api/v1/contractors/create", request,
                new ParameterizedTypeReference<ApiResponseDTO<ContractorDTO>>() {
                });
    }

    public ContractorDTO update(UUID id, UpdateContractorRequest request) {
        return client.put("/api/v1/contractors/" + id, request,
                new ParameterizedTypeReference<ApiResponseDTO<ContractorDTO>>() {
                });
    }

    public ContractorDTO updateStatus(UUID id, ContractorStatus status) {
        return client.patch("/api/v1/contractors/" + id + "/status", new UpdateContractorStatusRequest(status),
                new ParameterizedTypeReference<ApiResponseDTO<ContractorDTO>>() {
                });
    }

    public void delete(UUID id) {
        client.delete("/api/v1/contractors/" + id);
    }
}
