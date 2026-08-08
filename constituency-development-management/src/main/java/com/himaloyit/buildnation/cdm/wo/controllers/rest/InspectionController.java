package com.himaloyit.buildnation.cdm.wo.controllers.rest;

import com.himaloyit.buildnation.cdm.wo.domain.dto.InspectionDTO;
import com.himaloyit.buildnation.cdm.common.model.ApiResponse;
import com.himaloyit.buildnation.cdm.wo.domain.model.*;
import com.himaloyit.buildnation.cdm.wo.services.iServices.IInspectionService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/inspections")
public class InspectionController {

    private final IInspectionService iInspectionService;

    public InspectionController(IInspectionService iInspectionService) {
        this.iInspectionService = iInspectionService;
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<InspectionDTO>> createInspection(@Valid @RequestBody CreateInspectionRequest request) {
        InspectionDTO saved = iInspectionService.createInspection(request);
        return ResponseEntity.ok(ApiResponse.success("Inspection created successfully", saved));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<InspectionDTO>> getInspectionById(@PathVariable UUID id) {
        InspectionDTO inspection = iInspectionService.getInspection(id);
        return ResponseEntity.ok(ApiResponse.success("Inspection found", inspection));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<InspectionDTO>>> getAllInspections(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<InspectionDTO> pagedInspections = iInspectionService.getAllInspections(PageRequest.of(page, size));
        return ResponseEntity.ok(ApiResponse.success("Paged inspections retrieved", pagedInspections));
    }

    @GetMapping("/by-work-order/{workOrderId}")
    public ResponseEntity<ApiResponse<Page<InspectionDTO>>> getInspectionsByWorkOrder(
            @PathVariable UUID workOrderId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<InspectionDTO> inspections = iInspectionService.getInspectionsByWorkOrder(workOrderId, PageRequest.of(page, size));
        return ResponseEntity.ok(ApiResponse.success("Inspections by work order retrieved", inspections));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<InspectionDTO>> updateInspection(
            @PathVariable UUID id,
            @RequestBody UpdateInspectionRequest request) {
        InspectionDTO updated = iInspectionService.updateInspection(id, request);
        return ResponseEntity.ok(ApiResponse.success("Inspection updated successfully", updated));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<InspectionDTO>> updateInspectionStatus(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateInspectionStatusRequest request) {
        InspectionDTO updated = iInspectionService.updateInspectionStatus(id, request);
        return ResponseEntity.ok(ApiResponse.success("Inspection status updated", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteInspection(@PathVariable UUID id) {
        iInspectionService.deleteInspection(id);
        return ResponseEntity.ok(ApiResponse.success("Inspection deleted successfully", null));
    }
}
