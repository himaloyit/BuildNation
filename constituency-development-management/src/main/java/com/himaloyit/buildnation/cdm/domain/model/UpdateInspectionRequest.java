package com.himaloyit.buildnation.cdm.domain.model;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateInspectionRequest {
    private Integer progressPercentage;
    private String quality;
    private String remarks;
}
