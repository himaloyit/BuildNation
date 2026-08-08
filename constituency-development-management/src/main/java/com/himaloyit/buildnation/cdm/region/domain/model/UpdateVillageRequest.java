package com.himaloyit.buildnation.cdm.region.domain.model;

import lombok.*;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateVillageRequest {
    private String name;
    private String code;
    private UUID wardId;
}
