package com.himaloyit.buildnation.ui.dto.fund;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/** Mirrors com.himaloyit.buildnation.cdm.fund.domain.dto.FundDTO. */
@Data
@NoArgsConstructor
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
