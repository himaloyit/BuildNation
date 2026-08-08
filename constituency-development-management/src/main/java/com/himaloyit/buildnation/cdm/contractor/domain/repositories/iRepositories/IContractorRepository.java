package com.himaloyit.buildnation.cdm.contractor.domain.repositories.iRepositories;

import com.himaloyit.buildnation.cdm.contractor.domain.entities.Contractor;
import com.himaloyit.buildnation.cdm.contractor.domain.enums.ContractorStatus;
import com.himaloyit.buildnation.cdm.contractor.domain.enums.ContractorType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface IContractorRepository extends JpaRepository<Contractor, UUID> {

    Page<Contractor> findByType(ContractorType type, Pageable pageable);

    Page<Contractor> findByStatus(ContractorStatus status, Pageable pageable);
}
