package com.himaloyit.buildnation.cdm.domain.repositories.iRepositories;

import com.himaloyit.buildnation.cdm.domain.entities.Payment;
import com.himaloyit.buildnation.cdm.domain.enums.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface IPaymentRepository extends JpaRepository<Payment, UUID> {

    Page<Payment> findByWorkOrderId(UUID workOrderId, Pageable pageable);

    List<Payment> findByWorkOrderIdAndStatusNot(UUID workOrderId, PaymentStatus status);

    long countByVoucherNumberStartingWith(String prefix);
}
