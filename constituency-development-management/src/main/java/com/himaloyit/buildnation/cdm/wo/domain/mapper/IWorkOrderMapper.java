package com.himaloyit.buildnation.cdm.wo.domain.mapper;

import com.himaloyit.buildnation.cdm.wo.domain.dto.WorkOrderDTO;
import com.himaloyit.buildnation.cdm.wo.domain.entities.WorkOrder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface IWorkOrderMapper {

    @Mapping(target = "projectId", source = "project.id")
    @Mapping(target = "contractorId", source = "contractor.id")
    @Mapping(target = "fundAllocationId", source = "fundAllocation.id")
    WorkOrderDTO toDto(WorkOrder workOrder);

    @Mapping(target = "project", ignore = true)
    @Mapping(target = "contractor", ignore = true)
    @Mapping(target = "fundAllocation", ignore = true)
    WorkOrder toEntity(WorkOrderDTO workOrderDTO);
}
