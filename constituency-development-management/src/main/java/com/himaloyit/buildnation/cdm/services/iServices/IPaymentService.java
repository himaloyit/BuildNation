package com.himaloyit.buildnation.cdm.services.iServices;

import com.himaloyit.buildnation.cdm.domain.dto.PaymentDTO;
import com.himaloyit.buildnation.cdm.domain.model.CreatePaymentRequest;
import com.himaloyit.buildnation.cdm.domain.model.UpdatePaymentStatusRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface IPaymentService {

    PaymentDTO createPayment(CreatePaymentRequest request);
    PaymentDTO getPayment(UUID id);
    Page<PaymentDTO> getAllPayments(Pageable pageable);
    Page<PaymentDTO> getPaymentsByWorkOrder(UUID workOrderId, Pageable pageable);
    PaymentDTO updatePaymentStatus(UUID id, UpdatePaymentStatusRequest request);
    void deletePayment(UUID id);
}
