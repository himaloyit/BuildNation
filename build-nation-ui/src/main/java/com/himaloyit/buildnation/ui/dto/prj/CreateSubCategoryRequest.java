package com.himaloyit.buildnation.ui.dto.prj;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

/** Mirrors com.himaloyit.buildnation.cdm.prj.domain.model.CreateSubCategoryRequest. */
@Data
@AllArgsConstructor
public class CreateSubCategoryRequest {
    private String name;
    private String code;
    private UUID categoryId;
}
