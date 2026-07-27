package com.himaloyit.buildnation.cdm.domain.mapper;

import com.himaloyit.buildnation.cdm.domain.dto.DistrictDTO;
import com.himaloyit.buildnation.cdm.domain.entities.District;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface IDistrictMapper {

    DistrictDTO toDto(District district);

    District toEntity(DistrictDTO districtDTO);
}
