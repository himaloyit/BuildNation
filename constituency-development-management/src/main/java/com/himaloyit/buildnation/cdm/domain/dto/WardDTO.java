package com.himaloyit.buildnation.cdm.domain.dto;

import lombok.*;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WardDTO {
    private UUID id;
    private String name;
    private String code;
    private UUID unionId;
}
