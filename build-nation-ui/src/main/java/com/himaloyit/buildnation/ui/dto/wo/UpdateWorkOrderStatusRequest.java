package com.himaloyit.buildnation.ui.dto.wo;

import lombok.AllArgsConstructor;
import lombok.Data;

/** Mirrors com.himaloyit.buildnation.cdm.wo.domain.model.UpdateWorkOrderStatusRequest. */
@Data
@AllArgsConstructor
public class UpdateWorkOrderStatusRequest {
    private WorkOrderStatus status;
}
