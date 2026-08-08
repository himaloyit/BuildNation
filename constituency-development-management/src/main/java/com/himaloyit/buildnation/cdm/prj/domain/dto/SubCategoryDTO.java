package com.himaloyit.buildnation.cdm.prj.domain.dto;

import lombok.*;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubCategoryDTO {
    private UUID id;
    private String name;
    private String code;
    private UUID categoryId;
}
