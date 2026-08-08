package com.himaloyit.buildnation.cdm.controllers.rest;

import com.himaloyit.buildnation.cdm.domain.dto.ContractorDTO;
import com.himaloyit.buildnation.cdm.domain.enums.ContractorStatus;
import com.himaloyit.buildnation.cdm.domain.enums.ContractorType;
import com.himaloyit.buildnation.cdm.domain.model.*;
import com.himaloyit.buildnation.cdm.services.iServices.IContractorService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/contractors")
public class ContractorController {

    private final IContractorService iContractorService;

    public ContractorController(IContractorService iContractorService) {
        this.iContractorService = iContractorService;
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<ContractorDTO>> createContractor(@Valid @RequestBody CreateContractorRequest request) {
        ContractorDTO saved = iContractorService.createContractor(request);
        return ResponseEntity.ok(ApiResponse.success("Contractor created successfully", saved));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ContractorDTO>> getContractorById(@PathVariable UUID id) {
        ContractorDTO contractor = iContractorService.getContractor(id);
        return ResponseEntity.ok(ApiResponse.success("Contractor found", contractor));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<ContractorDTO>>> getAllContractors(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<ContractorDTO> pagedContractors = iContractorService.getAllContractors(PageRequest.of(page, size));
        return ResponseEntity.ok(ApiResponse.success("Paged contractors retrieved", pagedContractors));
    }

    @GetMapping("/by-type/{type}")
    public ResponseEntity<ApiResponse<Page<ContractorDTO>>> getContractorsByType(
            @PathVariable ContractorType type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<ContractorDTO> contractors = iContractorService.getContractorsByType(type, PageRequest.of(page, size));
        return ResponseEntity.ok(ApiResponse.success("Contractors by type retrieved", contractors));
    }

    @GetMapping("/by-status/{status}")
    public ResponseEntity<ApiResponse<Page<ContractorDTO>>> getContractorsByStatus(
            @PathVariable ContractorStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<ContractorDTO> contractors = iContractorService.getContractorsByStatus(status, PageRequest.of(page, size));
        return ResponseEntity.ok(ApiResponse.success("Contractors by status retrieved", contractors));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ContractorDTO>> updateContractor(
            @PathVariable UUID id,
            @RequestBody UpdateContractorRequest request) {
        ContractorDTO updated = iContractorService.updateContractor(id, request);
        return ResponseEntity.ok(ApiResponse.success("Contractor updated successfully", updated));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<ContractorDTO>> updateContractorStatus(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateContractorStatusRequest request) {
        ContractorDTO updated = iContractorService.updateContractorStatus(id, request);
        return ResponseEntity.ok(ApiResponse.success("Contractor status updated", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteContractor(@PathVariable UUID id) {
        iContractorService.deleteContractor(id);
        return ResponseEntity.ok(ApiResponse.success("Contractor deleted successfully", null));
    }
}
