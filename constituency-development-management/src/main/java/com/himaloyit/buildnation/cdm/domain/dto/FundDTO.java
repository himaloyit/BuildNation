package com.himaloyit.buildnation.cdm.domain.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FundDTO {
    private UUID id;
    private LocalDate month;
    private String fundType;
    private UUID categoryId;
    private UUID subCategoryId;
    private BigDecimal receivedAmount;
    private BigDecimal allocatedAmount;
    private BigDecimal spentAmount;
    private BigDecimal remainingAmount;
}
