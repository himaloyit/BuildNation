package com.himaloyit.buildnation.ui.dto.prj;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

/** Mirrors com.himaloyit.buildnation.cdm.prj.domain.dto.ProjectDTO. */
@Data
@NoArgsConstructor
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
