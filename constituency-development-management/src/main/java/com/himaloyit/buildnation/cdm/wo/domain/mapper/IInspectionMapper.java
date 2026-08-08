package com.himaloyit.buildnation.cdm.wo.domain.mapper;

import com.himaloyit.buildnation.cdm.wo.domain.dto.InspectionDTO;
import com.himaloyit.buildnation.cdm.wo.domain.entities.Inspection;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface IInspectionMapper {

    @Mapping(target = "workOrderId", source = "workOrder.id")
    InspectionDTO toDto(Inspection inspection);

    @Mapping(target = "workOrder", ignore = true)
    Inspection toEntity(InspectionDTO inspectionDTO);
}
