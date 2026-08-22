package com.himaloyit.buildnation.ui.dto.wo;

import lombok.AllArgsConstructor;
import lombok.Data;

/** Mirrors com.himaloyit.buildnation.cdm.wo.domain.model.UpdateInspectionRequest. */
@Data
@AllArgsConstructor
public class UpdateInspectionRequest {
    private Integer progressPercentage;
    private String quality;
    private String remarks;
}
