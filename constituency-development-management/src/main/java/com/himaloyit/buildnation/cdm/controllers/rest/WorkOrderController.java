package com.himaloyit.buildnation.cdm.controllers.rest;

import com.himaloyit.buildnation.cdm.domain.dto.WorkOrderDTO;
import com.himaloyit.buildnation.cdm.domain.model.*;
import com.himaloyit.buildnation.cdm.services.iServices.IWorkOrderService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/work-orders")
public class WorkOrderController {

    private final IWorkOrderService iWorkOrderService;

    public WorkOrderController(IWorkOrderService iWorkOrderService) {
        this.iWorkOrderService = iWorkOrderService;
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<WorkOrderDTO>> createWorkOrder(@Valid @RequestBody CreateWorkOrderRequest request) {
        WorkOrderDTO saved = iWorkOrderService.createWorkOrder(request);
        return ResponseEntity.ok(ApiResponse.success("Work order created successfully", saved));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<WorkOrderDTO>> getWorkOrderById(@PathVariable UUID id) {
        WorkOrderDTO workOrder = iWorkOrderService.getWorkOrder(id);
        return ResponseEntity.ok(ApiResponse.success("Work order found", workOrder));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<WorkOrderDTO>>> getAllWorkOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<WorkOrderDTO> pagedWorkOrders = iWorkOrderService.getAllWorkOrders(PageRequest.of(page, size));
        return ResponseEntity.ok(ApiResponse.success("Paged work orders retrieved", pagedWorkOrders));
    }

    @GetMapping("/by-project/{projectId}")
    public ResponseEntity<ApiResponse<Page<WorkOrderDTO>>> getWorkOrdersByProject(
            @PathVariable UUID projectId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<WorkOrderDTO> workOrders = iWorkOrderService.getWorkOrdersByProject(projectId, PageRequest.of(page, size));
        return ResponseEntity.ok(ApiResponse.success("Work orders by project retrieved", workOrders));
    }

    @GetMapping("/by-contractor/{contractorId}")
    public ResponseEntity<ApiResponse<Page<WorkOrderDTO>>> getWorkOrdersByContractor(
            @PathVariable UUID contractorId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<WorkOrderDTO> workOrders = iWorkOrderService.getWorkOrdersByContractor(contractorId, PageRequest.of(page, size));
        return ResponseEntity.ok(ApiResponse.success("Work orders by contractor retrieved", workOrders));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<WorkOrderDTO>> updateWorkOrder(
            @PathVariable UUID id,
            @RequestBody UpdateWorkOrderRequest request) {
        WorkOrderDTO updated = iWorkOrderService.updateWorkOrder(id, request);
        return ResponseEntity.ok(ApiResponse.success("Work order updated successfully", updated));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<WorkOrderDTO>> updateWorkOrderStatus(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateWorkOrderStatusRequest request) {
        WorkOrderDTO updated = iWorkOrderService.updateWorkOrderStatus(id, request);
        return ResponseEntity.ok(ApiResponse.success("Work order status updated", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteWorkOrder(@PathVariable UUID id) {
        iWorkOrderService.deleteWorkOrder(id);
        return ResponseEntity.ok(ApiResponse.success("Work order deleted successfully", null));
    }
}
