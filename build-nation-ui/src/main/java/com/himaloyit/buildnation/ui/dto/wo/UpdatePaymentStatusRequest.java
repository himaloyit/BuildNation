package com.himaloyit.buildnation.ui.dto.wo;

import lombok.AllArgsConstructor;
import lombok.Data;

/** Mirrors com.himaloyit.buildnation.cdm.wo.domain.model.UpdatePaymentStatusRequest. */
@Data
@AllArgsConstructor
public class UpdatePaymentStatusRequest {
    private PaymentStatus status;
}
