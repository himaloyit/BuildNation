package com.himaloyit.buildnation.cdm.services.iServices;

import com.himaloyit.buildnation.cdm.domain.dto.DistrictDTO;
import com.himaloyit.buildnation.cdm.domain.model.CreateDistrictRequest;
import com.himaloyit.buildnation.cdm.domain.model.UpdateDistrictRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface IDistrictService {

    DistrictDTO createDistrict(CreateDistrictRequest request);
    DistrictDTO getDistrict(UUID id);
    Page<DistrictDTO> getAllDistricts(Pageable pageable);
    DistrictDTO updateDistrict(UUID id, UpdateDistrictRequest request);
    void deleteDistrict(UUID id);
}
