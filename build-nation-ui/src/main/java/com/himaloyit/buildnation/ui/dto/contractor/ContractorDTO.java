package com.himaloyit.buildnation.ui.dto.contractor;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/** Mirrors com.himaloyit.buildnation.cdm.contractor.domain.dto.ContractorDTO. */
@Data
@NoArgsConstructor
public class ContractorDTO {
    private UUID id;
    private String name;
    private ContractorType type;
    private ContractorStatus status;
    private String contactNumber;
    private String address;
    private String license;
    private String keyPersonName;
    private String keyPersonContact;
    private String bankName;
    private String bankAccountNumber;
    private String bankBranch;
}
