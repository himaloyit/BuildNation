package com.himaloyit.buildnation.cdm.region.domain.mapper;

import com.himaloyit.buildnation.cdm.region.domain.dto.DistrictDTO;
import com.himaloyit.buildnation.cdm.region.domain.entities.District;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface IDistrictMapper {

    DistrictDTO toDto(District district);

    District toEntity(DistrictDTO districtDTO);
}
