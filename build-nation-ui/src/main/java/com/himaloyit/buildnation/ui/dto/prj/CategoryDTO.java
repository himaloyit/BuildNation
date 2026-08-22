package com.himaloyit.buildnation.ui.dto.prj;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/** Mirrors com.himaloyit.buildnation.cdm.prj.domain.dto.CategoryDTO. */
@Data
@NoArgsConstructor
public class CategoryDTO {
    private UUID id;
    private String name;
    private String code;
}
