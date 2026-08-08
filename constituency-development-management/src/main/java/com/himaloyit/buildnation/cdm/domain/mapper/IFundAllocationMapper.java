package com.himaloyit.buildnation.cdm.domain.mapper;

import com.himaloyit.buildnation.cdm.domain.dto.FundAllocationDTO;
import com.himaloyit.buildnation.cdm.domain.entities.FundAllocation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface IFundAllocationMapper {

    @Mapping(target = "fundId", source = "fund.id")
    @Mapping(target = "projectId", source = "project.id")
    FundAllocationDTO toDto(FundAllocation fundAllocation);

    @Mapping(target = "fund", ignore = true)
    @Mapping(target = "project", ignore = true)
    FundAllocation toEntity(FundAllocationDTO fundAllocationDTO);
}
