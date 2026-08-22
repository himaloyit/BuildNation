package com.himaloyit.buildnation.ui.dto.region;

import lombok.AllArgsConstructor;
import lombok.Data;

/** Mirrors com.himaloyit.buildnation.cdm.region.domain.model.UpdateDistrictRequest. */
@Data
@AllArgsConstructor
public class UpdateDistrictRequest {
    private String name;
    private String code;
}
