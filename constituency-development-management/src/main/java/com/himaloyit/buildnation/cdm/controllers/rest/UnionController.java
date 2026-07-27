package com.himaloyit.buildnation.cdm.controllers.rest;

import com.himaloyit.buildnation.cdm.domain.dto.UnionDTO;
import com.himaloyit.buildnation.cdm.domain.model.ApiResponse;
import com.himaloyit.buildnation.cdm.domain.model.CreateUnionRequest;
import com.himaloyit.buildnation.cdm.domain.model.UpdateUnionRequest;
import com.himaloyit.buildnation.cdm.services.iServices.IUnionService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/unions")
public class UnionController {

    private final IUnionService iUnionService;

    public UnionController(IUnionService iUnionService) {
        this.iUnionService = iUnionService;
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<UnionDTO>> createUnion(@Valid @RequestBody CreateUnionRequest request) {
        UnionDTO saved = iUnionService.createUnion(request);
        return ResponseEntity.ok(ApiResponse.success("Union created successfully", saved));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UnionDTO>> getUnionById(@PathVariable UUID id) {
        UnionDTO union = iUnionService.getUnion(id);
        return ResponseEntity.ok(ApiResponse.success("Union found", union));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<UnionDTO>>> getAllUnions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<UnionDTO> pagedUnions = iUnionService.getAllUnions(PageRequest.of(page, size));
        return ResponseEntity.ok(ApiResponse.success("Paged unions retrieved", pagedUnions));
    }

    @GetMapping("/by-upazila/{upazilaId}")
    public ResponseEntity<ApiResponse<Page<UnionDTO>>> getUnionsByUpazila(
            @PathVariable UUID upazilaId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<UnionDTO> unions = iUnionService.getUnionsByUpazila(upazilaId, PageRequest.of(page, size));
        return ResponseEntity.ok(ApiResponse.success("Unions by upazila retrieved", unions));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UnionDTO>> updateUnion(
            @PathVariable UUID id,
            @RequestBody UpdateUnionRequest request) {
        UnionDTO updated = iUnionService.updateUnion(id, request);
        return ResponseEntity.ok(ApiResponse.success("Union updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUnion(@PathVariable UUID id) {
        iUnionService.deleteUnion(id);
        return ResponseEntity.ok(ApiResponse.success("Union deleted successfully", null));
    }
}
