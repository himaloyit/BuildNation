package com.himaloyit.buildnation.cdm.services.impl;

import com.himaloyit.buildnation.cdm.domain.dto.FundAllocationDTO;
import com.himaloyit.buildnation.cdm.domain.entities.Fund;
import com.himaloyit.buildnation.cdm.domain.entities.FundAllocation;
import com.himaloyit.buildnation.cdm.domain.entities.Project;
import com.himaloyit.buildnation.cdm.domain.enums.ProjectStatus;
import com.himaloyit.buildnation.cdm.domain.mapper.IFundAllocationMapper;
import com.himaloyit.buildnation.cdm.domain.model.CreateFundAllocationRequest;
import com.himaloyit.buildnation.cdm.domain.model.UpdateProjectStatusRequest;
import com.himaloyit.buildnation.cdm.domain.repositories.iRepositories.IFundAllocationRepository;
import com.himaloyit.buildnation.cdm.domain.repositories.iRepositories.IFundRepository;
import com.himaloyit.buildnation.cdm.domain.repositories.iRepositories.IProjectRepository;
import com.himaloyit.buildnation.cdm.services.iServices.IFundAllocationService;
import com.himaloyit.buildnation.cdm.services.iServices.IProjectService;
import com.himaloyit.buildnation.cdm.util.exceptions.EntityNotFoundException;
import com.himaloyit.buildnation.cdm.util.exceptions.InsufficientFundBalanceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
public class FundAllocationService implements IFundAllocationService {

    private final IFundAllocationRepository iFundAllocationRepository;
    private final IFundRepository iFundRepository;
    private final IProjectRepository iProjectRepository;
    private final IProjectService iProjectService;
    private final IFundAllocationMapper iFundAllocationMapper;

    public FundAllocationService(IFundAllocationRepository iFundAllocationRepository, IFundRepository iFundRepository,
                                  IProjectRepository iProjectRepository, IProjectService iProjectService,
                                  IFundAllocationMapper iFundAllocationMapper) {
        this.iFundAllocationRepository = iFundAllocationRepository;
        this.iFundRepository = iFundRepository;
        this.iProjectRepository = iProjectRepository;
        this.iProjectService = iProjectService;
        this.iFundAllocationMapper = iFundAllocationMapper;
    }

    @Override
    @Transactional
    @CacheEvict(value = {"fund-allocations-list", "funds", "funds-list"}, allEntries = true)
    public FundAllocationDTO createAllocation(CreateFundAllocationRequest request) {
        log.info("Creating fund allocation: fundId={}, projectId={}, amount={}",
                request.getFundId(), request.getProjectId(), request.getAmount());
        Fund fund = iFundRepository.findById(request.getFundId())
                .orElseThrow(() -> new EntityNotFoundException("Fund not found with id: " + request.getFundId()));
        Project project = iProjectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new EntityNotFoundException("Project not found with id: " + request.getProjectId()));

        if (request.getAmount().compareTo(fund.getRemainingAmount()) > 0) {
            throw new InsufficientFundBalanceException(
                    "Requested amount " + request.getAmount() + " exceeds remaining fund balance " + fund.getRemainingAmount());
        }

        FundAllocation allocation = FundAllocation.builder()
                .fund(fund)
                .project(project)
                .amount(request.getAmount())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        FundAllocation saved = iFundAllocationRepository.save(allocation);

        fund.setAllocatedAmount(fund.getAllocatedAmount().add(request.getAmount()));
        fund.setRemainingAmount(fund.getRemainingAmount().subtract(request.getAmount()));
        fund.setUpdatedAt(LocalDateTime.now());
        iFundRepository.save(fund);

        iProjectService.updateProjectStatus(project.getId(),
                UpdateProjectStatusRequest.builder().status(ProjectStatus.ALLOCATED).build());

        log.info("Fund allocation created: id={}", saved.getId());
        return iFundAllocationMapper.toDto(saved);
    }

    @Override
    @Cacheable(value = "fund-allocations", key = "#id")
    public FundAllocationDTO getAllocation(UUID id) {
        log.debug("Fetching fund allocation: id={}", id);
        return iFundAllocationRepository.findById(id)
                .map(iFundAllocationMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("FundAllocation not found with id: " + id));
    }

    @Override
    @Cacheable(value = "fund-allocations-list", key = "#pageable.pageNumber + '-' + #pageable.pageSize")
    public Page<FundAllocationDTO> getAllAllocations(Pageable pageable) {
        return iFundAllocationRepository.findAll(pageable).map(iFundAllocationMapper::toDto);
    }

    @Override
    public Page<FundAllocationDTO> getAllocationsByFund(UUID fundId, Pageable pageable) {
        return iFundAllocationRepository.findByFundId(fundId, pageable).map(iFundAllocationMapper::toDto);
    }

    @Override
    public Page<FundAllocationDTO> getAllocationsByProject(UUID projectId, Pageable pageable) {
        return iFundAllocationRepository.findByProjectId(projectId, pageable).map(iFundAllocationMapper::toDto);
    }

    @Override
    @Transactional
    @Caching(evict = {
        @CacheEvict(value = "fund-allocations", key = "#id"),
        @CacheEvict(value = {"fund-allocations-list", "funds", "funds-list"}, allEntries = true)
    })
    public void deleteAllocation(UUID id) {
        log.info("Deleting fund allocation: id={}", id);
        FundAllocation allocation = iFundAllocationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("FundAllocation not found with id: " + id));

        Fund fund = allocation.getFund();
        fund.setAllocatedAmount(fund.getAllocatedAmount().subtract(allocation.getAmount()));
        fund.setRemainingAmount(fund.getRemainingAmount().add(allocation.getAmount()));
        fund.setUpdatedAt(LocalDateTime.now());
        iFundRepository.save(fund);

        iFundAllocationRepository.deleteById(id);
        log.info("Fund allocation deleted: id={}", id);
    }
}
