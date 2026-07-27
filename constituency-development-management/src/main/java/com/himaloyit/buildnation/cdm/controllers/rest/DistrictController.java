package com.himaloyit.buildnation.cdm.controllers.rest;

import com.himaloyit.buildnation.cdm.domain.dto.DistrictDTO;
import com.himaloyit.buildnation.cdm.domain.model.ApiResponse;
import com.himaloyit.buildnation.cdm.domain.model.CreateDistrictRequest;
import com.himaloyit.buildnation.cdm.domain.model.UpdateDistrictRequest;
import com.himaloyit.buildnation.cdm.services.iServices.IDistrictService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/districts")
public class DistrictController {

    private final IDistrictService iDistrictService;

    public DistrictController(IDistrictService iDistrictService) {
        this.iDistrictService = iDistrictService;
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<DistrictDTO>> createDistrict(@Valid @RequestBody CreateDistrictRequest request) {
        DistrictDTO saved = iDistrictService.createDistrict(request);
        return ResponseEntity.ok(ApiResponse.success("District created successfully", saved));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DistrictDTO>> getDistrictById(@PathVariable UUID id) {
        DistrictDTO district = iDistrictService.getDistrict(id);
        return ResponseEntity.ok(ApiResponse.success("District found", district));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<DistrictDTO>>> getAllDistricts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<DistrictDTO> pagedDistricts = iDistrictService.getAllDistricts(PageRequest.of(page, size));
        return ResponseEntity.ok(ApiResponse.success("Paged districts retrieved", pagedDistricts));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<DistrictDTO>> updateDistrict(
            @PathVariable UUID id,
            @RequestBody UpdateDistrictRequest request) {
        DistrictDTO updated = iDistrictService.updateDistrict(id, request);
        return ResponseEntity.ok(ApiResponse.success("District updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteDistrict(@PathVariable UUID id) {
        iDistrictService.deleteDistrict(id);
        return ResponseEntity.ok(ApiResponse.success("District deleted successfully", null));
    }
}
