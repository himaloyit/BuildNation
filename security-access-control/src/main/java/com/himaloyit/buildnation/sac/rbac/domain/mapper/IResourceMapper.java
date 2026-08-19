package com.himaloyit.buildnation.sac.rbac.domain.mapper;

import com.himaloyit.buildnation.sac.rbac.domain.dto.ResourceDTO;
import com.himaloyit.buildnation.sac.rbac.domain.entities.Resource;
import org.mapstruct.Mapper;

/*
 * Author: Rajib Kumer Ghosh
 */

@Mapper(componentModel = "spring")
public interface IResourceMapper {

    ResourceDTO toDto(Resource resource);
}
