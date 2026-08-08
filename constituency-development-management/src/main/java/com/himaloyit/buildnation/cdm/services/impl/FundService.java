package com.himaloyit.buildnation.cdm.services.impl;

import com.himaloyit.buildnation.cdm.domain.dto.FundDTO;
import com.himaloyit.buildnation.cdm.domain.entities.Category;
import com.himaloyit.buildnation.cdm.domain.entities.Fund;
import com.himaloyit.buildnation.cdm.domain.entities.SubCategory;
import com.himaloyit.buildnation.cdm.domain.mapper.IFundMapper;
import com.himaloyit.buildnation.cdm.domain.model.CreateFundRequest;
import com.himaloyit.buildnation.cdm.domain.model.UpdateFundRequest;
import com.himaloyit.buildnation.cdm.domain.repositories.iRepositories.ICategoryRepository;
import com.himaloyit.buildnation.cdm.domain.repositories.iRepositories.IFundRepository;
import com.himaloyit.buildnation.cdm.domain.repositories.iRepositories.ISubCategoryRepository;
import com.himaloyit.buildnation.cdm.services.iServices.IFundService;
import com.himaloyit.buildnation.cdm.util.exceptions.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
public class FundService implements IFundService {

    private final IFundRepository iFundRepository;
    private final ICategoryRepository iCategoryRepository;
    private final ISubCategoryRepository iSubCategoryRepository;
    private final IFundMapper iFundMapper;

    public FundService(IFundRepository iFundRepository, ICategoryRepository iCategoryRepository,
                        ISubCategoryRepository iSubCategoryRepository, IFundMapper iFundMapper) {
        this.iFundRepository = iFundRepository;
        this.iCategoryRepository = iCategoryRepository;
        this.iSubCategoryRepository = iSubCategoryRepository;
        this.iFundMapper = iFundMapper;
    }

    @Override
    public FundDTO createFund(CreateFundRequest request) {
        log.info("Creating fund: month={}, fundType={}", request.getMonth(), request.getFundType());
        Category category = null;
        if (request.getCategoryId() != null) {
            category = iCategoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + request.getCategoryId()));
        }
        SubCategory subCategory = null;
        if (request.getSubCategoryId() != null) {
            subCategory = iSubCategoryRepository.findById(request.getSubCategoryId())
                    .orElseThrow(() -> new EntityNotFoundException("SubCategory not found with id: " + request.getSubCategoryId()));
        }

        Fund fund = Fund.builder()
                .month(request.getMonth().withDayOfMonth(1))
                .fundType(request.getFundType())
                .category(category)
                .subCategory(subCategory)
                .receivedAmount(request.getReceivedAmount())
                .allocatedAmount(BigDecimal.ZERO)
                .spentAmount(BigDecimal.ZERO)
                .remainingAmount(request.getReceivedAmount())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Fund saved = iFundRepository.save(fund);
        log.info("Fund created: id={}", saved.getId());
        return iFundMapper.toDto(saved);
    }

    @Override
    @Cacheable(value = "funds", key = "#id")
    public FundDTO getFund(UUID id) {
        log.debug("Fetching fund: id={}", id);
        return iFundRepository.findById(id)
                .map(iFundMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Fund not found with id: " + id));
    }

    @Override
    public Page<FundDTO> getAllFunds(Pageable pageable) {
        return iFundRepository.findAll(pageable).map(iFundMapper::toDto);
    }

    @Override
    public Page<FundDTO> getFundsByCategory(UUID categoryId, Pageable pageable) {
        return iFundRepository.findByCategoryId(categoryId, pageable).map(iFundMapper::toDto);
    }

    @Override
    @CachePut(value = "funds", key = "#id")
    public FundDTO updateFund(UUID id, UpdateFundRequest request) {
        log.info("Updating fund: id={}", id);
        Fund fund = iFundRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Fund not found with id: " + id));

        if (request.getMonth() != null) fund.setMonth(request.getMonth().withDayOfMonth(1));
        if (request.getFundType() != null) fund.setFundType(request.getFundType());
        if (request.getReceivedAmount() != null) {
            fund.setReceivedAmount(request.getReceivedAmount());
            fund.setRemainingAmount(request.getReceivedAmount().subtract(fund.getAllocatedAmount()));
        }
        if (request.getCategoryId() != null) {
            Category category = iCategoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + request.getCategoryId()));
            fund.setCategory(category);
        }
        if (request.getSubCategoryId() != null) {
            SubCategory subCategory = iSubCategoryRepository.findById(request.getSubCategoryId())
                    .orElseThrow(() -> new EntityNotFoundException("SubCategory not found with id: " + request.getSubCategoryId()));
            fund.setSubCategory(subCategory);
        }
        fund.setUpdatedAt(LocalDateTime.now());

        FundDTO updated = iFundMapper.toDto(iFundRepository.save(fund));
        log.info("Fund updated: id={}", id);
        return updated;
    }

    @Override
    @CacheEvict(value = "funds", key = "#id")
    public void deleteFund(UUID id) {
        log.info("Deleting fund: id={}", id);
        if (!iFundRepository.existsById(id)) {
            throw new EntityNotFoundException("Fund not found with id: " + id);
        }
        iFundRepository.deleteById(id);
        log.info("Fund deleted: id={}", id);
    }
}
