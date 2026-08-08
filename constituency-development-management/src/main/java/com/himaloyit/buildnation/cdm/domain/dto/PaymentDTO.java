package com.himaloyit.buildnation.cdm.domain.dto;

import com.himaloyit.buildnation.cdm.domain.enums.MilestoneType;
import com.himaloyit.buildnation.cdm.domain.enums.PaymentStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
