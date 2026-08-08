package com.himaloyit.buildnation.cdm.contractor.domain.mapper;

import com.himaloyit.buildnation.cdm.contractor.domain.dto.ContractorDTO;
import com.himaloyit.buildnation.cdm.contractor.domain.entities.Contractor;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface IContractorMapper {

    ContractorDTO toDto(Contractor contractor);

    Contractor toEntity(ContractorDTO contractorDTO);
}
