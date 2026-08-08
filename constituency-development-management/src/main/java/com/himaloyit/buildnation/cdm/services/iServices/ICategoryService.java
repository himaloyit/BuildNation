package com.himaloyit.buildnation.cdm.services.iServices;

import com.himaloyit.buildnation.cdm.domain.dto.CategoryDTO;
import com.himaloyit.buildnation.cdm.domain.model.CreateCategoryRequest;
import com.himaloyit.buildnation.cdm.domain.model.UpdateCategoryRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ICategoryService {

    CategoryDTO createCategory(CreateCategoryRequest request);
    CategoryDTO getCategory(UUID id);
    Page<CategoryDTO> getAllCategories(Pageable pageable);
    CategoryDTO updateCategory(UUID id, UpdateCategoryRequest request);
    void deleteCategory(UUID id);
}
