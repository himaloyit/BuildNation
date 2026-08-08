package com.himaloyit.buildnation.cdm.domain.entities;

import com.himaloyit.buildnation.cdm.domain.enums.MilestoneType;
import com.himaloyit.buildnation.cdm.domain.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "cons_payment")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "work_order_id", nullable = false)
    private WorkOrder workOrder;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MilestoneType milestoneType;

    @Column(nullable = false)
    private Integer percentage;

    @Column(nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    private String voucherNumber;

    @Column(nullable = false)
    private LocalDate requestedDate;

    private LocalDate approvedDate;
    private LocalDate paymentDate;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
