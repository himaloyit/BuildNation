package com.himaloyit.buildnation.cdm.domain.mapper;

import com.himaloyit.buildnation.cdm.domain.dto.SubCategoryDTO;
import com.himaloyit.buildnation.cdm.domain.entities.SubCategory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ISubCategoryMapper {

    @Mapping(target = "categoryId", source = "category.id")
    SubCategoryDTO toDto(SubCategory subCategory);

    @Mapping(target = "category", ignore = true)
    SubCategory toEntity(SubCategoryDTO subCategoryDTO);
}
