package com.himaloyit.buildnation.cdm.services.iServices;

import com.himaloyit.buildnation.cdm.domain.dto.FundDTO;
import com.himaloyit.buildnation.cdm.domain.model.CreateFundRequest;
import com.himaloyit.buildnation.cdm.domain.model.UpdateFundRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface IFundService {

    FundDTO createFund(CreateFundRequest request);
    FundDTO getFund(UUID id);
    Page<FundDTO> getAllFunds(Pageable pageable);
    Page<FundDTO> getFundsByCategory(UUID categoryId, Pageable pageable);
    FundDTO updateFund(UUID id, UpdateFundRequest request);
    void deleteFund(UUID id);
}
