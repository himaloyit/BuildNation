package com.himaloyit.buildnation.cdm.domain.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateInspectionRequest {

    @NotNull(message = "Work order id is mandatory")
    private UUID workOrderId;

    @NotBlank(message = "Inspector name is mandatory")
    private String inspectorName;

    @NotNull(message = "Progress percentage is mandatory")
    @Min(value = 0, message = "Progress percentage must be between 0 and 100")
    @Max(value = 100, message = "Progress percentage must be between 0 and 100")
    private Integer progressPercentage;

    @NotBlank(message = "Quality is mandatory")
    private String quality;

    private String remarks;

    @NotNull(message = "Inspection date is mandatory")
    private LocalDate inspectionDate;
}
