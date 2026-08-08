package com.himaloyit.buildnation.cdm.region.domain.mapper;

import com.himaloyit.buildnation.cdm.region.domain.dto.WardDTO;
import com.himaloyit.buildnation.cdm.region.domain.entities.Ward;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface IWardMapper {

    @Mapping(target = "unionId", source = "union.id")
    WardDTO toDto(Ward ward);

    @Mapping(target = "union", ignore = true)
    Ward toEntity(WardDTO wardDTO);
}
