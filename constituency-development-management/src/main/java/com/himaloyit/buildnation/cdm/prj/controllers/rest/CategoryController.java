package com.himaloyit.buildnation.cdm.prj.controllers.rest;

import com.himaloyit.buildnation.cdm.prj.domain.dto.CategoryDTO;
import com.himaloyit.buildnation.cdm.common.model.ApiResponse;
import com.himaloyit.buildnation.cdm.prj.domain.model.CreateCategoryRequest;
import com.himaloyit.buildnation.cdm.prj.domain.model.UpdateCategoryRequest;
import com.himaloyit.buildnation.cdm.prj.services.iServices.ICategoryService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {

    private final ICategoryService iCategoryService;

    public CategoryController(ICategoryService iCategoryService) {
        this.iCategoryService = iCategoryService;
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<CategoryDTO>> createCategory(@Valid @RequestBody CreateCategoryRequest request) {
        CategoryDTO saved = iCategoryService.createCategory(request);
        return ResponseEntity.ok(ApiResponse.success("Category created successfully", saved));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryDTO>> getCategoryById(@PathVariable UUID id) {
        CategoryDTO category = iCategoryService.getCategory(id);
        return ResponseEntity.ok(ApiResponse.success("Category found", category));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<CategoryDTO>>> getAllCategories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<CategoryDTO> pagedCategories = iCategoryService.getAllCategories(PageRequest.of(page, size));
        return ResponseEntity.ok(ApiResponse.success("Paged categories retrieved", pagedCategories));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryDTO>> updateCategory(
            @PathVariable UUID id,
            @RequestBody UpdateCategoryRequest request) {
        CategoryDTO updated = iCategoryService.updateCategory(id, request);
        return ResponseEntity.ok(ApiResponse.success("Category updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable UUID id) {
        iCategoryService.deleteCategory(id);
        return ResponseEntity.ok(ApiResponse.success("Category deleted successfully", null));
    }
}
