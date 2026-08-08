package com.himaloyit.buildnation.cdm.domain.model;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateWorkOrderRequest {
    private BigDecimal amount;
    private LocalDate startDate;
    private LocalDate endDate;
}
