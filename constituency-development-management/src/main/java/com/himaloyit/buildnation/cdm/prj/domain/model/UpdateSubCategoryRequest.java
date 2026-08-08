package com.himaloyit.buildnation.cdm.prj.domain.model;

import lombok.*;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateSubCategoryRequest {
    private String name;
    private String code;
    private UUID categoryId;
}
