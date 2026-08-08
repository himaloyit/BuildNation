package com.himaloyit.buildnation.cdm.fund.services.iServices;

import com.himaloyit.buildnation.cdm.fund.domain.dto.FundAllocationDTO;
import com.himaloyit.buildnation.cdm.fund.domain.model.CreateFundAllocationRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface IFundAllocationService {

    FundAllocationDTO createAllocation(CreateFundAllocationRequest request);
    FundAllocationDTO getAllocation(UUID id);
    Page<FundAllocationDTO> getAllAllocations(Pageable pageable);
    Page<FundAllocationDTO> getAllocationsByFund(UUID fundId, Pageable pageable);
    Page<FundAllocationDTO> getAllocationsByProject(UUID projectId, Pageable pageable);
    void deleteAllocation(UUID id);
}
