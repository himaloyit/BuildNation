package com.himaloyit.buildnation.cdm.domain.model;

import lombok.*;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateUnionRequest {
    private String name;
    private String code;
    private UUID upazilaId;
}
