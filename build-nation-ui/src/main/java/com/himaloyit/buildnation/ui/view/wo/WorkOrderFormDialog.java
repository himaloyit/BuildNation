package com.himaloyit.buildnation.ui.view.wo;

import com.himaloyit.buildnation.ui.client.contractor.ContractorClient;
import com.himaloyit.buildnation.ui.client.fund.FundAllocationClient;
import com.himaloyit.buildnation.ui.client.prj.ProjectClient;
import com.himaloyit.buildnation.ui.client.support.GatewayApiException;
import com.himaloyit.buildnation.ui.client.wo.WorkOrderClient;
import com.himaloyit.buildnation.ui.dto.contractor.ContractorDTO;
import com.himaloyit.buildnation.ui.dto.fund.FundAllocationDTO;
import com.himaloyit.buildnation.ui.dto.prj.ProjectDTO;
import com.himaloyit.buildnation.ui.dto.wo.CreateWorkOrderRequest;
import com.himaloyit.buildnation.ui.dto.wo.UpdateWorkOrderRequest;
import com.himaloyit.buildnation.ui.dto.wo.WorkOrderDTO;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.data.binder.Binder;

import java.util.List;

/**
 * Add/Edit dialog for a WorkOrder. Project/Contractor/FundAllocation can only be chosen at creation
 * (the backend's UpdateWorkOrderRequest has no FK fields — only amount/startDate/endDate), so those
 * three ComboBoxes are read-only when editing.
 */
public class WorkOrderFormDialog extends Dialog {

    private static final int REFERENCE_DATA_PAGE_SIZE = 500;

    private final Binder<WorkOrderDTO> binder = new Binder<>(WorkOrderDTO.class);
    private final List<ProjectDTO> projects;
    private final List<ContractorDTO> contractors;
    private final List<FundAllocationDTO> fundAllocations;

    public WorkOrderFormDialog(WorkOrderClient client, ProjectClient projectClient, ContractorClient contractorClient,
                                FundAllocationClient fundAllocationClient, WorkOrderDTO existing, Runnable onSaved) {
        setHeaderTitle(existing == null ? "Add Work Order" : "Edit Work Order");
        this.projects = projectClient.getAll(0, REFERENCE_DATA_PAGE_SIZE).getContent();
        this.contractors = contractorClient.getAll(0, REFERENCE_DATA_PAGE_SIZE).getContent();
        this.fundAllocations = fundAllocationClient.getAll(0, REFERENCE_DATA_PAGE_SIZE).getContent();

        ComboBox<ProjectDTO> project = new ComboBox<>("Project");
        project.setItemLabelGenerator(ProjectDTO::getName);
        project.setItems(projects);

        ComboBox<ContractorDTO> contractor = new ComboBox<>("Contractor");
        contractor.setItemLabelGenerator(ContractorDTO::getName);
        contractor.setItems(contractors);

        ComboBox<FundAllocationDTO> fundAllocation = new ComboBox<>("Fund Allocation");
        fundAllocation.setItemLabelGenerator(fa -> "Allocation " + fa.getId().toString().substring(0, 8) + " ($" + fa.getAmount() + ")");
        fundAllocation.setItems(fundAllocations);

        BigDecimalField amount = new BigDecimalField("Amount");
        DatePicker startDate = new DatePicker("Start Date");
        DatePicker endDate = new DatePicker("End Date");

        binder.forField(project).asRequired("Project is mandatory")
                .bind(this::findProjectOf, (dto, selected) -> dto.setProjectId(selected == null ? null : selected.getId()));
        binder.forField(contractor).asRequired("Contractor is mandatory")
                .bind(this::findContractorOf, (dto, selected) -> dto.setContractorId(selected == null ? null : selected.getId()));
        binder.forField(fundAllocation).asRequired("Fund allocation is mandatory")
                .bind(this::findFundAllocationOf, (dto, selected) -> dto.setFundAllocationId(selected == null ? null : selected.getId()));
        binder.forField(amount).asRequired("Amount is mandatory").bind(WorkOrderDTO::getAmount, WorkOrderDTO::setAmount);
        binder.forField(startDate).asRequired("Start date is mandatory").bind(WorkOrderDTO::getStartDate, WorkOrderDTO::setStartDate);
        binder.forField(endDate).asRequired("End date is mandatory").bind(WorkOrderDTO::getEndDate, WorkOrderDTO::setEndDate);

        WorkOrderDTO working = new WorkOrderDTO();
        if (existing != null) {
            working.setId(existing.getId());
            working.setWorkOrderNumber(existing.getWorkOrderNumber());
            working.setProjectId(existing.getProjectId());
            working.setContractorId(existing.getContractorId());
            working.setFundAllocationId(existing.getFundAllocationId());
            working.setAmount(existing.getAmount());
            working.setStartDate(existing.getStartDate());
            working.setEndDate(existing.getEndDate());
            working.setStatus(existing.getStatus());

            project.setReadOnly(true);
            contractor.setReadOnly(true);
            fundAllocation.setReadOnly(true);
        }
        binder.readBean(working);

        FormLayout form = new FormLayout(project, contractor, fundAllocation, amount, startDate, endDate);
        add(form);

        Button cancel = new Button("Cancel", e -> close());
        Button save = new Button("Save", e -> save(client, existing, working, onSaved));
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        getFooter().add(cancel, save);
    }

    private ProjectDTO findProjectOf(WorkOrderDTO dto) {
        if (dto.getProjectId() == null) {
            return null;
        }
        return projects.stream().filter(p -> p.getId().equals(dto.getProjectId())).findFirst().orElse(null);
    }

    private ContractorDTO findContractorOf(WorkOrderDTO dto) {
        if (dto.getContractorId() == null) {
            return null;
        }
        return contractors.stream().filter(c -> c.getId().equals(dto.getContractorId())).findFirst().orElse(null);
    }

    private FundAllocationDTO findFundAllocationOf(WorkOrderDTO dto) {
        if (dto.getFundAllocationId() == null) {
            return null;
        }
        return fundAllocations.stream().filter(fa -> fa.getId().equals(dto.getFundAllocationId())).findFirst().orElse(null);
    }

    private void save(WorkOrderClient client, WorkOrderDTO existing, WorkOrderDTO working, Runnable onSaved) {
        if (!binder.writeBeanIfValid(working)) {
            return;
        }
        try {
            if (existing == null) {
                client.create(new CreateWorkOrderRequest(working.getProjectId(), working.getContractorId(),
                        working.getFundAllocationId(), working.getAmount(), working.getStartDate(), working.getEndDate()));
            } else {
                client.update(existing.getId(), new UpdateWorkOrderRequest(working.getAmount(), working.getStartDate(),
                        working.getEndDate()));
            }
            Notification.show("Work order saved");
            onSaved.run();
            close();
        } catch (GatewayApiException ex) {
            Notification errorNotification = Notification.show("Could not save work order: " + ex.getMessage(),
                    5000, Notification.Position.MIDDLE);
            errorNotification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }
}
