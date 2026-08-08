package com.himaloyit.buildnation.cdm.wo.services.impl;

import com.himaloyit.buildnation.cdm.wo.domain.dto.InspectionDTO;
import com.himaloyit.buildnation.cdm.wo.domain.entities.Inspection;
import com.himaloyit.buildnation.cdm.wo.domain.entities.WorkOrder;
import com.himaloyit.buildnation.cdm.wo.domain.enums.InspectionStatus;
import com.himaloyit.buildnation.cdm.prj.domain.enums.ProjectStatus;
import com.himaloyit.buildnation.cdm.wo.domain.enums.WorkOrderStatus;
import com.himaloyit.buildnation.cdm.wo.domain.mapper.IInspectionMapper;
import com.himaloyit.buildnation.cdm.wo.domain.model.CreateInspectionRequest;
import com.himaloyit.buildnation.cdm.wo.domain.model.UpdateInspectionRequest;
import com.himaloyit.buildnation.cdm.wo.domain.model.UpdateInspectionStatusRequest;
import com.himaloyit.buildnation.cdm.prj.domain.model.UpdateProjectStatusRequest;
import com.himaloyit.buildnation.cdm.wo.domain.repositories.iRepositories.IInspectionRepository;
import com.himaloyit.buildnation.cdm.wo.domain.repositories.iRepositories.IWorkOrderRepository;
import com.himaloyit.buildnation.cdm.wo.services.iServices.IInspectionService;
import com.himaloyit.buildnation.cdm.prj.services.iServices.IProjectService;
import com.himaloyit.buildnation.cdm.common.exceptions.EntityNotFoundException;
import com.himaloyit.buildnation.cdm.common.exceptions.InvalidStateException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
public class InspectionService implements IInspectionService {

    private final IInspectionRepository iInspectionRepository;
    private final IWorkOrderRepository iWorkOrderRepository;
    private final IProjectService iProjectService;
    private final IInspectionMapper iInspectionMapper;

    public InspectionService(IInspectionRepository iInspectionRepository, IWorkOrderRepository iWorkOrderRepository,
                              IProjectService iProjectService, IInspectionMapper iInspectionMapper) {
        this.iInspectionRepository = iInspectionRepository;
        this.iWorkOrderRepository = iWorkOrderRepository;
        this.iProjectService = iProjectService;
        this.iInspectionMapper = iInspectionMapper;
    }

    @Override
    @Transactional
    public InspectionDTO createInspection(CreateInspectionRequest request) {
        log.info("Creating inspection: workOrderId={}", request.getWorkOrderId());
        WorkOrder workOrder = iWorkOrderRepository.findById(request.getWorkOrderId())
                .orElseThrow(() -> new EntityNotFoundException("WorkOrder not found with id: " + request.getWorkOrderId()));

        if (workOrder.getStatus() == WorkOrderStatus.COMPLETED || workOrder.getStatus() == WorkOrderStatus.CANCELLED) {
            throw new InvalidStateException("Cannot inspect a work order that is already " + workOrder.getStatus());
        }

        Inspection inspection = Inspection.builder()
                .workOrder(workOrder)
                .inspectorName(request.getInspectorName())
                .progressPercentage(request.getProgressPercentage())
                .quality(request.getQuality())
                .remarks(request.getRemarks())
                .status(InspectionStatus.PENDING)
                .inspectionDate(request.getInspectionDate())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Inspection saved = iInspectionRepository.save(inspection);

        workOrder.setStatus(WorkOrderStatus.INSPECTION);
        workOrder.setUpdatedAt(LocalDateTime.now());
        iWorkOrderRepository.save(workOrder);

        iProjectService.updateProjectStatus(workOrder.getProject().getId(),
                UpdateProjectStatusRequest.builder().status(ProjectStatus.INSPECTION).build());

        log.info("Inspection created: id={}", saved.getId());
        return iInspectionMapper.toDto(saved);
    }

    @Override
    public InspectionDTO getInspection(UUID id) {
        log.debug("Fetching inspection: id={}", id);
        return iInspectionRepository.findById(id)
                .map(iInspectionMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Inspection not found with id: " + id));
    }

    @Override
    public Page<InspectionDTO> getAllInspections(Pageable pageable) {
        return iInspectionRepository.findAll(pageable).map(iInspectionMapper::toDto);
    }

    @Override
    public Page<InspectionDTO> getInspectionsByWorkOrder(UUID workOrderId, Pageable pageable) {
        return iInspectionRepository.findByWorkOrderId(workOrderId, pageable).map(iInspectionMapper::toDto);
    }

    @Override
    public InspectionDTO updateInspection(UUID id, UpdateInspectionRequest request) {
        log.info("Updating inspection: id={}", id);
        Inspection inspection = iInspectionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Inspection not found with id: " + id));

        if (request.getProgressPercentage() != null) inspection.setProgressPercentage(request.getProgressPercentage());
        if (request.getQuality() != null) inspection.setQuality(request.getQuality());
        if (request.getRemarks() != null) inspection.setRemarks(request.getRemarks());
        inspection.setUpdatedAt(LocalDateTime.now());

        InspectionDTO updated = iInspectionMapper.toDto(iInspectionRepository.save(inspection));
        log.info("Inspection updated: id={}", id);
        return updated;
    }

    @Override
    public InspectionDTO updateInspectionStatus(UUID id, UpdateInspectionStatusRequest request) {
        log.info("Updating inspection status: id={}, status={}", id, request.getStatus());
        Inspection inspection = iInspectionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Inspection not found with id: " + id));

        inspection.setStatus(request.getStatus());
        inspection.setUpdatedAt(LocalDateTime.now());

        return iInspectionMapper.toDto(iInspectionRepository.save(inspection));
    }

    @Override
    public void deleteInspection(UUID id) {
        log.info("Deleting inspection: id={}", id);
        if (!iInspectionRepository.existsById(id)) {
            throw new EntityNotFoundException("Inspection not found with id: " + id);
        }
        iInspectionRepository.deleteById(id);
        log.info("Inspection deleted: id={}", id);
    }
}
