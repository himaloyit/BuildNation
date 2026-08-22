package com.himaloyit.buildnation.ui.dto.region;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/** Mirrors com.himaloyit.buildnation.cdm.region.domain.dto.WardDTO. */
@Data
@NoArgsConstructor
public class WardDTO {
    private UUID id;
    private String name;
    private String code;
    private UUID unionId;
}
