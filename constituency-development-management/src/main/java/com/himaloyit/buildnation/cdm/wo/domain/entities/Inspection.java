package com.himaloyit.buildnation.cdm.wo.domain.entities;

import com.himaloyit.buildnation.cdm.wo.domain.enums.InspectionStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "cons_inspection")
public class Inspection {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "work_order_id", nullable = false)
    private WorkOrder workOrder;

    @Column(nullable = false)
    private String inspectorName;

    @Column(nullable = false)
    private Integer progressPercentage;

    @Column(nullable = false)
    private String quality;

    private String remarks;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InspectionStatus status;

    @Column(nullable = false)
    private LocalDate inspectionDate;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
