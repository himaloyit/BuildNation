package com.himaloyit.buildnation.ui.dto.prj;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/** Mirrors com.himaloyit.buildnation.cdm.prj.domain.dto.SubCategoryDTO. */
@Data
@NoArgsConstructor
public class SubCategoryDTO {
    private UUID id;
    private String name;
    private String code;
    private UUID categoryId;
}
