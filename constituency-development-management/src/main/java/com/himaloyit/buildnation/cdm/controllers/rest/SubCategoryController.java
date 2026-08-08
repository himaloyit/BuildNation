package com.himaloyit.buildnation.cdm.controllers.rest;

import com.himaloyit.buildnation.cdm.domain.dto.SubCategoryDTO;
import com.himaloyit.buildnation.cdm.domain.model.ApiResponse;
import com.himaloyit.buildnation.cdm.domain.model.CreateSubCategoryRequest;
import com.himaloyit.buildnation.cdm.domain.model.UpdateSubCategoryRequest;
import com.himaloyit.buildnation.cdm.services.iServices.ISubCategoryService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/subcategories")
public class SubCategoryController {

    private final ISubCategoryService iSubCategoryService;

    public SubCategoryController(ISubCategoryService iSubCategoryService) {
        this.iSubCategoryService = iSubCategoryService;
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<SubCategoryDTO>> createSubCategory(@Valid @RequestBody CreateSubCategoryRequest request) {
        SubCategoryDTO saved = iSubCategoryService.createSubCategory(request);
        return ResponseEntity.ok(ApiResponse.success("SubCategory created successfully", saved));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SubCategoryDTO>> getSubCategoryById(@PathVariable UUID id) {
        SubCategoryDTO subCategory = iSubCategoryService.getSubCategory(id);
        return ResponseEntity.ok(ApiResponse.success("SubCategory found", subCategory));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<SubCategoryDTO>>> getAllSubCategories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<SubCategoryDTO> pagedSubCategories = iSubCategoryService.getAllSubCategories(PageRequest.of(page, size));
        return ResponseEntity.ok(ApiResponse.success("Paged subcategories retrieved", pagedSubCategories));
    }

    @GetMapping("/by-category/{categoryId}")
    public ResponseEntity<ApiResponse<Page<SubCategoryDTO>>> getSubCategoriesByCategory(
            @PathVariable UUID categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<SubCategoryDTO> subCategories = iSubCategoryService.getSubCategoriesByCategory(categoryId, PageRequest.of(page, size));
        return ResponseEntity.ok(ApiResponse.success("SubCategories by category retrieved", subCategories));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<SubCategoryDTO>> updateSubCategory(
            @PathVariable UUID id,
            @RequestBody UpdateSubCategoryRequest request) {
        SubCategoryDTO updated = iSubCategoryService.updateSubCategory(id, request);
        return ResponseEntity.ok(ApiResponse.success("SubCategory updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteSubCategory(@PathVariable UUID id) {
        iSubCategoryService.deleteSubCategory(id);
        return ResponseEntity.ok(ApiResponse.success("SubCategory deleted successfully", null));
    }
}
