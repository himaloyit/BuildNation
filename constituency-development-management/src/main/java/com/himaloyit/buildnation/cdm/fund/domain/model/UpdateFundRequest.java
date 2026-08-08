package com.himaloyit.buildnation.cdm.fund.domain.model;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateFundRequest {
    private LocalDate month;
    private String fundType;
    private UUID categoryId;
    private UUID subCategoryId;
    private BigDecimal receivedAmount;
}
