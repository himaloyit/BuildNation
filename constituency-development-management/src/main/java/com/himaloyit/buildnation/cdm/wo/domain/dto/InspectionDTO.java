package com.himaloyit.buildnation.cdm.wo.domain.dto;

import com.himaloyit.buildnation.cdm.wo.domain.enums.InspectionStatus;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InspectionDTO {
    private UUID id;
    private UUID workOrderId;
    private String inspectorName;
    private Integer progressPercentage;
    private String quality;
    private String remarks;
    private InspectionStatus status;
    private LocalDate inspectionDate;
}
