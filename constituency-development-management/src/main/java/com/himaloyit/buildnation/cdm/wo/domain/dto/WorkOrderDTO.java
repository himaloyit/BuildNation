package com.himaloyit.buildnation.cdm.wo.domain.dto;

import com.himaloyit.buildnation.cdm.wo.domain.enums.WorkOrderStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
