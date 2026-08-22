package com.himaloyit.buildnation.ui.dto.contractor;

import lombok.AllArgsConstructor;
import lombok.Data;

/** Mirrors com.himaloyit.buildnation.cdm.contractor.domain.model.UpdateContractorStatusRequest. */
@Data
@AllArgsConstructor
public class UpdateContractorStatusRequest {
    private ContractorStatus status;
}
