package com.himaloyit.buildnation.sac.rbac.domain.mapper;

import com.himaloyit.buildnation.sac.rbac.domain.dto.PermissionDTO;
import com.himaloyit.buildnation.sac.rbac.domain.entities.Permission;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/*
 * Author: Rajib Kumer Ghosh
 */

@Mapper(componentModel = "spring")
public interface IPermissionMapper {

    @Mapping(target = "resourceId", source = "resource.resourceId")
    @Mapping(target = "resourceCode", source = "resource.resourceCode")
    PermissionDTO toDto(Permission permission);
}
