package com.himaloyit.buildnation.cdm.wo.domain.repositories.iRepositories;

import com.himaloyit.buildnation.cdm.wo.domain.entities.Inspection;
import com.himaloyit.buildnation.cdm.wo.domain.enums.InspectionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface IInspectionRepository extends JpaRepository<Inspection, UUID> {

    Page<Inspection> findByWorkOrderId(UUID workOrderId, Pageable pageable);

    boolean existsByWorkOrderIdAndStatus(UUID workOrderId, InspectionStatus status);
}
