package com.himaloyit.buildnation.cdm.wo.domain.repositories.iRepositories;

import com.himaloyit.buildnation.cdm.wo.domain.entities.WorkOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface IWorkOrderRepository extends JpaRepository<WorkOrder, UUID> {

    Page<WorkOrder> findByProjectId(UUID projectId, Pageable pageable);

    Page<WorkOrder> findByContractorId(UUID contractorId, Pageable pageable);

    long countByWorkOrderNumberStartingWith(String prefix);
}
