package com.himaloyit.buildnation.ui.dto.wo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/** Mirrors com.himaloyit.buildnation.cdm.wo.domain.model.CreateWorkOrderRequest. */
@Data
@AllArgsConstructor
public class CreateWorkOrderRequest {
    private UUID projectId;
    private UUID contractorId;
    private UUID fundAllocationId;
    private BigDecimal amount;
    private LocalDate startDate;
    private LocalDate endDate;
}
