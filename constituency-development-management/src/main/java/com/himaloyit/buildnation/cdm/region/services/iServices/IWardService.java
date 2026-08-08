package com.himaloyit.buildnation.cdm.region.services.iServices;

import com.himaloyit.buildnation.cdm.region.domain.dto.WardDTO;
import com.himaloyit.buildnation.cdm.region.domain.model.CreateWardRequest;
import com.himaloyit.buildnation.cdm.region.domain.model.UpdateWardRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface IWardService {

    WardDTO createWard(CreateWardRequest request);
    WardDTO getWard(UUID id);
    Page<WardDTO> getAllWards(Pageable pageable);
    Page<WardDTO> getWardsByUnion(UUID unionId, Pageable pageable);
    WardDTO updateWard(UUID id, UpdateWardRequest request);
    void deleteWard(UUID id);
}
