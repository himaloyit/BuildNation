package com.himaloyit.buildnation.cdm.controllers.rest;

import com.himaloyit.buildnation.cdm.domain.dto.FundDTO;
import com.himaloyit.buildnation.cdm.domain.model.ApiResponse;
import com.himaloyit.buildnation.cdm.domain.model.CreateFundRequest;
import com.himaloyit.buildnation.cdm.domain.model.UpdateFundRequest;
import com.himaloyit.buildnation.cdm.services.iServices.IFundService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/funds")
public class FundController {

    private final IFundService iFundService;

    public FundController(IFundService iFundService) {
        this.iFundService = iFundService;
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<FundDTO>> createFund(@Valid @RequestBody CreateFundRequest request) {
        FundDTO saved = iFundService.createFund(request);
        return ResponseEntity.ok(ApiResponse.success("Fund created successfully", saved));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<FundDTO>> getFundById(@PathVariable UUID id) {
        FundDTO fund = iFundService.getFund(id);
        return ResponseEntity.ok(ApiResponse.success("Fund found", fund));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<FundDTO>>> getAllFunds(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<FundDTO> pagedFunds = iFundService.getAllFunds(PageRequest.of(page, size));
        return ResponseEntity.ok(ApiResponse.success("Paged funds retrieved", pagedFunds));
    }

    @GetMapping("/by-category/{categoryId}")
    public ResponseEntity<ApiResponse<Page<FundDTO>>> getFundsByCategory(
            @PathVariable UUID categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<FundDTO> funds = iFundService.getFundsByCategory(categoryId, PageRequest.of(page, size));
        return ResponseEntity.ok(ApiResponse.success("Funds by category retrieved", funds));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<FundDTO>> updateFund(
            @PathVariable UUID id,
            @RequestBody UpdateFundRequest request) {
        FundDTO updated = iFundService.updateFund(id, request);
        return ResponseEntity.ok(ApiResponse.success("Fund updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteFund(@PathVariable UUID id) {
        iFundService.deleteFund(id);
        return ResponseEntity.ok(ApiResponse.success("Fund deleted successfully", null));
    }
}
