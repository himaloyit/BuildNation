package com.himaloyit.buildnation.cdm.region.services.iServices;

import com.himaloyit.buildnation.cdm.region.domain.dto.UpazilaDTO;
import com.himaloyit.buildnation.cdm.region.domain.model.CreateUpazilaRequest;
import com.himaloyit.buildnation.cdm.region.domain.model.UpdateUpazilaRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface IUpazilaService {

    UpazilaDTO createUpazila(CreateUpazilaRequest request);
    UpazilaDTO getUpazila(UUID id);
    Page<UpazilaDTO> getAllUpazilas(Pageable pageable);
    Page<UpazilaDTO> getUpazilasByDistrict(UUID districtId, Pageable pageable);
    UpazilaDTO updateUpazila(UUID id, UpdateUpazilaRequest request);
    void deleteUpazila(UUID id);
}
