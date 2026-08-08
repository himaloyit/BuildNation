package com.himaloyit.buildnation.cdm.wo.controllers.rest;

import com.himaloyit.buildnation.cdm.wo.domain.dto.PaymentDTO;
import com.himaloyit.buildnation.cdm.common.model.ApiResponse;
import com.himaloyit.buildnation.cdm.wo.domain.model.*;
import com.himaloyit.buildnation.cdm.wo.services.iServices.IPaymentService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private final IPaymentService iPaymentService;

    public PaymentController(IPaymentService iPaymentService) {
        this.iPaymentService = iPaymentService;
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<PaymentDTO>> createPayment(@Valid @RequestBody CreatePaymentRequest request) {
        PaymentDTO saved = iPaymentService.createPayment(request);
        return ResponseEntity.ok(ApiResponse.success("Payment created successfully", saved));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PaymentDTO>> getPaymentById(@PathVariable UUID id) {
        PaymentDTO payment = iPaymentService.getPayment(id);
        return ResponseEntity.ok(ApiResponse.success("Payment found", payment));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<PaymentDTO>>> getAllPayments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<PaymentDTO> pagedPayments = iPaymentService.getAllPayments(PageRequest.of(page, size));
        return ResponseEntity.ok(ApiResponse.success("Paged payments retrieved", pagedPayments));
    }

    @GetMapping("/by-work-order/{workOrderId}")
    public ResponseEntity<ApiResponse<Page<PaymentDTO>>> getPaymentsByWorkOrder(
            @PathVariable UUID workOrderId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<PaymentDTO> payments = iPaymentService.getPaymentsByWorkOrder(workOrderId, PageRequest.of(page, size));
        return ResponseEntity.ok(ApiResponse.success("Payments by work order retrieved", payments));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<PaymentDTO>> updatePaymentStatus(
            @PathVariable UUID id,
            @Valid @RequestBody UpdatePaymentStatusRequest request) {
        PaymentDTO updated = iPaymentService.updatePaymentStatus(id, request);
        return ResponseEntity.ok(ApiResponse.success("Payment status updated", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePayment(@PathVariable UUID id) {
        iPaymentService.deletePayment(id);
        return ResponseEntity.ok(ApiResponse.success("Payment deleted successfully", null));
    }
}
