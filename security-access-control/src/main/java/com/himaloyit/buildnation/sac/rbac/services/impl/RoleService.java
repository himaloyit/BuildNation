package com.himaloyit.buildnation.sac.rbac.services.impl;

import com.himaloyit.buildnation.sac.rbac.domain.dto.RoleDTO;
import com.himaloyit.buildnation.sac.rbac.domain.entities.Role;
import com.himaloyit.buildnation.sac.rbac.domain.enums.Status;
import com.himaloyit.buildnation.sac.rbac.domain.mapper.IRoleMapper;
import com.himaloyit.buildnation.sac.rbac.domain.model.CreateRoleRequest;
import com.himaloyit.buildnation.sac.rbac.domain.model.UpdateRoleRequest;
import com.himaloyit.buildnation.sac.rbac.domain.repositories.iRepositories.IRoleRepository;
import com.himaloyit.buildnation.sac.rbac.security.CurrentPrincipalContext;
import com.himaloyit.buildnation.sac.rbac.services.iServices.IRoleService;
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
public class RoleService implements IRoleService {

    private final IRoleRepository roleRepository;
    private final IRoleMapper roleMapper;

    public RoleService(IRoleRepository roleRepository, IRoleMapper roleMapper) {
        this.roleRepository = roleRepository;
        this.roleMapper = roleMapper;
    }

    @Override
    public RoleDTO createRole(CreateRoleRequest request) {
        if (roleRepository.existsByRoleCode(request.getRoleCode())) {
            throw new DuplicateCodeException("Role already exists with code: " + request.getRoleCode());
        }

        String caller = CurrentPrincipalContext.currentPrincipalCode();
        Role role = Role.builder()
                .roleCode(request.getRoleCode())
                .roleName(request.getRoleName())
                .description(request.getDescription())
                .status(Status.ACTIVE)
                .createdBy(caller)
                .createdDate(LocalDateTime.now())
                .updatedBy(caller)
                .updatedDate(LocalDateTime.now())
                .build();

        Role saved = roleRepository.save(role);
        log.info("Role created: id={}, code={}", saved.getRoleId(), saved.getRoleCode());
        return roleMapper.toDto(saved);
    }

    @Override
    @Cacheable(value = "roles", key = "#id")
    public RoleDTO getRole(UUID id) {
        return roleRepository.findById(id)
                .map(roleMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Role not found with id: " + id));
    }

    @Override
    public RoleDTO getRoleByCode(String roleCode) {
        return roleRepository.findByRoleCode(roleCode)
                .map(roleMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Role not found with code: " + roleCode));
    }

    @Override
    public Page<RoleDTO> getAllRoles(Pageable pageable) {
        return roleRepository.findAll(pageable).map(roleMapper::toDto);
    }

    @Override
    @CachePut(value = "roles", key = "#id")
    public RoleDTO updateRole(UUID id, UpdateRoleRequest request) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Role not found with id: " + id));

        if (request.getRoleName() != null) role.setRoleName(request.getRoleName());
        if (request.getDescription() != null) role.setDescription(request.getDescription());
        role.setUpdatedBy(CurrentPrincipalContext.currentPrincipalCode());
        role.setUpdatedDate(LocalDateTime.now());

        return roleMapper.toDto(roleRepository.save(role));
    }

    @Override
    @CacheEvict(value = "roles", key = "#id")
    public void deleteRole(UUID id) {
        if (!roleRepository.existsById(id)) {
            throw new EntityNotFoundException("Role not found with id: " + id);
        }
        roleRepository.deleteById(id);
    }
}
