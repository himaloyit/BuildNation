package com.himaloyit.buildnation.cdm.domain.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateUpazilaRequest {

    @NotBlank(message = "Name is mandatory")
    private String name;

    @NotBlank(message = "Code is mandatory")
    private String code;

    @NotNull(message = "District id is mandatory")
    private UUID districtId;
}
