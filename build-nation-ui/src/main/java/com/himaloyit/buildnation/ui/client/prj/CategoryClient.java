package com.himaloyit.buildnation.ui.client.prj;

import com.himaloyit.buildnation.ui.client.support.AuthorizedGatewayClient;
import com.himaloyit.buildnation.ui.dto.ApiResponseDTO;
import com.himaloyit.buildnation.ui.dto.PageResponseDTO;
import com.himaloyit.buildnation.ui.dto.prj.CategoryDTO;
import com.himaloyit.buildnation.ui.dto.prj.CreateCategoryRequest;
import com.himaloyit.buildnation.ui.dto.prj.UpdateCategoryRequest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

import java.util.UUID;

/** Talks to constituency-development-management's /api/v1/categories endpoints through the Gateway. */
@Component
public class CategoryClient {

    private final AuthorizedGatewayClient client;

    public CategoryClient(AuthorizedGatewayClient client) {
        this.client = client;
    }

    public PageResponseDTO<CategoryDTO> getAll(int page, int size) {
        return client.get("/api/v1/categories?page=" + page + "&size=" + size,
                new ParameterizedTypeReference<ApiResponseDTO<PageResponseDTO<CategoryDTO>>>() {
                });
    }

    public CategoryDTO getById(UUID id) {
        return client.get("/api/v1/categories/" + id,
                new ParameterizedTypeReference<ApiResponseDTO<CategoryDTO>>() {
                });
    }

    public CategoryDTO create(CreateCategoryRequest request) {
        return client.post("/api/v1/categories/create", request,
                new ParameterizedTypeReference<ApiResponseDTO<CategoryDTO>>() {
                });
    }

    public CategoryDTO update(UUID id, UpdateCategoryRequest request) {
        return client.put("/api/v1/categories/" + id, request,
                new ParameterizedTypeReference<ApiResponseDTO<CategoryDTO>>() {
                });
    }

    public void delete(UUID id) {
        client.delete("/api/v1/categories/" + id);
    }
}
