package com.himaloyit.buildnation.cdm.controllers.rest;

import com.himaloyit.buildnation.cdm.domain.dto.VillageDTO;
import com.himaloyit.buildnation.cdm.domain.model.ApiResponse;
import com.himaloyit.buildnation.cdm.domain.model.CreateVillageRequest;
import com.himaloyit.buildnation.cdm.domain.model.UpdateVillageRequest;
import com.himaloyit.buildnation.cdm.services.iServices.IVillageService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/villages")
public class VillageController {

    private final IVillageService iVillageService;

    public VillageController(IVillageService iVillageService) {
        this.iVillageService = iVillageService;
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<VillageDTO>> createVillage(@Valid @RequestBody CreateVillageRequest request) {
        VillageDTO saved = iVillageService.createVillage(request);
        return ResponseEntity.ok(ApiResponse.success("Village created successfully", saved));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<VillageDTO>> getVillageById(@PathVariable UUID id) {
        VillageDTO village = iVillageService.getVillage(id);
        return ResponseEntity.ok(ApiResponse.success("Village found", village));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<VillageDTO>>> getAllVillages(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<VillageDTO> pagedVillages = iVillageService.getAllVillages(PageRequest.of(page, size));
        return ResponseEntity.ok(ApiResponse.success("Paged villages retrieved", pagedVillages));
    }

    @GetMapping("/by-ward/{wardId}")
    public ResponseEntity<ApiResponse<Page<VillageDTO>>> getVillagesByWard(
            @PathVariable UUID wardId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<VillageDTO> villages = iVillageService.getVillagesByWard(wardId, PageRequest.of(page, size));
        return ResponseEntity.ok(ApiResponse.success("Villages by ward retrieved", villages));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<VillageDTO>> updateVillage(
            @PathVariable UUID id,
            @RequestBody UpdateVillageRequest request) {
        VillageDTO updated = iVillageService.updateVillage(id, request);
        return ResponseEntity.ok(ApiResponse.success("Village updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteVillage(@PathVariable UUID id) {
        iVillageService.deleteVillage(id);
        return ResponseEntity.ok(ApiResponse.success("Village deleted successfully", null));
    }
}
