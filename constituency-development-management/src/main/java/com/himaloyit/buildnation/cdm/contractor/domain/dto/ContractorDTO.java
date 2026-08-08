package com.himaloyit.buildnation.cdm.contractor.domain.dto;

import com.himaloyit.buildnation.cdm.contractor.domain.enums.ContractorStatus;
import com.himaloyit.buildnation.cdm.contractor.domain.enums.ContractorType;
import lombok.*;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
