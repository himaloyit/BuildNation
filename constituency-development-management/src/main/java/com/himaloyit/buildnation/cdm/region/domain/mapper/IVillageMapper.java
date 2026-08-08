package com.himaloyit.buildnation.cdm.region.domain.mapper;

import com.himaloyit.buildnation.cdm.region.domain.dto.VillageDTO;
import com.himaloyit.buildnation.cdm.region.domain.entities.Village;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface IVillageMapper {

    @Mapping(target = "wardId", source = "ward.id")
    VillageDTO toDto(Village village);

    @Mapping(target = "ward", ignore = true)
    Village toEntity(VillageDTO villageDTO);
}
