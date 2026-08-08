package com.himaloyit.buildnation.cdm.region.domain.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateVillageRequest {

    @NotBlank(message = "Name is mandatory")
    private String name;

    @NotBlank(message = "Code is mandatory")
    private String code;

    @NotNull(message = "Ward id is mandatory")
    private UUID wardId;
}
