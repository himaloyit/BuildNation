package com.himaloyit.buildnation.cdm.prj.domain.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateProjectRequest {

    @NotBlank(message = "Name is mandatory")
    private String name;

    private String description;

    private String currentCondition;

    @NotNull(message = "Category id is mandatory")
    private UUID categoryId;

    @NotNull(message = "SubCategory id is mandatory")
    private UUID subCategoryId;

    @NotNull(message = "Village id is mandatory")
    private UUID villageId;

    @NotNull(message = "Estimated cost is mandatory")
    @PositiveOrZero(message = "Estimated cost must be zero or positive")
    private BigDecimal estimatedCost;

    @PositiveOrZero(message = "Priority score must be zero or positive")
    private Integer priorityScore;

    private String submittedBy;
}
