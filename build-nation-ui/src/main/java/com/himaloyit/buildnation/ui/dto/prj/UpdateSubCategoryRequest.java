package com.himaloyit.buildnation.ui.dto.prj;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

/** Mirrors com.himaloyit.buildnation.cdm.prj.domain.model.UpdateSubCategoryRequest. */
@Data
@AllArgsConstructor
public class UpdateSubCategoryRequest {
    private String name;
    private String code;
    private UUID categoryId;
}
