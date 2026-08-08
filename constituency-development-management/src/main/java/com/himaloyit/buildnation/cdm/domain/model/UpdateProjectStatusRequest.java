package com.himaloyit.buildnation.cdm.domain.model;

import com.himaloyit.buildnation.cdm.domain.enums.ProjectStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateProjectStatusRequest {

    @NotNull(message = "Status is mandatory")
    private ProjectStatus status;
}
