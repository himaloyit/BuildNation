package com.himaloyit.buildnation.cdm.domain.model;

import com.himaloyit.buildnation.cdm.domain.enums.ContractorType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateContractorRequest {

    @NotBlank(message = "Name is mandatory")
    private String name;

    @NotNull(message = "Type is mandatory")
    private ContractorType type;

    @NotBlank(message = "Contact number is mandatory")
    private String contactNumber;

    @NotBlank(message = "Address is mandatory")
    private String address;

    private String license;

    @NotBlank(message = "Key person name is mandatory")
    private String keyPersonName;

    @NotBlank(message = "Key person contact is mandatory")
    private String keyPersonContact;

    private String bankName;
    private String bankAccountNumber;
    private String bankBranch;
}
