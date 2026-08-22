package com.himaloyit.buildnation.ui.dto.fund;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

/** Mirrors com.himaloyit.buildnation.cdm.fund.domain.model.CreateFundAllocationRequest. */
@Data
@AllArgsConstructor
public class CreateFundAllocationRequest {
    private UUID fundId;
    private UUID projectId;
    private BigDecimal amount;
}
