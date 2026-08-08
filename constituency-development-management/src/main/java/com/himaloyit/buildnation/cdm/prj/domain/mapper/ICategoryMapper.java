package com.himaloyit.buildnation.cdm.prj.domain.mapper;

import com.himaloyit.buildnation.cdm.prj.domain.dto.CategoryDTO;
import com.himaloyit.buildnation.cdm.prj.domain.entities.Category;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ICategoryMapper {

    CategoryDTO toDto(Category category);

    Category toEntity(CategoryDTO categoryDTO);
}
