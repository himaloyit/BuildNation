package com.himaloyit.buildnation.ui.dto.wo;

import lombok.AllArgsConstructor;
import lombok.Data;

/** Mirrors com.himaloyit.buildnation.cdm.wo.domain.model.UpdateInspectionStatusRequest. */
@Data
@AllArgsConstructor
public class UpdateInspectionStatusRequest {
    private InspectionStatus status;
}
