package com.himaloyit.buildnation.cdm.domain.mapper;

import com.himaloyit.buildnation.cdm.domain.dto.ProjectDTO;
import com.himaloyit.buildnation.cdm.domain.entities.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface IProjectMapper {

    @Mapping(target = "categoryId", source = "category.id")
    @Mapping(target = "subCategoryId", source = "subCategory.id")
    @Mapping(target = "villageId", source = "village.id")
    ProjectDTO toDto(Project project);

    @Mapping(target = "category", ignore = true)
    @Mapping(target = "subCategory", ignore = true)
    @Mapping(target = "village", ignore = true)
    Project toEntity(ProjectDTO projectDTO);
}
