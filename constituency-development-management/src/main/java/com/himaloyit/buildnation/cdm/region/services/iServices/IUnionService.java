package com.himaloyit.buildnation.cdm.region.services.iServices;

import com.himaloyit.buildnation.cdm.region.domain.dto.UnionDTO;
import com.himaloyit.buildnation.cdm.region.domain.model.CreateUnionRequest;
import com.himaloyit.buildnation.cdm.region.domain.model.UpdateUnionRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface IUnionService {

    UnionDTO createUnion(CreateUnionRequest request);
    UnionDTO getUnion(UUID id);
    Page<UnionDTO> getAllUnions(Pageable pageable);
    Page<UnionDTO> getUnionsByUpazila(UUID upazilaId, Pageable pageable);
    UnionDTO updateUnion(UUID id, UpdateUnionRequest request);
    void deleteUnion(UUID id);
}
