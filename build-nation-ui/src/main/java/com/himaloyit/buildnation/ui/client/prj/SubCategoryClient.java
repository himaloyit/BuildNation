package com.himaloyit.buildnation.ui.client.prj;

import com.himaloyit.buildnation.ui.client.support.AuthorizedGatewayClient;
import com.himaloyit.buildnation.ui.dto.ApiResponseDTO;
import com.himaloyit.buildnation.ui.dto.PageResponseDTO;
import com.himaloyit.buildnation.ui.dto.prj.CreateSubCategoryRequest;
import com.himaloyit.buildnation.ui.dto.prj.SubCategoryDTO;
import com.himaloyit.buildnation.ui.dto.prj.UpdateSubCategoryRequest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

import java.util.UUID;

/** Talks to constituency-development-management's /api/v1/subcategories endpoints through the Gateway. */
@Component
public class SubCategoryClient {

    private final AuthorizedGatewayClient client;

    public SubCategoryClient(AuthorizedGatewayClient client) {
        this.client = client;
    }

    public PageResponseDTO<SubCategoryDTO> getAll(int page, int size) {
        return client.get("/api/v1/subcategories?page=" + page + "&size=" + size,
                new ParameterizedTypeReference<ApiResponseDTO<PageResponseDTO<SubCategoryDTO>>>() {
                });
    }

    public PageResponseDTO<SubCategoryDTO> getByCategory(UUID categoryId, int page, int size) {
        return client.get("/api/v1/subcategories/by-category/" + categoryId + "?page=" + page + "&size=" + size,
                new ParameterizedTypeReference<ApiResponseDTO<PageResponseDTO<SubCategoryDTO>>>() {
                });
    }

    public SubCategoryDTO getById(UUID id) {
        return client.get("/api/v1/subcategories/" + id,
                new ParameterizedTypeReference<ApiResponseDTO<SubCategoryDTO>>() {
                });
    }

    public SubCategoryDTO create(CreateSubCategoryRequest request) {
        return client.post("/api/v1/subcategories/create", request,
                new ParameterizedTypeReference<ApiResponseDTO<SubCategoryDTO>>() {
                });
    }

    public SubCategoryDTO update(UUID id, UpdateSubCategoryRequest request) {
        return client.put("/api/v1/subcategories/" + id, request,
                new ParameterizedTypeReference<ApiResponseDTO<SubCategoryDTO>>() {
                });
    }

    public void delete(UUID id) {
        client.delete("/api/v1/subcategories/" + id);
    }
}
