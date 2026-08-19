package com.himaloyit.buildnation.sac.rbac.domain.mapper;

import com.himaloyit.buildnation.sac.rbac.domain.dto.PrincipalDTO;
import com.himaloyit.buildnation.sac.rbac.domain.entities.Principal;
import org.mapstruct.Mapper;

/*
 * Author: Rajib Kumer Ghosh
 */

@Mapper(componentModel = "spring")
public interface IPrincipalMapper {

    PrincipalDTO toDto(Principal principal);
}
