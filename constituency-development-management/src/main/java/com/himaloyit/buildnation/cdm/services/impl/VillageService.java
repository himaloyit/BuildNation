package com.himaloyit.buildnation.cdm.services.impl;

import com.himaloyit.buildnation.cdm.domain.dto.VillageDTO;
import com.himaloyit.buildnation.cdm.domain.entities.Village;
import com.himaloyit.buildnation.cdm.domain.entities.Ward;
import com.himaloyit.buildnation.cdm.domain.mapper.IVillageMapper;
import com.himaloyit.buildnation.cdm.domain.model.CreateVillageRequest;
import com.himaloyit.buildnation.cdm.domain.model.UpdateVillageRequest;
import com.himaloyit.buildnation.cdm.domain.repositories.iRepositories.IVillageRepository;
import com.himaloyit.buildnation.cdm.domain.repositories.iRepositories.IWardRepository;
import com.himaloyit.buildnation.cdm.services.iServices.IVillageService;
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
public class VillageService implements IVillageService {

    private final IVillageRepository iVillageRepository;
    private final IWardRepository iWardRepository;
    private final IVillageMapper iVillageMapper;

    public VillageService(IVillageRepository iVillageRepository, IWardRepository iWardRepository, IVillageMapper iVillageMapper) {
        this.iVillageRepository = iVillageRepository;
        this.iWardRepository = iWardRepository;
        this.iVillageMapper = iVillageMapper;
    }

    @Override
    public VillageDTO createVillage(CreateVillageRequest request) {
        log.info("Creating village: code={}, wardId={}", request.getCode(), request.getWardId());
        Ward ward = iWardRepository.findById(request.getWardId())
                .orElseThrow(() -> new EntityNotFoundException("Ward not found with id: " + request.getWardId()));

        Village village = Village.builder()
                .name(request.getName())
                .code(request.getCode())
                .ward(ward)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Village saved = iVillageRepository.save(village);
        log.info("Village created: id={}", saved.getId());
        return iVillageMapper.toDto(saved);
    }

    @Override
    @Cacheable(value = "villages", key = "#id")
    public VillageDTO getVillage(UUID id) {
        log.debug("Fetching village: id={}", id);
        return iVillageRepository.findById(id)
                .map(iVillageMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Village not found with id: " + id));
    }

    @Override
    public Page<VillageDTO> getAllVillages(Pageable pageable) {
        return iVillageRepository.findAll(pageable).map(iVillageMapper::toDto);
    }

    @Override
    public Page<VillageDTO> getVillagesByWard(UUID wardId, Pageable pageable) {
        return iVillageRepository.findByWardId(wardId, pageable).map(iVillageMapper::toDto);
    }

    @Override
    @CachePut(value = "villages", key = "#id")
    public VillageDTO updateVillage(UUID id, UpdateVillageRequest request) {
        log.info("Updating village: id={}", id);
        Village village = iVillageRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Village not found with id: " + id));

        if (request.getName() != null) village.setName(request.getName());
        if (request.getCode() != null) village.setCode(request.getCode());
        if (request.getWardId() != null) {
            Ward ward = iWardRepository.findById(request.getWardId())
                    .orElseThrow(() -> new EntityNotFoundException("Ward not found with id: " + request.getWardId()));
            village.setWard(ward);
        }
        village.setUpdatedAt(LocalDateTime.now());

        VillageDTO updated = iVillageMapper.toDto(iVillageRepository.save(village));
        log.info("Village updated: id={}", id);
        return updated;
    }

    @Override
    @CacheEvict(value = "villages", key = "#id")
    public void deleteVillage(UUID id) {
        log.info("Deleting village: id={}", id);
        if (!iVillageRepository.existsById(id)) {
            throw new EntityNotFoundException("Village not found with id: " + id);
        }
        iVillageRepository.deleteById(id);
        log.info("Village deleted: id={}", id);
    }
}
