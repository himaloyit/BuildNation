package com.himaloyit.buildnation.ui.dto.region;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/** Mirrors com.himaloyit.buildnation.cdm.region.domain.dto.VillageDTO. */
@Data
@NoArgsConstructor
public class VillageDTO {
    private UUID id;
    private String name;
    private String code;
    private UUID wardId;
}
