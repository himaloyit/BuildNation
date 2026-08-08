package com.himaloyit.buildnation.cdm.fund.domain.entities;

import com.himaloyit.buildnation.cdm.prj.domain.entities.Category;
import com.himaloyit.buildnation.cdm.prj.domain.entities.SubCategory;
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
@Table(name = "cons_fund")
public class Fund {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private LocalDate month;

    @Column(nullable = false)
    private String fundType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sub_category_id")
    private SubCategory subCategory;

    @Column(nullable = false)
    private BigDecimal receivedAmount;

    @Column(nullable = false)
    private BigDecimal allocatedAmount;

    @Column(nullable = false)
    private BigDecimal spentAmount;

    @Column(nullable = false)
    private BigDecimal remainingAmount;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
