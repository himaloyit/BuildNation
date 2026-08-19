package com.himaloyit.buildnation.sac.rbac.services.impl;

import com.himaloyit.buildnation.sac.rbac.domain.dto.ResourceDTO;
import com.himaloyit.buildnation.sac.rbac.domain.entities.Resource;
import com.himaloyit.buildnation.sac.rbac.domain.enums.Status;
import com.himaloyit.buildnation.sac.rbac.domain.mapper.IResourceMapper;
import com.himaloyit.buildnation.sac.rbac.domain.model.CreateResourceRequest;
import com.himaloyit.buildnation.sac.rbac.domain.model.UpdateResourceRequest;
import com.himaloyit.buildnation.sac.rbac.domain.repositories.iRepositories.IResourceRepository;
import com.himaloyit.buildnation.sac.rbac.security.CurrentPrincipalContext;
import com.himaloyit.buildnation.sac.rbac.services.iServices.IResourceService;
import com.himaloyit.buildnation.sac.rbac.util.exceptions.DuplicateCodeException;
import com.himaloyit.buildnation.sac.rbac.util.exceptions.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

/*
 * Author: Rajib Kumer Ghosh
 */

@Slf4j
@Service
public class ResourceService implements IResourceService {

    private final IResourceRepository resourceRepository;
    private final IResourceMapper resourceMapper;

    public ResourceService(IResourceRepository resourceRepository, IResourceMapper resourceMapper) {
        this.resourceRepository = resourceRepository;
        this.resourceMapper = resourceMapper;
    }

    @Override
    public ResourceDTO createResource(CreateResourceRequest request) {
        if (resourceRepository.existsByResourceCode(request.getResourceCode())) {
            throw new DuplicateCodeException("Resource already exists with code: " + request.getResourceCode());
        }

        String caller = CurrentPrincipalContext.currentPrincipalCode();
        Resource resource = Resource.builder()
                .resourceCode(request.getResourceCode())
                .resourceName(request.getResourceName())
                .serviceName(request.getServiceName())
                .apiPath(request.getApiPath())
                .httpMethod(request.getHttpMethod())
                .resourceType(request.getResourceType())
                .status(Status.ACTIVE)
                .createdBy(caller)
                .createdDate(LocalDateTime.now())
                .updatedBy(caller)
                .updatedDate(LocalDateTime.now())
                .build();

        Resource saved = resourceRepository.save(resource);
        log.info("Resource created: id={}, code={}", saved.getResourceId(), saved.getResourceCode());
        return resourceMapper.toDto(saved);
    }

    @Override
    @Cacheable(value = "resources", key = "#id")
    public ResourceDTO getResource(UUID id) {
        return resourceRepository.findById(id)
                .map(resourceMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Resource not found with id: " + id));
    }

    @Override
    public Page<ResourceDTO> getAllResources(Pageable pageable) {
        return resourceRepository.findAll(pageable).map(resourceMapper::toDto);
    }

    @Override
    public Page<ResourceDTO> getResourcesByServiceName(String serviceName, Pageable pageable) {
        return resourceRepository.findByServiceName(serviceName, pageable).map(resourceMapper::toDto);
    }

    @Override
    @CachePut(value = "resources", key = "#id")
    public ResourceDTO updateResource(UUID id, UpdateResourceRequest request) {
        Resource resource = resourceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Resource not found with id: " + id));

        if (request.getResourceName() != null) resource.setResourceName(request.getResourceName());
        if (request.getServiceName() != null) resource.setServiceName(request.getServiceName());
        if (request.getApiPath() != null) resource.setApiPath(request.getApiPath());
        if (request.getHttpMethod() != null) resource.setHttpMethod(request.getHttpMethod());
        if (request.getResourceType() != null) resource.setResourceType(request.getResourceType());
        resource.setUpdatedBy(CurrentPrincipalContext.currentPrincipalCode());
        resource.setUpdatedDate(LocalDateTime.now());

        return resourceMapper.toDto(resourceRepository.save(resource));
    }

    @Override
    @CacheEvict(value = "resources", key = "#id")
    public void deleteResource(UUID id) {
        if (!resourceRepository.existsById(id)) {
            throw new EntityNotFoundException("Resource not found with id: " + id);
        }
        resourceRepository.deleteById(id);
    }
}
