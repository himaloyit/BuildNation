package com.himaloyit.buildnation.ui.dto.region;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/** Mirrors com.himaloyit.buildnation.cdm.region.domain.dto.UnionDTO. */
@Data
@NoArgsConstructor
public class UnionDTO {
    private UUID id;
    private String name;
    private String code;
    private UUID upazilaId;
}
