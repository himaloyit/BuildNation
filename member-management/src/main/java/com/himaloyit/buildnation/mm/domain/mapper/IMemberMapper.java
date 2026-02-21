package com.himaloyit.buildnation.mm.domain.mapper;

import com.himaloyit.buildnation.mm.domain.dto.MemberDTO;
import com.himaloyit.buildnation.mm.domain.entities.Member;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/*
 * Author: Rajib Kumer Ghosh
 */

@Mapper(componentModel = "spring")
public interface IMemberMapper {

    MemberDTO toDto(Member member);

    @Mapping(target = "profile", ignore = true)
    @Mapping(target = "communicationPreference", ignore = true)
    Member toEntity(MemberDTO memberDTO);
}
