package com.himaloyit.buildnation.ui.dto.wo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

/** Mirrors com.himaloyit.buildnation.cdm.wo.domain.model.CreateInspectionRequest. */
@Data
@AllArgsConstructor
public class CreateInspectionRequest {
    private UUID workOrderId;
    private String inspectorName;
    private Integer progressPercentage;
    private String quality;
    private String remarks;
    private LocalDate inspectionDate;
}
