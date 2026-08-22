package com.himaloyit.buildnation.ui.client.fund;

import com.himaloyit.buildnation.ui.client.support.AuthorizedGatewayClient;
import com.himaloyit.buildnation.ui.dto.ApiResponseDTO;
import com.himaloyit.buildnation.ui.dto.PageResponseDTO;
import com.himaloyit.buildnation.ui.dto.fund.CreateFundRequest;
import com.himaloyit.buildnation.ui.dto.fund.FundDTO;
import com.himaloyit.buildnation.ui.dto.fund.UpdateFundRequest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

import java.util.UUID;

/** Talks to constituency-development-management's /api/v1/funds endpoints through the Gateway. */
@Component
public class FundClient {

    private final AuthorizedGatewayClient client;

    public FundClient(AuthorizedGatewayClient client) {
        this.client = client;
    }

    public PageResponseDTO<FundDTO> getAll(int page, int size) {
        return client.get("/api/v1/funds?page=" + page + "&size=" + size,
                new ParameterizedTypeReference<ApiResponseDTO<PageResponseDTO<FundDTO>>>() {
                });
    }

    public PageResponseDTO<FundDTO> getByCategory(UUID categoryId, int page, int size) {
        return client.get("/api/v1/funds/by-category/" + categoryId + "?page=" + page + "&size=" + size,
                new ParameterizedTypeReference<ApiResponseDTO<PageResponseDTO<FundDTO>>>() {
                });
    }

    public FundDTO getById(UUID id) {
        return client.get("/api/v1/funds/" + id,
                new ParameterizedTypeReference<ApiResponseDTO<FundDTO>>() {
                });
    }

    public FundDTO create(CreateFundRequest request) {
        return client.post("/api/v1/funds/create", request,
                new ParameterizedTypeReference<ApiResponseDTO<FundDTO>>() {
                });
    }

    public FundDTO update(UUID id, UpdateFundRequest request) {
        return client.put("/api/v1/funds/" + id, request,
                new ParameterizedTypeReference<ApiResponseDTO<FundDTO>>() {
                });
    }

    public void delete(UUID id) {
        client.delete("/api/v1/funds/" + id);
    }
}
