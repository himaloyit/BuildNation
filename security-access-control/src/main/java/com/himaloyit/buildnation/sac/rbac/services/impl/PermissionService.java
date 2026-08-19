package com.himaloyit.buildnation.sac.rbac.services.impl;

import com.himaloyit.buildnation.sac.rbac.domain.dto.PermissionDTO;
import com.himaloyit.buildnation.sac.rbac.domain.entities.Permission;
import com.himaloyit.buildnation.sac.rbac.domain.entities.Resource;
import com.himaloyit.buildnation.sac.rbac.domain.enums.Status;
import com.himaloyit.buildnation.sac.rbac.domain.mapper.IPermissionMapper;
import com.himaloyit.buildnation.sac.rbac.domain.model.CreatePermissionRequest;
import com.himaloyit.buildnation.sac.rbac.domain.model.UpdatePermissionRequest;
import com.himaloyit.buildnation.sac.rbac.domain.repositories.iRepositories.IPermissionRepository;
import com.himaloyit.buildnation.sac.rbac.domain.repositories.iRepositories.IResourceRepository;
import com.himaloyit.buildnation.sac.rbac.security.CurrentPrincipalContext;
import com.himaloyit.buildnation.sac.rbac.services.iServices.IPermissionService;
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
public class PermissionService implements IPermissionService {

    private final IPermissionRepository permissionRepository;
    private final IResourceRepository resourceRepository;
    private final IPermissionMapper permissionMapper;

    public PermissionService(IPermissionRepository permissionRepository,
                              IResourceRepository resourceRepository,
                              IPermissionMapper permissionMapper) {
        this.permissionRepository = permissionRepository;
        this.resourceRepository = resourceRepository;
        this.permissionMapper = permissionMapper;
    }

    @Override
    public PermissionDTO createPermission(CreatePermissionRequest request) {
        if (permissionRepository.existsByPermissionCode(request.getPermissionCode())) {
            throw new DuplicateCodeException("Permission already exists with code: " + request.getPermissionCode());
        }
        Resource resource = resourceRepository.findById(request.getResourceId())
                .orElseThrow(() -> new EntityNotFoundException("Resource not found with id: " + request.getResourceId()));

        String caller = CurrentPrincipalContext.currentPrincipalCode();
        Permission permission = Permission.builder()
                .permissionCode(request.getPermissionCode())
                .permissionName(request.getPermissionName())
                .action(request.getAction())
                .resource(resource)
                .status(Status.ACTIVE)
                .createdBy(caller)
                .createdDate(LocalDateTime.now())
                .updatedBy(caller)
                .updatedDate(LocalDateTime.now())
                .build();

        Permission saved = permissionRepository.save(permission);
        log.info("Permission created: id={}, code={}", saved.getPermissionId(), saved.getPermissionCode());
        return permissionMapper.toDto(saved);
    }

    @Override
    @Cacheable(value = "permissions", key = "#id")
    public PermissionDTO getPermission(UUID id) {
        return permissionRepository.findById(id)
                .map(permissionMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Permission not found with id: " + id));
    }

    @Override
    public Page<PermissionDTO> getAllPermissions(Pageable pageable) {
        return permissionRepository.findAll(pageable).map(permissionMapper::toDto);
    }

    @Override
    @CachePut(value = "permissions", key = "#id")
    public PermissionDTO updatePermission(UUID id, UpdatePermissionRequest request) {
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Permission not found with id: " + id));

        if (request.getPermissionName() != null) permission.setPermissionName(request.getPermissionName());
        if (request.getAction() != null) permission.setAction(request.getAction());
        if (request.getResourceId() != null) {
            Resource resource = resourceRepository.findById(request.getResourceId())
                    .orElseThrow(() -> new EntityNotFoundException("Resource not found with id: " + request.getResourceId()));
            permission.setResource(resource);
        }
        permission.setUpdatedBy(CurrentPrincipalContext.currentPrincipalCode());
        permission.setUpdatedDate(LocalDateTime.now());

        return permissionMapper.toDto(permissionRepository.save(permission));
    }

    @Override
    @CacheEvict(value = "permissions", key = "#id")
    public void deletePermission(UUID id) {
        if (!permissionRepository.existsById(id)) {
            throw new EntityNotFoundException("Permission not found with id: " + id);
        }
        permissionRepository.deleteById(id);
    }
}
