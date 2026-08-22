package com.himaloyit.buildnation.ui.dto.contractor;

import lombok.AllArgsConstructor;
import lombok.Data;

/** Mirrors com.himaloyit.buildnation.cdm.contractor.domain.model.UpdateContractorRequest. */
@Data
@AllArgsConstructor
public class UpdateContractorRequest {
    private String name;
    private ContractorType type;
    private String contactNumber;
    private String address;
    private String license;
    private String keyPersonName;
    private String keyPersonContact;
    private String bankName;
    private String bankAccountNumber;
    private String bankBranch;
}
