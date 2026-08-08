package com.himaloyit.buildnation.cdm.services.impl;

import com.himaloyit.buildnation.cdm.domain.dto.WorkOrderDTO;
import com.himaloyit.buildnation.cdm.domain.entities.Contractor;
import com.himaloyit.buildnation.cdm.domain.entities.FundAllocation;
import com.himaloyit.buildnation.cdm.domain.entities.Project;
import com.himaloyit.buildnation.cdm.domain.entities.WorkOrder;
import com.himaloyit.buildnation.cdm.domain.enums.ContractorStatus;
import com.himaloyit.buildnation.cdm.domain.enums.ProjectStatus;
import com.himaloyit.buildnation.cdm.domain.enums.WorkOrderStatus;
import com.himaloyit.buildnation.cdm.domain.mapper.IWorkOrderMapper;
import com.himaloyit.buildnation.cdm.domain.model.CreateWorkOrderRequest;
import com.himaloyit.buildnation.cdm.domain.model.UpdateProjectStatusRequest;
import com.himaloyit.buildnation.cdm.domain.model.UpdateWorkOrderRequest;
import com.himaloyit.buildnation.cdm.domain.model.UpdateWorkOrderStatusRequest;
import com.himaloyit.buildnation.cdm.domain.repositories.iRepositories.IContractorRepository;
import com.himaloyit.buildnation.cdm.domain.repositories.iRepositories.IFundAllocationRepository;
import com.himaloyit.buildnation.cdm.domain.repositories.iRepositories.IProjectRepository;
import com.himaloyit.buildnation.cdm.domain.repositories.iRepositories.IWorkOrderRepository;
import com.himaloyit.buildnation.cdm.services.iServices.IProjectService;
import com.himaloyit.buildnation.cdm.services.iServices.IWorkOrderService;
import com.himaloyit.buildnation.cdm.util.exceptions.EntityNotFoundException;
import com.himaloyit.buildnation.cdm.util.exceptions.InvalidStateException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
public class WorkOrderService implements IWorkOrderService {

    private final IWorkOrderRepository iWorkOrderRepository;
    private final IProjectRepository iProjectRepository;
    private final IContractorRepository iContractorRepository;
    private final IFundAllocationRepository iFundAllocationRepository;
    private final IProjectService iProjectService;
    private final IWorkOrderMapper iWorkOrderMapper;

    public WorkOrderService(IWorkOrderRepository iWorkOrderRepository, IProjectRepository iProjectRepository,
                             IContractorRepository iContractorRepository, IFundAllocationRepository iFundAllocationRepository,
                             IProjectService iProjectService, IWorkOrderMapper iWorkOrderMapper) {
        this.iWorkOrderRepository = iWorkOrderRepository;
        this.iProjectRepository = iProjectRepository;
        this.iContractorRepository = iContractorRepository;
        this.iFundAllocationRepository = iFundAllocationRepository;
        this.iProjectService = iProjectService;
        this.iWorkOrderMapper = iWorkOrderMapper;
    }

    @Override
    @Transactional
    public WorkOrderDTO createWorkOrder(CreateWorkOrderRequest request) {
        log.info("Creating work order: projectId={}, contractorId={}", request.getProjectId(), request.getContractorId());
        Project project = iProjectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new EntityNotFoundException("Project not found with id: " + request.getProjectId()));
        Contractor contractor = iContractorRepository.findById(request.getContractorId())
                .orElseThrow(() -> new EntityNotFoundException("Contractor not found with id: " + request.getContractorId()));
        FundAllocation fundAllocation = iFundAllocationRepository.findById(request.getFundAllocationId())
                .orElseThrow(() -> new EntityNotFoundException("Fund allocation not found with id: " + request.getFundAllocationId()));

