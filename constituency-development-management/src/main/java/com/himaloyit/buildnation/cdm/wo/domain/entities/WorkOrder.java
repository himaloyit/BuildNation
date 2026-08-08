package com.himaloyit.buildnation.cdm.wo.domain.entities;

import com.himaloyit.buildnation.cdm.contractor.domain.entities.Contractor;
import com.himaloyit.buildnation.cdm.fund.domain.entities.FundAllocation;
import com.himaloyit.buildnation.cdm.prj.domain.entities.Project;
import com.himaloyit.buildnation.cdm.wo.domain.enums.WorkOrderStatus;
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
@Table(name = "cons_work_order")
public class WorkOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String workOrderNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contractor_id", nullable = false)
    private Contractor contractor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fund_allocation_id", nullable = false)
    private FundAllocation fundAllocation;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WorkOrderStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
