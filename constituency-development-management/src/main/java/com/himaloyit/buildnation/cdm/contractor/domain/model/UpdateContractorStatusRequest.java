package com.himaloyit.buildnation.cdm.contractor.domain.model;

import com.himaloyit.buildnation.cdm.contractor.domain.enums.ContractorStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateContractorStatusRequest {

    @NotNull(message = "Status is mandatory")
    private ContractorStatus status;
}
