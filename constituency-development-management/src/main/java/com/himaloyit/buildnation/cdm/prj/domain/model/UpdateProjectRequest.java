package com.himaloyit.buildnation.cdm.prj.domain.model;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateProjectRequest {
    private String name;
    private String description;
    private String currentCondition;
    private UUID categoryId;
    private UUID subCategoryId;
    private UUID villageId;
    private BigDecimal estimatedCost;
    private String submittedBy;
}
