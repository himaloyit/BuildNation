package com.himaloyit.buildnation.cdm.region.services.impl;

import com.himaloyit.buildnation.cdm.region.domain.dto.WardDTO;
import com.himaloyit.buildnation.cdm.region.domain.entities.Union;
import com.himaloyit.buildnation.cdm.region.domain.entities.Ward;
import com.himaloyit.buildnation.cdm.region.domain.mapper.IWardMapper;
import com.himaloyit.buildnation.cdm.region.domain.model.CreateWardRequest;
import com.himaloyit.buildnation.cdm.region.domain.model.UpdateWardRequest;
import com.himaloyit.buildnation.cdm.region.domain.repositories.iRepositories.IUnionRepository;
import com.himaloyit.buildnation.cdm.region.domain.repositories.iRepositories.IWardRepository;
import com.himaloyit.buildnation.cdm.region.services.iServices.IWardService;
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
public class WardService implements IWardService {

    private final IWardRepository iWardRepository;
    private final IUnionRepository iUnionRepository;
    private final IWardMapper iWardMapper;

    public WardService(IWardRepository iWardRepository, IUnionRepository iUnionRepository, IWardMapper iWardMapper) {
        this.iWardRepository = iWardRepository;
        this.iUnionRepository = iUnionRepository;
        this.iWardMapper = iWardMapper;
    }

    @Override
    public WardDTO createWard(CreateWardRequest request) {
        log.info("Creating ward: code={}, unionId={}", request.getCode(), request.getUnionId());
        Union union = iUnionRepository.findById(request.getUnionId())
                .orElseThrow(() -> new EntityNotFoundException("Union not found with id: " + request.getUnionId()));

        Ward ward = Ward.builder()
                .name(request.getName())
                .code(request.getCode())
                .union(union)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Ward saved = iWardRepository.save(ward);
        log.info("Ward created: id={}", saved.getId());
        return iWardMapper.toDto(saved);
    }

    @Override
    @Cacheable(value = "wards", key = "#id")
    public WardDTO getWard(UUID id) {
        log.debug("Fetching ward: id={}", id);
        return iWardRepository.findById(id)
                .map(iWardMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Ward not found with id: " + id));
    }

    @Override
    public Page<WardDTO> getAllWards(Pageable pageable) {
        return iWardRepository.findAll(pageable).map(iWardMapper::toDto);
    }

    @Override
    public Page<WardDTO> getWardsByUnion(UUID unionId, Pageable pageable) {
        return iWardRepository.findByUnionId(unionId, pageable).map(iWardMapper::toDto);
    }

    @Override
    @CachePut(value = "wards", key = "#id")
    public WardDTO updateWard(UUID id, UpdateWardRequest request) {
        log.info("Updating ward: id={}", id);
        Ward ward = iWardRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ward not found with id: " + id));

        if (request.getName() != null) ward.setName(request.getName());
        if (request.getCode() != null) ward.setCode(request.getCode());
        if (request.getUnionId() != null) {
            Union union = iUnionRepository.findById(request.getUnionId())
                    .orElseThrow(() -> new EntityNotFoundException("Union not found with id: " + request.getUnionId()));
            ward.setUnion(union);
        }
        ward.setUpdatedAt(LocalDateTime.now());

        WardDTO updated = iWardMapper.toDto(iWardRepository.save(ward));
        log.info("Ward updated: id={}", id);
        return updated;
    }

    @Override
    @CacheEvict(value = "wards", key = "#id")
    public void deleteWard(UUID id) {
        log.info("Deleting ward: id={}", id);
        if (!iWardRepository.existsById(id)) {
            throw new EntityNotFoundException("Ward not found with id: " + id);
        }
        iWardRepository.deleteById(id);
        log.info("Ward deleted: id={}", id);
    }
}
