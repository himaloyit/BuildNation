package com.himaloyit.buildnation.cdm.services.iServices;

import com.himaloyit.buildnation.cdm.domain.dto.VillageDTO;
import com.himaloyit.buildnation.cdm.domain.model.CreateVillageRequest;
import com.himaloyit.buildnation.cdm.domain.model.UpdateVillageRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface IVillageService {

    VillageDTO createVillage(CreateVillageRequest request);
    VillageDTO getVillage(UUID id);
    Page<VillageDTO> getAllVillages(Pageable pageable);
    Page<VillageDTO> getVillagesByWard(UUID wardId, Pageable pageable);
    VillageDTO updateVillage(UUID id, UpdateVillageRequest request);
    void deleteVillage(UUID id);
}
