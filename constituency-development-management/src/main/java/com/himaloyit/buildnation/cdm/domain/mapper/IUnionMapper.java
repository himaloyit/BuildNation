package com.himaloyit.buildnation.cdm.domain.mapper;

import com.himaloyit.buildnation.cdm.domain.dto.UnionDTO;
import com.himaloyit.buildnation.cdm.domain.entities.Union;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface IUnionMapper {

    @Mapping(target = "upazilaId", source = "upazila.id")
    UnionDTO toDto(Union union);

    @Mapping(target = "upazila", ignore = true)
    Union toEntity(UnionDTO unionDTO);
}
