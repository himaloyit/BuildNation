package com.himaloyit.buildnation.cdm.services.iServices;

import com.himaloyit.buildnation.cdm.domain.dto.InspectionDTO;
import com.himaloyit.buildnation.cdm.domain.model.CreateInspectionRequest;
import com.himaloyit.buildnation.cdm.domain.model.UpdateInspectionRequest;
import com.himaloyit.buildnation.cdm.domain.model.UpdateInspectionStatusRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface IInspectionService {

    InspectionDTO createInspection(CreateInspectionRequest request);
    InspectionDTO getInspection(UUID id);
    Page<InspectionDTO> getAllInspections(Pageable pageable);
    Page<InspectionDTO> getInspectionsByWorkOrder(UUID workOrderId, Pageable pageable);
    InspectionDTO updateInspection(UUID id, UpdateInspectionRequest request);
    InspectionDTO updateInspectionStatus(UUID id, UpdateInspectionStatusRequest request);
    void deleteInspection(UUID id);
}
