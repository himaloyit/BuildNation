package com.himaloyit.buildnation.mm.domain.mapper;

import com.himaloyit.buildnation.mm.domain.dto.CommunicationPreferenceDTO;
import com.himaloyit.buildnation.mm.domain.entities.CommunicationPreference;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/*
 * Author: Rajib Kumer Ghosh
 */

@Mapper(componentModel = "spring")
public interface ICommunicationPreferenceMapper {

    CommunicationPreferenceDTO toDto(CommunicationPreference communicationPreference);

    @Mapping(target = "member", ignore = true)
    CommunicationPreference toEntity(CommunicationPreferenceDTO communicationPreferenceDTO);
}
