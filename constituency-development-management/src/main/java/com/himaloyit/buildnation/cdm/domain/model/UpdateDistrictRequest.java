package com.himaloyit.buildnation.cdm.domain.model;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateDistrictRequest {
    private String name;
    private String code;
}
