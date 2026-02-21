package com.himaloyit.buildnation.mm.domain.mapper;

import com.himaloyit.buildnation.mm.domain.dto.MemberProfileDTO;
import com.himaloyit.buildnation.mm.domain.entities.MemberProfile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/*
 * Author: Rajib Kumer Ghosh
 */

@Mapper(componentModel = "spring")
public interface IMemberProfileMapper {

    MemberProfileDTO toDto(MemberProfile memberProfile);

    @Mapping(target = "member", ignore = true)
    MemberProfile toEntity(MemberProfileDTO memberProfileDTO);
}
