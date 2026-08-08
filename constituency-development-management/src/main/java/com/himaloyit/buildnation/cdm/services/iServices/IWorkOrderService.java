package com.himaloyit.buildnation.cdm.services.iServices;

import com.himaloyit.buildnation.cdm.domain.dto.WorkOrderDTO;
import com.himaloyit.buildnation.cdm.domain.model.CreateWorkOrderRequest;
import com.himaloyit.buildnation.cdm.domain.model.UpdateWorkOrderRequest;
import com.himaloyit.buildnation.cdm.domain.model.UpdateWorkOrderStatusRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface IWorkOrderService {

    WorkOrderDTO createWorkOrder(CreateWorkOrderRequest request);
    WorkOrderDTO getWorkOrder(UUID id);
    Page<WorkOrderDTO> getAllWorkOrders(Pageable pageable);
    Page<WorkOrderDTO> getWorkOrdersByProject(UUID projectId, Pageable pageable);
    Page<WorkOrderDTO> getWorkOrdersByContractor(UUID contractorId, Pageable pageable);
    WorkOrderDTO updateWorkOrder(UUID id, UpdateWorkOrderRequest request);
    WorkOrderDTO updateWorkOrderStatus(UUID id, UpdateWorkOrderStatusRequest request);
    void deleteWorkOrder(UUID id);
}
