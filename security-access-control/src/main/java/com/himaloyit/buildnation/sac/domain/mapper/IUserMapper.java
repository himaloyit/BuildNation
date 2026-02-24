package com.himaloyit.buildnation.sac.domain.mapper;

import com.himaloyit.buildnation.sac.domain.dto.UserDTO;
import com.himaloyit.buildnation.sac.domain.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/*
 * Author: Rajib Kumer Ghosh
 */

@Mapper(componentModel = "spring")
public interface IUserMapper {

    UserDTO toDto(User user);

    @Mapping(target = "password", ignore = true)
    @Mapping(target = "enabled", ignore = true)
    @Mapping(target = "accountNonLocked", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "id", ignore = true)
    User toEntity(UserDTO userDTO);
}
