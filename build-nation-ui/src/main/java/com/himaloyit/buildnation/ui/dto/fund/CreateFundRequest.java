package com.himaloyit.buildnation.ui.dto.fund;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/** Mirrors com.himaloyit.buildnation.cdm.fund.domain.model.CreateFundRequest. */
@Data
@AllArgsConstructor
public class CreateFundRequest {
    private LocalDate month;
    private String fundType;
    private UUID categoryId;
    private UUID subCategoryId;
    private BigDecimal receivedAmount;
}
