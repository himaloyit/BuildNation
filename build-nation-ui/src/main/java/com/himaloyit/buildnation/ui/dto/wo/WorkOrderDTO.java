package com.himaloyit.buildnation.ui.dto.wo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/** Mirrors com.himaloyit.buildnation.cdm.wo.domain.dto.WorkOrderDTO. */
@Data
@NoArgsConstructor
public class WorkOrderDTO {
    private UUID id;
    private String workOrderNumber;
    private UUID projectId;
    private UUID contractorId;
    private UUID fundAllocationId;
    private BigDecimal amount;
    private LocalDate startDate;
    private LocalDate endDate;
    private WorkOrderStatus status;
}
