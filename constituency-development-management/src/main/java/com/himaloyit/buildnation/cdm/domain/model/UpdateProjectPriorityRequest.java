package com.himaloyit.buildnation.cdm.domain.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateProjectPriorityRequest {

    @NotNull(message = "Priority score is mandatory")
    @PositiveOrZero(message = "Priority score must be zero or positive")
    private Integer priorityScore;
}
