package com.himaloyit.buildnation.ui.dto.prj;

import lombok.AllArgsConstructor;
import lombok.Data;

/** Mirrors com.himaloyit.buildnation.cdm.prj.domain.model.UpdateCategoryRequest. */
@Data
@AllArgsConstructor
public class UpdateCategoryRequest {
    private String name;
    private String code;
}
