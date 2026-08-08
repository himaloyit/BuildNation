package com.himaloyit.buildnation.cdm.domain.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FundAllocationDTO {
    private UUID id;
    private UUID fundId;
    private UUID projectId;
    private BigDecimal amount;
}
