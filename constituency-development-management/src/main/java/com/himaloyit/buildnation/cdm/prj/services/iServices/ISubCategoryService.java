package com.himaloyit.buildnation.cdm.prj.services.iServices;

import com.himaloyit.buildnation.cdm.prj.domain.dto.SubCategoryDTO;
import com.himaloyit.buildnation.cdm.prj.domain.model.CreateSubCategoryRequest;
import com.himaloyit.buildnation.cdm.prj.domain.model.UpdateSubCategoryRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ISubCategoryService {

    SubCategoryDTO createSubCategory(CreateSubCategoryRequest request);
    SubCategoryDTO getSubCategory(UUID id);
    Page<SubCategoryDTO> getAllSubCategories(Pageable pageable);
    Page<SubCategoryDTO> getSubCategoriesByCategory(UUID categoryId, Pageable pageable);
    SubCategoryDTO updateSubCategory(UUID id, UpdateSubCategoryRequest request);
    void deleteSubCategory(UUID id);
}
