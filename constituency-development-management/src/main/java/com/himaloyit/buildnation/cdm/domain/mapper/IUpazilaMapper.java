package com.himaloyit.buildnation.cdm.domain.mapper;

import com.himaloyit.buildnation.cdm.domain.dto.UpazilaDTO;
import com.himaloyit.buildnation.cdm.domain.entities.Upazila;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface IUpazilaMapper {

    @Mapping(target = "districtId", source = "district.id")
    UpazilaDTO toDto(Upazila upazila);

    @Mapping(target = "district", ignore = true)
    Upazila toEntity(UpazilaDTO upazilaDTO);
}
