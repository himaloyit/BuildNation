package com.himaloyit.buildnation.cdm.services.impl;

import com.himaloyit.buildnation.cdm.domain.dto.DistrictDTO;
import com.himaloyit.buildnation.cdm.domain.entities.District;
import com.himaloyit.buildnation.cdm.domain.mapper.IDistrictMapper;
import com.himaloyit.buildnation.cdm.domain.model.CreateDistrictRequest;
import com.himaloyit.buildnation.cdm.domain.model.UpdateDistrictRequest;
import com.himaloyit.buildnation.cdm.domain.repositories.iRepositories.IDistrictRepository;
import com.himaloyit.buildnation.cdm.services.iServices.IDistrictService;
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
public class DistrictService implements IDistrictService {

    private final IDistrictRepository iDistrictRepository;
    private final IDistrictMapper iDistrictMapper;

    public DistrictService(IDistrictRepository iDistrictRepository, IDistrictMapper iDistrictMapper) {
        this.iDistrictRepository = iDistrictRepository;
        this.iDistrictMapper = iDistrictMapper;
    }

    @Override
    @CacheEvict(value = "districts-list", allEntries = true)
    public DistrictDTO createDistrict(CreateDistrictRequest request) {
        log.info("Creating district: code={}", request.getCode());
        District district = District.builder()
                .name(request.getName())
                .code(request.getCode())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        District saved = iDistrictRepository.save(district);
        log.info("District created: id={}", saved.getId());
        return iDistrictMapper.toDto(saved);
    }

    @Override
    @Cacheable(value = "districts", key = "#id")
    public DistrictDTO getDistrict(UUID id) {
        log.debug("Fetching district: id={}", id);
        return iDistrictRepository.findById(id)
                .map(iDistrictMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("District not found with id: " + id));
    }

    @Override
    @Cacheable(value = "districts-list", key = "#pageable.pageNumber + '-' + #pageable.pageSize")
    public Page<DistrictDTO> getAllDistricts(Pageable pageable) {
        return iDistrictRepository.findAll(pageable).map(iDistrictMapper::toDto);
    }

    @Override
    @Caching(
        put  = @CachePut(value = "districts", key = "#id"),
        evict = @CacheEvict(value = "districts-list", allEntries = true)
    )
    public DistrictDTO updateDistrict(UUID id, UpdateDistrictRequest request) {
        log.info("Updating district: id={}", id);
        District district = iDistrictRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("District not found with id: " + id));

        if (request.getName() != null) district.setName(request.getName());
        if (request.getCode() != null) district.setCode(request.getCode());
        district.setUpdatedAt(LocalDateTime.now());

        DistrictDTO updated = iDistrictMapper.toDto(iDistrictRepository.save(district));
        log.info("District updated: id={}", id);
        return updated;
    }

    @Override
    @Caching(evict = {
        @CacheEvict(value = "districts", key = "#id"),
        @CacheEvict(value = "districts-list", allEntries = true)
    })
    public void deleteDistrict(UUID id) {
        log.info("Deleting district: id={}", id);
        if (!iDistrictRepository.existsById(id)) {
            throw new EntityNotFoundException("District not found with id: " + id);
        }
        iDistrictRepository.deleteById(id);
        log.info("District deleted: id={}", id);
    }
}
