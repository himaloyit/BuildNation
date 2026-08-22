package com.himaloyit.buildnation.ui.dto.prj;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

/** Mirrors com.himaloyit.buildnation.cdm.prj.domain.model.UpdateProjectRequest. */
@Data
@AllArgsConstructor
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
