package com.himaloyit.buildnation.cdm.controllers.rest;

import com.himaloyit.buildnation.cdm.domain.dto.UpazilaDTO;
import com.himaloyit.buildnation.cdm.domain.model.ApiResponse;
import com.himaloyit.buildnation.cdm.domain.model.CreateUpazilaRequest;
import com.himaloyit.buildnation.cdm.domain.model.UpdateUpazilaRequest;
import com.himaloyit.buildnation.cdm.services.iServices.IUpazilaService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/upazilas")
public class UpazilaController {

    private final IUpazilaService iUpazilaService;

    public UpazilaController(IUpazilaService iUpazilaService) {
        this.iUpazilaService = iUpazilaService;
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<UpazilaDTO>> createUpazila(@Valid @RequestBody CreateUpazilaRequest request) {
        UpazilaDTO saved = iUpazilaService.createUpazila(request);
        return ResponseEntity.ok(ApiResponse.success("Upazila created successfully", saved));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UpazilaDTO>> getUpazilaById(@PathVariable UUID id) {
        UpazilaDTO upazila = iUpazilaService.getUpazila(id);
        return ResponseEntity.ok(ApiResponse.success("Upazila found", upazila));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<UpazilaDTO>>> getAllUpazilas(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<UpazilaDTO> pagedUpazilas = iUpazilaService.getAllUpazilas(PageRequest.of(page, size));
        return ResponseEntity.ok(ApiResponse.success("Paged upazilas retrieved", pagedUpazilas));
    }

    @GetMapping("/by-district/{districtId}")
    public ResponseEntity<ApiResponse<Page<UpazilaDTO>>> getUpazilasByDistrict(
            @PathVariable UUID districtId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<UpazilaDTO> upazilas = iUpazilaService.getUpazilasByDistrict(districtId, PageRequest.of(page, size));
        return ResponseEntity.ok(ApiResponse.success("Upazilas by district retrieved", upazilas));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UpazilaDTO>> updateUpazila(
            @PathVariable UUID id,
            @RequestBody UpdateUpazilaRequest request) {
        UpazilaDTO updated = iUpazilaService.updateUpazila(id, request);
        return ResponseEntity.ok(ApiResponse.success("Upazila updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUpazila(@PathVariable UUID id) {
        iUpazilaService.deleteUpazila(id);
        return ResponseEntity.ok(ApiResponse.success("Upazila deleted successfully", null));
    }
}
