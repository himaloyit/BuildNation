package com.himaloyit.buildnation.cdm.contractor.services.iServices;

import com.himaloyit.buildnation.cdm.contractor.domain.dto.ContractorDTO;
import com.himaloyit.buildnation.cdm.contractor.domain.enums.ContractorStatus;
import com.himaloyit.buildnation.cdm.contractor.domain.enums.ContractorType;
import com.himaloyit.buildnation.cdm.contractor.domain.model.CreateContractorRequest;
import com.himaloyit.buildnation.cdm.contractor.domain.model.UpdateContractorRequest;
import com.himaloyit.buildnation.cdm.contractor.domain.model.UpdateContractorStatusRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface IContractorService {

    ContractorDTO createContractor(CreateContractorRequest request);
    ContractorDTO getContractor(UUID id);
    Page<ContractorDTO> getAllContractors(Pageable pageable);
    Page<ContractorDTO> getContractorsByType(ContractorType type, Pageable pageable);
    Page<ContractorDTO> getContractorsByStatus(ContractorStatus status, Pageable pageable);
    ContractorDTO updateContractor(UUID id, UpdateContractorRequest request);
    ContractorDTO updateContractorStatus(UUID id, UpdateContractorStatusRequest request);
    void deleteContractor(UUID id);
}
