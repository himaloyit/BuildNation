package com.himaloyit.buildnation.cdm.domain.model;

import com.himaloyit.buildnation.cdm.domain.enums.PaymentStatus;
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
