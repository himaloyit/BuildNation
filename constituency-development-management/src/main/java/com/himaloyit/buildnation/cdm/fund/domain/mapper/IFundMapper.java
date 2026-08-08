package com.himaloyit.buildnation.cdm.fund.domain.mapper;

import com.himaloyit.buildnation.cdm.fund.domain.dto.FundDTO;
import com.himaloyit.buildnation.cdm.fund.domain.entities.Fund;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface IFundMapper {

    @Mapping(target = "categoryId", source = "category.id")
    @Mapping(target = "subCategoryId", source = "subCategory.id")
    FundDTO toDto(Fund fund);

    @Mapping(target = "category", ignore = true)
    @Mapping(target = "subCategory", ignore = true)
    Fund toEntity(FundDTO fundDTO);
}
