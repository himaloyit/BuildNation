package com.himaloyit.buildnation.cdm.region.domain.model;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateDistrictRequest {
    private String name;
    private String code;
}
