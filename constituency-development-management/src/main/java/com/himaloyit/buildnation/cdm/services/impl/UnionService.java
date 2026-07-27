package com.himaloyit.buildnation.cdm.services.impl;

import com.himaloyit.buildnation.cdm.domain.dto.UnionDTO;
import com.himaloyit.buildnation.cdm.domain.entities.Union;
import com.himaloyit.buildnation.cdm.domain.entities.Upazila;
import com.himaloyit.buildnation.cdm.domain.mapper.IUnionMapper;
import com.himaloyit.buildnation.cdm.domain.model.CreateUnionRequest;
import com.himaloyit.buildnation.cdm.domain.model.UpdateUnionRequest;
import com.himaloyit.buildnation.cdm.domain.repositories.iRepositories.IUnionRepository;
import com.himaloyit.buildnation.cdm.domain.repositories.iRepositories.IUpazilaRepository;
import com.himaloyit.buildnation.cdm.services.iServices.IUnionService;
import com.himaloyit.buildnation.cdm.util.exceptions.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
public class UnionService implements IUnionService {

    private final IUnionRepository iUnionRepository;
    private final IUpazilaRepository iUpazilaRepository;
    private final IUnionMapper iUnionMapper;

    public UnionService(IUnionRepository iUnionRepository, IUpazilaRepository iUpazilaRepository, IUnionMapper iUnionMapper) {
        this.iUnionRepository = iUnionRepository;
        this.iUpazilaRepository = iUpazilaRepository;
        this.iUnionMapper = iUnionMapper;
    }

    @Override
    @CacheEvict(value = "unions-list", allEntries = true)
    public UnionDTO createUnion(CreateUnionRequest request) {
        log.info("Creating union: code={}, upazilaId={}", request.getCode(), request.getUpazilaId());
        Upazila upazila = iUpazilaRepository.findById(request.getUpazilaId())
                .orElseThrow(() -> new EntityNotFoundException("Upazila not found with id: " + request.getUpazilaId()));

        Union union = Union.builder()
                .name(request.getName())
                .code(request.getCode())
                .upazila(upazila)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Union saved = iUnionRepository.save(union);
        log.info("Union created: id={}", saved.getId());
        return iUnionMapper.toDto(saved);
    }

    @Override
    @Cacheable(value = "unions", key = "#id")
    public UnionDTO getUnion(UUID id) {
        log.debug("Fetching union: id={}", id);
        return iUnionRepository.findById(id)
                .map(iUnionMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Union not found with id: " + id));
    }

    @Override
    @Cacheable(value = "unions-list", key = "#pageable.pageNumber + '-' + #pageable.pageSize")
    public Page<UnionDTO> getAllUnions(Pageable pageable) {
        return iUnionRepository.findAll(pageable).map(iUnionMapper::toDto);
    }

    @Override
    public Page<UnionDTO> getUnionsByUpazila(UUID upazilaId, Pageable pageable) {
        return iUnionRepository.findByUpazilaId(upazilaId, pageable).map(iUnionMapper::toDto);
    }

    @Override
    @Caching(
        put  = @CachePut(value = "unions", key = "#id"),
        evict = @CacheEvict(value = "unions-list", allEntries = true)
    )
    public UnionDTO updateUnion(UUID id, UpdateUnionRequest request) {
        log.info("Updating union: id={}", id);
        Union union = iUnionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Union not found with id: " + id));

        if (request.getName() != null) union.setName(request.getName());
        if (request.getCode() != null) union.setCode(request.getCode());
        if (request.getUpazilaId() != null) {
            Upazila upazila = iUpazilaRepository.findById(request.getUpazilaId())
                    .orElseThrow(() -> new EntityNotFoundException("Upazila not found with id: " + request.getUpazilaId()));
            union.setUpazila(upazila);
        }
        union.setUpdatedAt(LocalDateTime.now());

        UnionDTO updated = iUnionMapper.toDto(iUnionRepository.save(union));
        log.info("Union updated: id={}", id);
        return updated;
    }

    @Override
    @Caching(evict = {
        @CacheEvict(value = "unions", key = "#id"),
        @CacheEvict(value = "unions-list", allEntries = true)
    })
    public void deleteUnion(UUID id) {
        log.info("Deleting union: id={}", id);
        if (!iUnionRepository.existsById(id)) {
            throw new EntityNotFoundException("Union not found with id: " + id);
        }
        iUnionRepository.deleteById(id);
        log.info("Union deleted: id={}", id);
    }
}
