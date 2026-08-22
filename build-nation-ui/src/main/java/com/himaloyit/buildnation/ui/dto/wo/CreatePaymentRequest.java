package com.himaloyit.buildnation.ui.dto.wo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

/** Mirrors com.himaloyit.buildnation.cdm.wo.domain.model.CreatePaymentRequest. */
@Data
@AllArgsConstructor
public class CreatePaymentRequest {
    private UUID workOrderId;
    private MilestoneType milestoneType;
    private Integer percentage;
}
