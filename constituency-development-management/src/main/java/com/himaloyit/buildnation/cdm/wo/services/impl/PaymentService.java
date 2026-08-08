package com.himaloyit.buildnation.cdm.wo.services.impl;

import com.himaloyit.buildnation.cdm.wo.domain.dto.PaymentDTO;
import com.himaloyit.buildnation.cdm.fund.domain.entities.Fund;
import com.himaloyit.buildnation.cdm.wo.domain.entities.Payment;
import com.himaloyit.buildnation.cdm.wo.domain.entities.WorkOrder;
import com.himaloyit.buildnation.cdm.wo.domain.enums.InspectionStatus;
import com.himaloyit.buildnation.cdm.wo.domain.enums.MilestoneType;
import com.himaloyit.buildnation.cdm.wo.domain.enums.PaymentStatus;
import com.himaloyit.buildnation.cdm.prj.domain.enums.ProjectStatus;
import com.himaloyit.buildnation.cdm.wo.domain.enums.WorkOrderStatus;
import com.himaloyit.buildnation.cdm.wo.domain.mapper.IPaymentMapper;
import com.himaloyit.buildnation.cdm.wo.domain.model.CreatePaymentRequest;
import com.himaloyit.buildnation.cdm.wo.domain.model.UpdatePaymentStatusRequest;
import com.himaloyit.buildnation.cdm.prj.domain.model.UpdateProjectStatusRequest;
import com.himaloyit.buildnation.cdm.fund.domain.repositories.iRepositories.IFundRepository;
import com.himaloyit.buildnation.cdm.wo.domain.repositories.iRepositories.IInspectionRepository;
import com.himaloyit.buildnation.cdm.wo.domain.repositories.iRepositories.IPaymentRepository;
import com.himaloyit.buildnation.cdm.wo.domain.repositories.iRepositories.IWorkOrderRepository;
import com.himaloyit.buildnation.cdm.wo.services.iServices.IPaymentService;
import com.himaloyit.buildnation.cdm.prj.services.iServices.IProjectService;
import com.himaloyit.buildnation.cdm.common.exceptions.EntityNotFoundException;
import com.himaloyit.buildnation.cdm.common.exceptions.InvalidStateException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class PaymentService implements IPaymentService {

    private final IPaymentRepository iPaymentRepository;
    private final IWorkOrderRepository iWorkOrderRepository;
    private final IInspectionRepository iInspectionRepository;
    private final IFundRepository iFundRepository;
    private final IProjectService iProjectService;
    private final IPaymentMapper iPaymentMapper;

    public PaymentService(IPaymentRepository iPaymentRepository, IWorkOrderRepository iWorkOrderRepository,
                           IInspectionRepository iInspectionRepository, IFundRepository iFundRepository,
                           IProjectService iProjectService, IPaymentMapper iPaymentMapper) {
        this.iPaymentRepository = iPaymentRepository;
        this.iWorkOrderRepository = iWorkOrderRepository;
        this.iInspectionRepository = iInspectionRepository;
        this.iFundRepository = iFundRepository;
        this.iProjectService = iProjectService;
        this.iPaymentMapper = iPaymentMapper;
    }

    @Override
    @Transactional
    public PaymentDTO createPayment(CreatePaymentRequest request) {
        log.info("Creating payment: workOrderId={}, milestoneType={}, percentage={}",
                request.getWorkOrderId(), request.getMilestoneType(), request.getPercentage());
        WorkOrder workOrder = iWorkOrderRepository.findById(request.getWorkOrderId())
                .orElseThrow(() -> new EntityNotFoundException("WorkOrder not found with id: " + request.getWorkOrderId()));

        List<Payment> existing = iPaymentRepository.findByWorkOrderIdAndStatusNot(workOrder.getId(), PaymentStatus.REJECTED);
        int existingPercentage = existing.stream().mapToInt(Payment::getPercentage).sum();
        if (existingPercentage + request.getPercentage() > 100) {
            throw new InvalidStateException("Total payment percentage would exceed 100% (already requested/approved/paid: "
                    + existingPercentage + "%, this request: " + request.getPercentage() + "%)");
        }

        BigDecimal amount = workOrder.getAmount()
                .multiply(BigDecimal.valueOf(request.getPercentage()))
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        Payment payment = Payment.builder()
                .workOrder(workOrder)
                .milestoneType(request.getMilestoneType())
                .percentage(request.getPercentage())
                .amount(amount)
                .status(PaymentStatus.REQUESTED)
                .requestedDate(LocalDate.now())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Payment saved = iPaymentRepository.save(payment);
        log.info("Payment created: id={}, amount={}", saved.getId(), saved.getAmount());
        return iPaymentMapper.toDto(saved);
    }

    private String generateVoucherNumber() {
        int year = LocalDate.now().getYear();
        long countThisYear = iPaymentRepository.countByVoucherNumberStartingWith("V-" + year + "-");
        return String.format("V-%d-%04d", year, countThisYear + 1);
    }

    @Override
    public PaymentDTO getPayment(UUID id) {
        log.debug("Fetching payment: id={}", id);
        return iPaymentRepository.findById(id)
                .map(iPaymentMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Payment not found with id: " + id));
    }

    @Override
    public Page<PaymentDTO> getAllPayments(Pageable pageable) {
        return iPaymentRepository.findAll(pageable).map(iPaymentMapper::toDto);
    }

    @Override
    public Page<PaymentDTO> getPaymentsByWorkOrder(UUID workOrderId, Pageable pageable) {
        return iPaymentRepository.findByWorkOrderId(workOrderId, pageable).map(iPaymentMapper::toDto);
    }

    @Override
    @Transactional
    @CacheEvict(value = "funds", allEntries = true)
    public PaymentDTO updatePaymentStatus(UUID id, UpdatePaymentStatusRequest request) {
        log.info("Updating payment status: id={}, status={}", id, request.getStatus());
        Payment payment = iPaymentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Payment not found with id: " + id));

        PaymentStatus current = payment.getStatus();
        PaymentStatus target = request.getStatus();

        switch (target) {
            case APPROVED -> {
                if (current != PaymentStatus.REQUESTED) {
                    throw new InvalidStateException("Only REQUESTED payments can be approved (current status: " + current + ")");
                }
                if (payment.getMilestoneType() == MilestoneType.FINAL
                        && !iInspectionRepository.existsByWorkOrderIdAndStatus(payment.getWorkOrder().getId(), InspectionStatus.APPROVED)) {
                    throw new InvalidStateException("Final payment requires an approved inspection before it can be approved");
                }
                payment.setStatus(PaymentStatus.APPROVED);
                payment.setApprovedDate(LocalDate.now());
            }
            case PAID -> {
                if (current != PaymentStatus.APPROVED) {
                    throw new InvalidStateException("Only APPROVED payments can be marked PAID (current status: " + current + ")");
                }
                payment.setStatus(PaymentStatus.PAID);
                payment.setPaymentDate(LocalDate.now());
                payment.setVoucherNumber(generateVoucherNumber());

                Fund fund = payment.getWorkOrder().getFundAllocation().getFund();
                fund.setSpentAmount(fund.getSpentAmount().add(payment.getAmount()));
                fund.setUpdatedAt(LocalDateTime.now());
                iFundRepository.save(fund);

                if (payment.getMilestoneType() == MilestoneType.FINAL) {
                    WorkOrder workOrder = payment.getWorkOrder();
                    workOrder.setStatus(WorkOrderStatus.COMPLETED);
                    workOrder.setUpdatedAt(LocalDateTime.now());
                    iWorkOrderRepository.save(workOrder);

                    iProjectService.updateProjectStatus(workOrder.getProject().getId(),
                            UpdateProjectStatusRequest.builder().status(ProjectStatus.COMPLETED).build());
                }
            }
            case REJECTED -> {
                if (current != PaymentStatus.REQUESTED) {
                    throw new InvalidStateException("Only REQUESTED payments can be rejected (current status: " + current + ")");
                }
                payment.setStatus(PaymentStatus.REJECTED);
            }
            case REQUESTED -> throw new InvalidStateException("Cannot manually set a payment status back to REQUESTED");
        }

        payment.setUpdatedAt(LocalDateTime.now());
        PaymentDTO updated = iPaymentMapper.toDto(iPaymentRepository.save(payment));
        log.info("Payment status updated: id={}, status={}", id, target);
        return updated;
    }

    @Override
    public void deletePayment(UUID id) {
        log.info("Deleting payment: id={}", id);
        Payment payment = iPaymentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Payment not found with id: " + id));

        if (payment.getStatus() == PaymentStatus.APPROVED || payment.getStatus() == PaymentStatus.PAID) {
            throw new InvalidStateException("Cannot delete a payment that is already " + payment.getStatus());
        }

        iPaymentRepository.deleteById(id);
        log.info("Payment deleted: id={}", id);
    }
}
