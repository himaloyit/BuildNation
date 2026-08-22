package com.himaloyit.buildnation.ui.dto.wo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

/** Mirrors com.himaloyit.buildnation.cdm.wo.domain.dto.InspectionDTO. */
@Data
@NoArgsConstructor
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
