package com.himaloyit.buildnation.ui.dto.wo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/** Mirrors com.himaloyit.buildnation.cdm.wo.domain.dto.PaymentDTO. */
@Data
@NoArgsConstructor
public class PaymentDTO {
    private UUID id;
    private UUID workOrderId;
    private MilestoneType milestoneType;
    private Integer percentage;
    private BigDecimal amount;
    private PaymentStatus status;
    private String voucherNumber;
    private LocalDate requestedDate;
    private LocalDate approvedDate;
    private LocalDate paymentDate;
}
