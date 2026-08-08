package com.himaloyit.buildnation.cdm.wo.domain.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateWorkOrderRequest {

    @NotNull(message = "Project id is mandatory")
    private UUID projectId;

    @NotNull(message = "Contractor id is mandatory")
    private UUID contractorId;

    @NotNull(message = "Fund allocation id is mandatory")
    private UUID fundAllocationId;

    @NotNull(message = "Amount is mandatory")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;

    @NotNull(message = "Start date is mandatory")
    private LocalDate startDate;

    @NotNull(message = "End date is mandatory")
    private LocalDate endDate;
}
