package com.himaloyit.buildnation.cdm.prj.services.impl;

import com.himaloyit.buildnation.cdm.prj.domain.dto.CategoryDTO;
import com.himaloyit.buildnation.cdm.prj.domain.entities.Category;
import com.himaloyit.buildnation.cdm.prj.domain.mapper.ICategoryMapper;
import com.himaloyit.buildnation.cdm.prj.domain.model.CreateCategoryRequest;
import com.himaloyit.buildnation.cdm.prj.domain.model.UpdateCategoryRequest;
import com.himaloyit.buildnation.cdm.prj.domain.repositories.iRepositories.ICategoryRepository;
import com.himaloyit.buildnation.cdm.prj.services.iServices.ICategoryService;
import com.himaloyit.buildnation.cdm.common.exceptions.EntityNotFoundException;
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
public class CategoryService implements ICategoryService {

    private final ICategoryRepository iCategoryRepository;
    private final ICategoryMapper iCategoryMapper;

    public CategoryService(ICategoryRepository iCategoryRepository, ICategoryMapper iCategoryMapper) {
        this.iCategoryRepository = iCategoryRepository;
        this.iCategoryMapper = iCategoryMapper;
    }

    @Override
    public CategoryDTO createCategory(CreateCategoryRequest request) {
        log.info("Creating category: code={}", request.getCode());
        Category category = Category.builder()
                .name(request.getName())
                .code(request.getCode())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Category saved = iCategoryRepository.save(category);
        log.info("Category created: id={}", saved.getId());
        return iCategoryMapper.toDto(saved);
    }

    @Override
    @Cacheable(value = "categories", key = "#id")
    public CategoryDTO getCategory(UUID id) {
        log.debug("Fetching category: id={}", id);
        return iCategoryRepository.findById(id)
                .map(iCategoryMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + id));
    }

    @Override
    public Page<CategoryDTO> getAllCategories(Pageable pageable) {
        return iCategoryRepository.findAll(pageable).map(iCategoryMapper::toDto);
    }

    @Override
    @CachePut(value = "categories", key = "#id")
    public CategoryDTO updateCategory(UUID id, UpdateCategoryRequest request) {
        log.info("Updating category: id={}", id);
        Category category = iCategoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + id));

        if (request.getName() != null) category.setName(request.getName());
        if (request.getCode() != null) category.setCode(request.getCode());
        category.setUpdatedAt(LocalDateTime.now());

        CategoryDTO updated = iCategoryMapper.toDto(iCategoryRepository.save(category));
        log.info("Category updated: id={}", id);
        return updated;
    }

    @Override
    @CacheEvict(value = "categories", key = "#id")
    public void deleteCategory(UUID id) {
        log.info("Deleting category: id={}", id);
        if (!iCategoryRepository.existsById(id)) {
            throw new EntityNotFoundException("Category not found with id: " + id);
        }
        iCategoryRepository.deleteById(id);
        log.info("Category deleted: id={}", id);
    }
}
