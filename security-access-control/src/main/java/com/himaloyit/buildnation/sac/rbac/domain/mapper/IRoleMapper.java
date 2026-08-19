package com.himaloyit.buildnation.sac.rbac.domain.mapper;

import com.himaloyit.buildnation.sac.rbac.domain.dto.RoleDTO;
import com.himaloyit.buildnation.sac.rbac.domain.entities.Role;
import org.mapstruct.Mapper;

/*
 * Author: Rajib Kumer Ghosh
 */

@Mapper(componentModel = "spring")
public interface IRoleMapper {

    RoleDTO toDto(Role role);
}
