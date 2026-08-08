package com.himaloyit.buildnation.cdm.region.controllers.rest;

import com.himaloyit.buildnation.cdm.region.domain.dto.WardDTO;
import com.himaloyit.buildnation.cdm.common.model.ApiResponse;
import com.himaloyit.buildnation.cdm.region.domain.model.CreateWardRequest;
import com.himaloyit.buildnation.cdm.region.domain.model.UpdateWardRequest;
import com.himaloyit.buildnation.cdm.region.services.iServices.IWardService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/wards")
public class WardController {

    private final IWardService iWardService;

    public WardController(IWardService iWardService) {
        this.iWardService = iWardService;
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<WardDTO>> createWard(@Valid @RequestBody CreateWardRequest request) {
        WardDTO saved = iWardService.createWard(request);
        return ResponseEntity.ok(ApiResponse.success("Ward created successfully", saved));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<WardDTO>> getWardById(@PathVariable UUID id) {
        WardDTO ward = iWardService.getWard(id);
        return ResponseEntity.ok(ApiResponse.success("Ward found", ward));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<WardDTO>>> getAllWards(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<WardDTO> pagedWards = iWardService.getAllWards(PageRequest.of(page, size));
        return ResponseEntity.ok(ApiResponse.success("Paged wards retrieved", pagedWards));
    }

    @GetMapping("/by-union/{unionId}")
    public ResponseEntity<ApiResponse<Page<WardDTO>>> getWardsByUnion(
            @PathVariable UUID unionId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<WardDTO> wards = iWardService.getWardsByUnion(unionId, PageRequest.of(page, size));
        return ResponseEntity.ok(ApiResponse.success("Wards by union retrieved", wards));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<WardDTO>> updateWard(
            @PathVariable UUID id,
            @RequestBody UpdateWardRequest request) {
        WardDTO updated = iWardService.updateWard(id, request);
        return ResponseEntity.ok(ApiResponse.success("Ward updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteWard(@PathVariable UUID id) {
        iWardService.deleteWard(id);
        return ResponseEntity.ok(ApiResponse.success("Ward deleted successfully", null));
    }
}
