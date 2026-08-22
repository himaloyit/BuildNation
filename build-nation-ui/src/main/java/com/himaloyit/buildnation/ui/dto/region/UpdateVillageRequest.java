package com.himaloyit.buildnation.ui.dto.region;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

/** Mirrors com.himaloyit.buildnation.cdm.region.domain.model.UpdateVillageRequest. */
@Data
@AllArgsConstructor
public class UpdateVillageRequest {
    private String name;
    private String code;
    private UUID wardId;
}
