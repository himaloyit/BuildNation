package com.himaloyit.buildnation.cdm.wo.domain.model;

import com.himaloyit.buildnation.cdm.wo.domain.enums.PaymentStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdatePaymentStatusRequest {

    @NotNull(message = "Status is mandatory")
    private PaymentStatus status;
}
