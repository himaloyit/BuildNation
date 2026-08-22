package com.himaloyit.buildnation.ui.dto.region;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

/** Mirrors com.himaloyit.buildnation.cdm.region.domain.model.CreateWardRequest. */
@Data
@AllArgsConstructor
public class CreateWardRequest {
    private String name;
    private String code;
    private UUID unionId;
}
