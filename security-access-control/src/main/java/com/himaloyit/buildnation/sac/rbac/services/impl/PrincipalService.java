package com.himaloyit.buildnation.sac.rbac.services.impl;

import com.himaloyit.buildnation.sac.rbac.domain.dto.PrincipalDTO;
import com.himaloyit.buildnation.sac.rbac.domain.entities.Principal;
import com.himaloyit.buildnation.sac.rbac.domain.enums.PrincipalType;
import com.himaloyit.buildnation.sac.rbac.domain.enums.Status;
import com.himaloyit.buildnation.sac.rbac.domain.mapper.IPrincipalMapper;
import com.himaloyit.buildnation.sac.rbac.domain.model.CreatePrincipalRequest;
import com.himaloyit.buildnation.sac.rbac.domain.model.UpdatePrincipalRequest;
import com.himaloyit.buildnation.sac.rbac.domain.model.UpdatePrincipalStatusRequest;
import com.himaloyit.buildnation.sac.rbac.domain.repositories.iRepositories.IPrincipalRepository;
import com.himaloyit.buildnation.sac.rbac.security.CurrentPrincipalContext;
import com.himaloyit.buildnation.sac.rbac.services.iServices.IPrincipalService;
import com.himaloyit.buildnation.sac.rbac.util.exceptions.DuplicateCodeException;
import com.himaloyit.buildnation.sac.rbac.util.exceptions.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

/*
 * Author: Rajib Kumer Ghosh
 */

@Slf4j
@Service
public class PrincipalService implements IPrincipalService {

    private final IPrincipalRepository principalRepository;
    private final IPrincipalMapper principalMapper;
    private final PasswordEncoder passwordEncoder;

    public PrincipalService(IPrincipalRepository principalRepository,
                             IPrincipalMapper principalMapper,
                             PasswordEncoder passwordEncoder) {
        this.principalRepository = principalRepository;
        this.principalMapper = principalMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public PrincipalDTO createPrincipal(CreatePrincipalRequest request) {
        if (principalRepository.existsByPrincipalCode(request.getPrincipalCode())) {
            throw new DuplicateCodeException("Principal already exists with code: " + request.getPrincipalCode());
        }
        if (request.getEmail() != null && principalRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateCodeException("Principal already exists with email: " + request.getEmail());
        }

        String caller = CurrentPrincipalContext.currentPrincipalCode();
        Principal principal = Principal.builder()
                .principalCode(request.getPrincipalCode())
                .principalName(request.getPrincipalName())
                .principalType(request.getPrincipalType())
                .email(request.getEmail())
                .password(request.getPassword() != null ? passwordEncoder.encode(request.getPassword()) : null)
                .credentialType(request.getCredentialType())
                .enabled(true)
                .accountNonLocked(true)
                .expiresAt(request.getExpiresAt())
                .ownerContact(request.getOwnerContact())
                .description(request.getDescription())
                .status(Status.ACTIVE)
                .createdBy(caller)
                .createdDate(LocalDateTime.now())
                .updatedBy(caller)
                .updatedDate(LocalDateTime.now())
                .build();

        Principal saved = principalRepository.save(principal);
        log.info("Principal created: id={}, code={}, type={}", saved.getPrincipalId(), saved.getPrincipalCode(), saved.getPrincipalType());
        return principalMapper.toDto(saved);
    }

    @Override
    @Cacheable(value = "principals", key = "#id")
    public PrincipalDTO getPrincipal(UUID id) {
        return principalRepository.findById(id)
                .map(principalMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Principal not found with id: " + id));
    }

    @Override
    public PrincipalDTO getPrincipalByCode(String principalCode) {
        return principalRepository.findByPrincipalCode(principalCode)
                .map(principalMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Principal not found with code: " + principalCode));
    }

    @Override
    public Page<PrincipalDTO> getAllPrincipals(Pageable pageable) {
        return principalRepository.findAll(pageable).map(principalMapper::toDto);
    }

    @Override
    public Page<PrincipalDTO> getPrincipalsByType(PrincipalType type, Pageable pageable) {
        return principalRepository.findByPrincipalType(type, pageable).map(principalMapper::toDto);
    }

    @Override
    @CachePut(value = "principals", key = "#id")
    public PrincipalDTO updatePrincipal(UUID id, UpdatePrincipalRequest request) {
        Principal principal = principalRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Principal not found with id: " + id));

        if (request.getPrincipalName() != null) principal.setPrincipalName(request.getPrincipalName());
        if (request.getEmail() != null) principal.setEmail(request.getEmail());
        if (request.getEnabled() != null) principal.setEnabled(request.getEnabled());
        if (request.getAccountNonLocked() != null) principal.setAccountNonLocked(request.getAccountNonLocked());
        if (request.getExpiresAt() != null) principal.setExpiresAt(request.getExpiresAt());
        if (request.getOwnerContact() != null) principal.setOwnerContact(request.getOwnerContact());
        if (request.getDescription() != null) principal.setDescription(request.getDescription());
        principal.setUpdatedBy(CurrentPrincipalContext.currentPrincipalCode());
        principal.setUpdatedDate(LocalDateTime.now());

        return principalMapper.toDto(principalRepository.save(principal));
    }

    @Override
    @CachePut(value = "principals", key = "#id")
    public PrincipalDTO updatePrincipalStatus(UUID id, UpdatePrincipalStatusRequest request) {
        Principal principal = principalRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Principal not found with id: " + id));

        principal.setStatus(request.getStatus());
        principal.setUpdatedBy(CurrentPrincipalContext.currentPrincipalCode());
        principal.setUpdatedDate(LocalDateTime.now());

        return principalMapper.toDto(principalRepository.save(principal));
    }

    @Override
    @CacheEvict(value = "principals", key = "#id")
    public void deletePrincipal(UUID id) {
        if (!principalRepository.existsById(id)) {
            throw new EntityNotFoundException("Principal not found with id: " + id);
        }
        principalRepository.deleteById(id);
    }
}
