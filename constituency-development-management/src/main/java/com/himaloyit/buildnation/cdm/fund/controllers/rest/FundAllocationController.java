package com.himaloyit.buildnation.cdm.fund.controllers.rest;

import com.himaloyit.buildnation.cdm.fund.domain.dto.FundAllocationDTO;
import com.himaloyit.buildnation.cdm.common.model.ApiResponse;
import com.himaloyit.buildnation.cdm.fund.domain.model.CreateFundAllocationRequest;
import com.himaloyit.buildnation.cdm.fund.services.iServices.IFundAllocationService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/fund-allocations")
public class FundAllocationController {

    private final IFundAllocationService iFundAllocationService;

    public FundAllocationController(IFundAllocationService iFundAllocationService) {
        this.iFundAllocationService = iFundAllocationService;
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<FundAllocationDTO>> createAllocation(@Valid @RequestBody CreateFundAllocationRequest request) {
        FundAllocationDTO saved = iFundAllocationService.createAllocation(request);
        return ResponseEntity.ok(ApiResponse.success("Fund allocation created successfully", saved));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<FundAllocationDTO>> getAllocationById(@PathVariable UUID id) {
        FundAllocationDTO allocation = iFundAllocationService.getAllocation(id);
        return ResponseEntity.ok(ApiResponse.success("Fund allocation found", allocation));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<FundAllocationDTO>>> getAllAllocations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<FundAllocationDTO> pagedAllocations = iFundAllocationService.getAllAllocations(PageRequest.of(page, size));
        return ResponseEntity.ok(ApiResponse.success("Paged fund allocations retrieved", pagedAllocations));
    }

    @GetMapping("/by-fund/{fundId}")
    public ResponseEntity<ApiResponse<Page<FundAllocationDTO>>> getAllocationsByFund(
            @PathVariable UUID fundId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<FundAllocationDTO> allocations = iFundAllocationService.getAllocationsByFund(fundId, PageRequest.of(page, size));
        return ResponseEntity.ok(ApiResponse.success("Fund allocations by fund retrieved", allocations));
    }

    @GetMapping("/by-project/{projectId}")
    public ResponseEntity<ApiResponse<Page<FundAllocationDTO>>> getAllocationsByProject(
            @PathVariable UUID projectId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<FundAllocationDTO> allocations = iFundAllocationService.getAllocationsByProject(projectId, PageRequest.of(page, size));
        return ResponseEntity.ok(ApiResponse.success("Fund allocations by project retrieved", allocations));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteAllocation(@PathVariable UUID id) {
        iFundAllocationService.deleteAllocation(id);
        return ResponseEntity.ok(ApiResponse.success("Fund allocation deleted successfully", null));
    }
}
