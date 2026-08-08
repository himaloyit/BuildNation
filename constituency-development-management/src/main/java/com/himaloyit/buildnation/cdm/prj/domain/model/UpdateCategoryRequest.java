package com.himaloyit.buildnation.cdm.prj.domain.model;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateCategoryRequest {
    private String name;
    private String code;
}
