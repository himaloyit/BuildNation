package com.himaloyit.buildnation.ui.dto.wo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Mirrors com.himaloyit.buildnation.cdm.wo.domain.model.UpdateWorkOrderRequest. */
@Data
@AllArgsConstructor
public class UpdateWorkOrderRequest {
    private BigDecimal amount;
    private LocalDate startDate;
    private LocalDate endDate;
}
