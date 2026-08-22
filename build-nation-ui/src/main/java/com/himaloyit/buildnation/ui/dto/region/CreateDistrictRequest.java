package com.himaloyit.buildnation.ui.dto.region;

import lombok.AllArgsConstructor;
import lombok.Data;

/** Mirrors com.himaloyit.buildnation.cdm.region.domain.model.CreateDistrictRequest. */
@Data
@AllArgsConstructor
public class CreateDistrictRequest {
    private String name;
    private String code;
}
