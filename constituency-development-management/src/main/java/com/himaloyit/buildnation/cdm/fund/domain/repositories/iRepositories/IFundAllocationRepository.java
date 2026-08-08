package com.himaloyit.buildnation.cdm.fund.domain.repositories.iRepositories;

import com.himaloyit.buildnation.cdm.fund.domain.entities.FundAllocation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface IFundAllocationRepository extends JpaRepository<FundAllocation, UUID> {

    Page<FundAllocation> findByFundId(UUID fundId, Pageable pageable);

    Page<FundAllocation> findByProjectId(UUID projectId, Pageable pageable);
}
