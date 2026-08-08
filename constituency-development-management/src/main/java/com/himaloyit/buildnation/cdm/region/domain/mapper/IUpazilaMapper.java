package com.himaloyit.buildnation.cdm.region.domain.mapper;

import com.himaloyit.buildnation.cdm.region.domain.dto.UpazilaDTO;
import com.himaloyit.buildnation.cdm.region.domain.entities.Upazila;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface IUpazilaMapper {

    @Mapping(target = "districtId", source = "district.id")
    UpazilaDTO toDto(Upazila upazila);

    @Mapping(target = "district", ignore = true)
    Upazila toEntity(UpazilaDTO upazilaDTO);
}
