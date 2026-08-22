package com.himaloyit.buildnation.ui.client.fund;

import com.himaloyit.buildnation.ui.client.support.AuthorizedGatewayClient;
import com.himaloyit.buildnation.ui.dto.ApiResponseDTO;
import com.himaloyit.buildnation.ui.dto.PageResponseDTO;
import com.himaloyit.buildnation.ui.dto.fund.CreateFundAllocationRequest;
import com.himaloyit.buildnation.ui.dto.fund.FundAllocationDTO;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Talks to constituency-development-management's /api/v1/fund-allocations endpoints through the
 * Gateway. A FundAllocation is immutable on the backend (no PUT — delete and recreate to change an
 * amount), so this client deliberately has no update method.
 */
@Component
public class FundAllocationClient {

    private final AuthorizedGatewayClient client;

    public FundAllocationClient(AuthorizedGatewayClient client) {
        this.client = client;
    }

    public PageResponseDTO<FundAllocationDTO> getAll(int page, int size) {
        return client.get("/api/v1/fund-allocations?page=" + page + "&size=" + size,
                new ParameterizedTypeReference<ApiResponseDTO<PageResponseDTO<FundAllocationDTO>>>() {
                });
    }

    public PageResponseDTO<FundAllocationDTO> getByFund(UUID fundId, int page, int size) {
        return client.get("/api/v1/fund-allocations/by-fund/" + fundId + "?page=" + page + "&size=" + size,
                new ParameterizedTypeReference<ApiResponseDTO<PageResponseDTO<FundAllocationDTO>>>() {
                });
    }

    public PageResponseDTO<FundAllocationDTO> getByProject(UUID projectId, int page, int size) {
        return client.get("/api/v1/fund-allocations/by-project/" + projectId + "?page=" + page + "&size=" + size,
                new ParameterizedTypeReference<ApiResponseDTO<PageResponseDTO<FundAllocationDTO>>>() {
                });
    }

    public FundAllocationDTO getById(UUID id) {
        return client.get("/api/v1/fund-allocations/" + id,
                new ParameterizedTypeReference<ApiResponseDTO<FundAllocationDTO>>() {
                });
    }

    public FundAllocationDTO create(CreateFundAllocationRequest request) {
        return client.post("/api/v1/fund-allocations/create", request,
                new ParameterizedTypeReference<ApiResponseDTO<FundAllocationDTO>>() {
                });
    }

    public void delete(UUID id) {
        client.delete("/api/v1/fund-allocations/" + id);
    }
}