        if (project.getStatus() != ProjectStatus.ALLOCATED) {
            throw new InvalidStateException("Project must be ALLOCATED before a work order can be issued (current status: " + project.getStatus() + ")");
        }
        if (contractor.getStatus() == ContractorStatus.BLACKLISTED) {
            throw new InvalidStateException("Contractor is blacklisted and cannot be issued a work order");
        }
        if (!fundAllocation.getProject().getId().equals(project.getId())) {
            throw new InvalidStateException("Fund allocation does not belong to the specified project");
        }
        if (request.getAmount().compareTo(fundAllocation.getAmount()) > 0) {
            throw new InvalidStateException("Work order amount " + request.getAmount() + " exceeds the fund allocation amount " + fundAllocation.getAmount());
        }

        WorkOrder workOrder = WorkOrder.builder()
                .workOrderNumber(generateWorkOrderNumber())
                .project(project)
                .contractor(contractor)
                .fundAllocation(fundAllocation)
                .amount(request.getAmount())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .status(WorkOrderStatus.IN_PROGRESS)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        WorkOrder saved = iWorkOrderRepository.save(workOrder);

        iProjectService.updateProjectStatus(project.getId(),
                UpdateProjectStatusRequest.builder().status(ProjectStatus.RUNNING).build());

        log.info("Work order created: id={}, number={}", saved.getId(), saved.getWorkOrderNumber());
        return iWorkOrderMapper.toDto(saved);
    }

    private String generateWorkOrderNumber() {
        int year = LocalDate.now().getYear();
        long countThisYear = iWorkOrderRepository.countByWorkOrderNumberStartingWith("WO-" + year + "-");
        return String.format("WO-%d-%04d", year, countThisYear + 1);
    }

    @Override
    public WorkOrderDTO getWorkOrder(UUID id) {
        log.debug("Fetching work order: id={}", id);
        return iWorkOrderRepository.findById(id)
                .map(iWorkOrderMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("WorkOrder not found with id: " + id));
    }

    @Override
    public Page<WorkOrderDTO> getAllWorkOrders(Pageable pageable) {
        return iWorkOrderRepository.findAll(pageable).map(iWorkOrderMapper::toDto);
    }

    @Override
    public Page<WorkOrderDTO> getWorkOrdersByProject(UUID projectId, Pageable pageable) {
        return iWorkOrderRepository.findByProjectId(projectId, pageable).map(iWorkOrderMapper::toDto);
    }

    @Override
    public Page<WorkOrderDTO> getWorkOrdersByContractor(UUID contractorId, Pageable pageable) {
        return iWorkOrderRepository.findByContractorId(contractorId, pageable).map(iWorkOrderMapper::toDto);
    }

    @Override
    public WorkOrderDTO updateWorkOrder(UUID id, UpdateWorkOrderRequest request) {
        log.info("Updating work order: id={}", id);
        WorkOrder workOrder = iWorkOrderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("WorkOrder not found with id: " + id));

        if (request.getAmount() != null) workOrder.setAmount(request.getAmount());
        if (request.getStartDate() != null) workOrder.setStartDate(request.getStartDate());
        if (request.getEndDate() != null) workOrder.setEndDate(request.getEndDate());
        workOrder.setUpdatedAt(LocalDateTime.now());

        WorkOrderDTO updated = iWorkOrderMapper.toDto(iWorkOrderRepository.save(workOrder));
        log.info("Work order updated: id={}", id);
        return updated;
    }

    @Override
    public WorkOrderDTO updateWorkOrderStatus(UUID id, UpdateWorkOrderStatusRequest request) {
        log.info("Updating work order status: id={}, status={}", id, request.getStatus());
        WorkOrder workOrder = iWorkOrderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("WorkOrder not found with id: " + id));

        workOrder.setStatus(request.getStatus());
        workOrder.setUpdatedAt(LocalDateTime.now());

        return iWorkOrderMapper.toDto(iWorkOrderRepository.save(workOrder));
    }

    @Override
    public void deleteWorkOrder(UUID id) {
        log.info("Deleting work order: id={}", id);
        if (!iWorkOrderRepository.existsById(id)) {
            throw new EntityNotFoundException("WorkOrder not found with id: " + id);
        }
        iWorkOrderRepository.deleteById(id);
        log.info("Work order deleted: id={}", id);
    }
}
