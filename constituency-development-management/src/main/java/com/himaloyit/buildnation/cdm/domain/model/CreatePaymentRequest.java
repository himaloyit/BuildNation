package com.himaloyit.buildnation.cdm.domain.model;

import com.himaloyit.buildnation.cdm.domain.enums.MilestoneType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreatePaymentRequest {

    @NotNull(message = "Work order id is mandatory")
    private UUID workOrderId;

    @NotNull(message = "Milestone type is mandatory")
    private MilestoneType milestoneType;

    @NotNull(message = "Percentage is mandatory")
    @Min(value = 1, message = "Percentage must be between 1 and 100")
    @Max(value = 100, message = "Percentage must be between 1 and 100")
    private Integer percentage;
}
