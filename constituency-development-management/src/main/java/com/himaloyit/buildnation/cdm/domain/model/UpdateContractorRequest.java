package com.himaloyit.buildnation.cdm.domain.model;

import com.himaloyit.buildnation.cdm.domain.enums.ContractorType;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
