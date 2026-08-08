package com.himaloyit.buildnation.cdm.region.services.impl;

import com.himaloyit.buildnation.cdm.region.domain.dto.UpazilaDTO;
import com.himaloyit.buildnation.cdm.region.domain.entities.District;
import com.himaloyit.buildnation.cdm.region.domain.entities.Upazila;
import com.himaloyit.buildnation.cdm.region.domain.mapper.IUpazilaMapper;
import com.himaloyit.buildnation.cdm.region.domain.model.CreateUpazilaRequest;
import com.himaloyit.buildnation.cdm.region.domain.model.UpdateUpazilaRequest;
import com.himaloyit.buildnation.cdm.region.domain.repositories.iRepositories.IDistrictRepository;
import com.himaloyit.buildnation.cdm.region.domain.repositories.iRepositories.IUpazilaRepository;
import com.himaloyit.buildnation.cdm.region.services.iServices.IUpazilaService;
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
public class UpazilaService implements IUpazilaService {

    private final IUpazilaRepository iUpazilaRepository;
    private final IDistrictRepository iDistrictRepository;
    private final IUpazilaMapper iUpazilaMapper;

    public UpazilaService(IUpazilaRepository iUpazilaRepository, IDistrictRepository iDistrictRepository, IUpazilaMapper iUpazilaMapper) {
        this.iUpazilaRepository = iUpazilaRepository;
        this.iDistrictRepository = iDistrictRepository;
        this.iUpazilaMapper = iUpazilaMapper;
    }

    @Override
    public UpazilaDTO createUpazila(CreateUpazilaRequest request) {
        log.info("Creating upazila: code={}, districtId={}", request.getCode(), request.getDistrictId());
        District district = iDistrictRepository.findById(request.getDistrictId())
                .orElseThrow(() -> new EntityNotFoundException("District not found with id: " + request.getDistrictId()));

        Upazila upazila = Upazila.builder()
                .name(request.getName())
                .code(request.getCode())
                .district(district)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Upazila saved = iUpazilaRepository.save(upazila);
        log.info("Upazila created: id={}", saved.getId());
        return iUpazilaMapper.toDto(saved);
    }

    @Override
    @Cacheable(value = "upazilas", key = "#id")
    public UpazilaDTO getUpazila(UUID id) {
        log.debug("Fetching upazila: id={}", id);
        return iUpazilaRepository.findById(id)
                .map(iUpazilaMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Upazila not found with id: " + id));
    }

    @Override
    public Page<UpazilaDTO> getAllUpazilas(Pageable pageable) {
        return iUpazilaRepository.findAll(pageable).map(iUpazilaMapper::toDto);
    }

    @Override
    public Page<UpazilaDTO> getUpazilasByDistrict(UUID districtId, Pageable pageable) {
        return iUpazilaRepository.findByDistrictId(districtId, pageable).map(iUpazilaMapper::toDto);
    }

    @Override
    @CachePut(value = "upazilas", key = "#id")
    public UpazilaDTO updateUpazila(UUID id, UpdateUpazilaRequest request) {
        log.info("Updating upazila: id={}", id);
        Upazila upazila = iUpazilaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Upazila not found with id: " + id));

        if (request.getName() != null) upazila.setName(request.getName());
        if (request.getCode() != null) upazila.setCode(request.getCode());
        if (request.getDistrictId() != null) {
            District district = iDistrictRepository.findById(request.getDistrictId())
                    .orElseThrow(() -> new EntityNotFoundException("District not found with id: " + request.getDistrictId()));
            upazila.setDistrict(district);
        }
        upazila.setUpdatedAt(LocalDateTime.now());

        UpazilaDTO updated = iUpazilaMapper.toDto(iUpazilaRepository.save(upazila));
        log.info("Upazila updated: id={}", id);
        return updated;
    }

    @Override
    @CacheEvict(value = "upazilas", key = "#id")
    public void deleteUpazila(UUID id) {
        log.info("Deleting upazila: id={}", id);
        if (!iUpazilaRepository.existsById(id)) {
            throw new EntityNotFoundException("Upazila not found with id: " + id);
        }
        iUpazilaRepository.deleteById(id);
        log.info("Upazila deleted: id={}", id);
    }
}
