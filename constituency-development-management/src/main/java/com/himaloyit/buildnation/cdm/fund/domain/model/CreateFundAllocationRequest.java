package com.himaloyit.buildnation.cdm.fund.domain.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateFundAllocationRequest {

    @NotNull(message = "Fund id is mandatory")
    private UUID fundId;

    @NotNull(message = "Project id is mandatory")
    private UUID projectId;

    @NotNull(message = "Amount is mandatory")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;
}
