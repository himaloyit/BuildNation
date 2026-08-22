package com.himaloyit.buildnation.ui.dto.region;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

/** Mirrors com.himaloyit.buildnation.cdm.region.domain.model.CreateUpazilaRequest. */
@Data
@AllArgsConstructor
public class CreateUpazilaRequest {
    private String name;
    private String code;
    private UUID districtId;
}
