package com.himaloyit.buildnation.cdm.domain.model;

import com.himaloyit.buildnation.cdm.domain.enums.WorkOrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateWorkOrderStatusRequest {

    @NotNull(message = "Status is mandatory")
    private WorkOrderStatus status;
}
