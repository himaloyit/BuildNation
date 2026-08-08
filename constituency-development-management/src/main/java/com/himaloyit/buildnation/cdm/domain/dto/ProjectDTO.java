package com.himaloyit.buildnation.cdm.domain.dto;

import com.himaloyit.buildnation.cdm.domain.enums.ProjectStatus;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectDTO {
    private UUID id;
    private String name;
    private String description;
    private String currentCondition;
    private UUID categoryId;
    private UUID subCategoryId;
    private UUID villageId;
    private BigDecimal estimatedCost;
    private Integer priorityScore;
    private Integer priorityRank;
    private String submittedBy;
    private ProjectStatus status;
}
