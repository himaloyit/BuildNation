package com.himaloyit.buildnation.cdm.contractor.services.impl;

import com.himaloyit.buildnation.cdm.contractor.domain.dto.ContractorDTO;
import com.himaloyit.buildnation.cdm.contractor.domain.entities.Contractor;
import com.himaloyit.buildnation.cdm.contractor.domain.enums.ContractorStatus;
import com.himaloyit.buildnation.cdm.contractor.domain.enums.ContractorType;
import com.himaloyit.buildnation.cdm.contractor.domain.mapper.IContractorMapper;
import com.himaloyit.buildnation.cdm.contractor.domain.model.CreateContractorRequest;
import com.himaloyit.buildnation.cdm.contractor.domain.model.UpdateContractorRequest;
import com.himaloyit.buildnation.cdm.contractor.domain.model.UpdateContractorStatusRequest;
import com.himaloyit.buildnation.cdm.contractor.domain.repositories.iRepositories.IContractorRepository;
import com.himaloyit.buildnation.cdm.contractor.services.iServices.IContractorService;
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
public class ContractorService implements IContractorService {

    private final IContractorRepository iContractorRepository;
    private final IContractorMapper iContractorMapper;

    public ContractorService(IContractorRepository iContractorRepository, IContractorMapper iContractorMapper) {
        this.iContractorRepository = iContractorRepository;
        this.iContractorMapper = iContractorMapper;
    }

    @Override
    public ContractorDTO createContractor(CreateContractorRequest request) {
        log.info("Creating contractor: name={}, type={}", request.getName(), request.getType());
        Contractor contractor = Contractor.builder()
                .name(request.getName())
                .type(request.getType())
                .status(ContractorStatus.ACTIVE)
                .contactNumber(request.getContactNumber())
                .address(request.getAddress())
                .license(request.getLicense())
                .keyPersonName(request.getKeyPersonName())
                .keyPersonContact(request.getKeyPersonContact())
                .bankName(request.getBankName())
                .bankAccountNumber(request.getBankAccountNumber())
                .bankBranch(request.getBankBranch())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Contractor saved = iContractorRepository.save(contractor);
        log.info("Contractor created: id={}", saved.getId());
        return iContractorMapper.toDto(saved);
    }

    @Override
    @Cacheable(value = "contractors", key = "#id")
    public ContractorDTO getContractor(UUID id) {
        log.debug("Fetching contractor: id={}", id);
        return iContractorRepository.findById(id)
                .map(iContractorMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Contractor not found with id: " + id));
    }

    @Override
    public Page<ContractorDTO> getAllContractors(Pageable pageable) {
        return iContractorRepository.findAll(pageable).map(iContractorMapper::toDto);
    }

    @Override
    public Page<ContractorDTO> getContractorsByType(ContractorType type, Pageable pageable) {
        return iContractorRepository.findByType(type, pageable).map(iContractorMapper::toDto);
    }

    @Override
    public Page<ContractorDTO> getContractorsByStatus(ContractorStatus status, Pageable pageable) {
        return iContractorRepository.findByStatus(status, pageable).map(iContractorMapper::toDto);
    }

    @Override
    @CachePut(value = "contractors", key = "#id")
    public ContractorDTO updateContractor(UUID id, UpdateContractorRequest request) {
        log.info("Updating contractor: id={}", id);
        Contractor contractor = iContractorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Contractor not found with id: " + id));

        if (request.getName() != null) contractor.setName(request.getName());
        if (request.getType() != null) contractor.setType(request.getType());
        if (request.getContactNumber() != null) contractor.setContactNumber(request.getContactNumber());
        if (request.getAddress() != null) contractor.setAddress(request.getAddress());
        if (request.getLicense() != null) contractor.setLicense(request.getLicense());
        if (request.getKeyPersonName() != null) contractor.setKeyPersonName(request.getKeyPersonName());
        if (request.getKeyPersonContact() != null) contractor.setKeyPersonContact(request.getKeyPersonContact());
        if (request.getBankName() != null) contractor.setBankName(request.getBankName());
        if (request.getBankAccountNumber() != null) contractor.setBankAccountNumber(request.getBankAccountNumber());
        if (request.getBankBranch() != null) contractor.setBankBranch(request.getBankBranch());
        contractor.setUpdatedAt(LocalDateTime.now());

        ContractorDTO updated = iContractorMapper.toDto(iContractorRepository.save(contractor));
        log.info("Contractor updated: id={}", id);
        return updated;
    }

    @Override
    @CachePut(value = "contractors", key = "#id")
    public ContractorDTO updateContractorStatus(UUID id, UpdateContractorStatusRequest request) {
        log.info("Updating contractor status: id={}, status={}", id, request.getStatus());
        Contractor contractor = iContractorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Contractor not found with id: " + id));

        contractor.setStatus(request.getStatus());
        contractor.setUpdatedAt(LocalDateTime.now());

        return iContractorMapper.toDto(iContractorRepository.save(contractor));
    }

    @Override
    @CacheEvict(value = "contractors", key = "#id")
    public void deleteContractor(UUID id) {
        log.info("Deleting contractor: id={}", id);
        if (!iContractorRepository.existsById(id)) {
            throw new EntityNotFoundException("Contractor not found with id: " + id);
        }
        iContractorRepository.deleteById(id);
        log.info("Contractor deleted: id={}", id);
    }
}
