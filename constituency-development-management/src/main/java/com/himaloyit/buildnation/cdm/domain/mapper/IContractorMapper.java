package com.himaloyit.buildnation.cdm.domain.mapper;

import com.himaloyit.buildnation.cdm.domain.dto.ContractorDTO;
import com.himaloyit.buildnation.cdm.domain.entities.Contractor;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface IContractorMapper {

    ContractorDTO toDto(Contractor contractor);

    Contractor toEntity(ContractorDTO contractorDTO);
}
