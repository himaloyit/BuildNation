package com.himaloyit.buildnation.cdm.services.impl;

import com.himaloyit.buildnation.cdm.domain.dto.SubCategoryDTO;
import com.himaloyit.buildnation.cdm.domain.entities.Category;
import com.himaloyit.buildnation.cdm.domain.entities.SubCategory;
import com.himaloyit.buildnation.cdm.domain.mapper.ISubCategoryMapper;
import com.himaloyit.buildnation.cdm.domain.model.CreateSubCategoryRequest;
import com.himaloyit.buildnation.cdm.domain.model.UpdateSubCategoryRequest;
import com.himaloyit.buildnation.cdm.domain.repositories.iRepositories.ICategoryRepository;
import com.himaloyit.buildnation.cdm.domain.repositories.iRepositories.ISubCategoryRepository;
import com.himaloyit.buildnation.cdm.services.iServices.ISubCategoryService;
import com.himaloyit.buildnation.cdm.util.exceptions.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
public class SubCategoryService implements ISubCategoryService {

    private final ISubCategoryRepository iSubCategoryRepository;
    private final ICategoryRepository iCategoryRepository;
    private final ISubCategoryMapper iSubCategoryMapper;

    public SubCategoryService(ISubCategoryRepository iSubCategoryRepository, ICategoryRepository iCategoryRepository, ISubCategoryMapper iSubCategoryMapper) {
        this.iSubCategoryRepository = iSubCategoryRepository;
        this.iCategoryRepository = iCategoryRepository;
        this.iSubCategoryMapper = iSubCategoryMapper;
    }

    @Override
    public SubCategoryDTO createSubCategory(CreateSubCategoryRequest request) {
        log.info("Creating subcategory: code={}, categoryId={}", request.getCode(), request.getCategoryId());
        Category category = iCategoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + request.getCategoryId()));

        SubCategory subCategory = SubCategory.builder()
                .name(request.getName())
                .code(request.getCode())
                .category(category)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        SubCategory saved = iSubCategoryRepository.save(subCategory);
        log.info("SubCategory created: id={}", saved.getId());
        return iSubCategoryMapper.toDto(saved);
    }

    @Override
    @Cacheable(value = "subcategories", key = "#id")
    public SubCategoryDTO getSubCategory(UUID id) {
        log.debug("Fetching subcategory: id={}", id);
        return iSubCategoryRepository.findById(id)
                .map(iSubCategoryMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("SubCategory not found with id: " + id));
    }

    @Override
    public Page<SubCategoryDTO> getAllSubCategories(Pageable pageable) {
        return iSubCategoryRepository.findAll(pageable).map(iSubCategoryMapper::toDto);
    }

    @Override
    public Page<SubCategoryDTO> getSubCategoriesByCategory(UUID categoryId, Pageable pageable) {
        return iSubCategoryRepository.findByCategoryId(categoryId, pageable).map(iSubCategoryMapper::toDto);
    }

    @Override
    @CachePut(value = "subcategories", key = "#id")
    public SubCategoryDTO updateSubCategory(UUID id, UpdateSubCategoryRequest request) {
        log.info("Updating subcategory: id={}", id);
        SubCategory subCategory = iSubCategoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("SubCategory not found with id: " + id));

        if (request.getName() != null) subCategory.setName(request.getName());
        if (request.getCode() != null) subCategory.setCode(request.getCode());
        if (request.getCategoryId() != null) {
            Category category = iCategoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + request.getCategoryId()));
            subCategory.setCategory(category);
        }
        subCategory.setUpdatedAt(LocalDateTime.now());

        SubCategoryDTO updated = iSubCategoryMapper.toDto(iSubCategoryRepository.save(subCategory));
        log.info("SubCategory updated: id={}", id);
        return updated;
    }

    @Override
    @CacheEvict(value = "subcategories", key = "#id")
    public void deleteSubCategory(UUID id) {
        log.info("Deleting subcategory: id={}", id);
        if (!iSubCategoryRepository.existsById(id)) {
            throw new EntityNotFoundException("SubCategory not found with id: " + id);
        }
        iSubCategoryRepository.deleteById(id);
        log.info("SubCategory deleted: id={}", id);
    }
}
