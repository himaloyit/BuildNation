package com.himaloyit.buildnation.cdm.region.domain.dto;

import lombok.*;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VillageDTO {
    private UUID id;
    private String name;
    private String code;
    private UUID wardId;
}
