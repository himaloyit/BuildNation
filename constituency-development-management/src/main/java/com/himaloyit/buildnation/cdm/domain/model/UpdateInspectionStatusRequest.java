package com.himaloyit.buildnation.cdm.domain.model;

import com.himaloyit.buildnation.cdm.domain.enums.InspectionStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateInspectionStatusRequest {

    @NotNull(message = "Status is mandatory")
    private InspectionStatus status;
}
