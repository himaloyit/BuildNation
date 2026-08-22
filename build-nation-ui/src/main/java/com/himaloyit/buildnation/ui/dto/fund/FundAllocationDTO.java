package com.himaloyit.buildnation.ui.dto.fund;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

/** Mirrors com.himaloyit.buildnation.cdm.fund.domain.dto.FundAllocationDTO. */
@Data
@NoArgsConstructor
public class FundAllocationDTO {
    private UUID id;
    private UUID fundId;
    private UUID projectId;
    private BigDecimal amount;
}
