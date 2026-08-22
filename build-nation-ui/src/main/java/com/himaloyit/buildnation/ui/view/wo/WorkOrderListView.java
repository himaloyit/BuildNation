package com.himaloyit.buildnation.ui.view.wo;

import com.himaloyit.buildnation.ui.client.contractor.ContractorClient;
import com.himaloyit.buildnation.ui.client.fund.FundAllocationClient;
import com.himaloyit.buildnation.ui.client.prj.ProjectClient;
import com.himaloyit.buildnation.ui.client.support.GatewayApiException;
import com.himaloyit.buildnation.ui.client.wo.WorkOrderClient;
import com.himaloyit.buildnation.ui.component.PaginationBar;
import com.himaloyit.buildnation.ui.dto.PageResponseDTO;
import com.himaloyit.buildnation.ui.dto.contractor.ContractorDTO;
import com.himaloyit.buildnation.ui.dto.prj.ProjectDTO;
import com.himaloyit.buildnation.ui.dto.wo.WorkOrderDTO;
import com.himaloyit.buildnation.ui.view.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import jakarta.annotation.security.PermitAll;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/** List/Search/View/Add/Edit/Delete for WorkOrder, filterable by Project or Contractor. */
@Route(value = "work-orders", layout = MainLayout.class)
@PageTitle("Work Orders | BuildNation")
@PermitAll
public class WorkOrderListView extends VerticalLayout {

    private static final int PAGE_SIZE = 20;
    private static final int REFERENCE_DATA_PAGE_SIZE = 500;

    private final WorkOrderClient client;
    private final ProjectClient projectClient;
    private final ContractorClient contractorClient;
    private final FundAllocationClient fundAllocationClient;
    private final Grid<WorkOrderDTO> grid = new Grid<>(WorkOrderDTO.class, false);
    private final PaginationBar pagination = new PaginationBar(this::previousPage, this::nextPage);
    private final ComboBox<ProjectDTO> projectFilter = new ComboBox<>("Filter by Project");
    private final ComboBox<ContractorDTO> contractorFilter = new ComboBox<>("Filter by Contractor");
    private final Map<UUID, String> projectNamesById = new HashMap<>();
    private final Map<UUID, String> contractorNamesById = new HashMap<>();
    private int currentPage = 0;

    public WorkOrderListView(WorkOrderClient client, ProjectClient projectClient, ContractorClient contractorClient,
                              FundAllocationClient fundAllocationClient) {
        this.client = client;
        this.projectClient = projectClient;
        this.contractorClient = contractorClient;
        this.fundAllocationClient = fundAllocationClient;
        setSizeFull();

        List<ProjectDTO> projects = projectClient.getAll(0, REFERENCE_DATA_PAGE_SIZE).getContent();
        projects.forEach(p -> projectNamesById.put(p.getId(), p.getName()));
        List<ContractorDTO> contractors = contractorClient.getAll(0, REFERENCE_DATA_PAGE_SIZE).getContent();
        contractors.forEach(c -> contractorNamesById.put(c.getId(), c.getName()));

        projectFilter.setItemLabelGenerator(ProjectDTO::getName);
        projectFilter.setItems(projects);
        projectFilter.setClearButtonVisible(true);
        projectFilter.addValueChangeListener(e -> {
            if (e.getValue() != null) {
                contractorFilter.clear();
            }
            currentPage = 0;
            refresh();
        });

        contractorFilter.setItemLabelGenerator(ContractorDTO::getName);
        contractorFilter.setItems(contractors);
        contractorFilter.setClearButtonVisible(true);
        contractorFilter.addValueChangeListener(e -> {
            if (e.getValue() != null) {
                projectFilter.clear();
            }
            currentPage = 0;
            refresh();
        });

        grid.addColumn(WorkOrderDTO::getWorkOrderNumber).setHeader("Work Order #");
        grid.addColumn(w -> projectNamesById.getOrDefault(w.getProjectId(), "-")).setHeader("Project");
        grid.addColumn(w -> contractorNamesById.getOrDefault(w.getContractorId(), "-")).setHeader("Contractor");
        grid.addColumn(WorkOrderDTO::getAmount).setHeader("Amount");
        grid.addColumn(WorkOrderDTO::getStartDate).setHeader("Start Date");
        grid.addColumn(WorkOrderDTO::getEndDate).setHeader("End Date");
        grid.addColumn(w -> w.getStatus() == null ? "-" : w.getStatus().name()).setHeader("Status");
        grid.addComponentColumn(this::actions).setHeader("Actions").setAutoWidth(true).setFlexGrow(0);
        grid.setSizeFull();

        Button add = new Button("Add Work Order", e -> openForm(null));
        add.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        HorizontalLayout toolbar = new HorizontalLayout(projectFilter, contractorFilter, add);
        toolbar.setWidthFull();
        toolbar.setAlignItems(Alignment.END);
        toolbar.setJustifyContentMode(JustifyContentMode.BETWEEN);

        add(new H2("Work Orders"), toolbar, grid, pagination);
        setFlexGrow(1, grid);

        refresh();
    }

    private HorizontalLayout actions(WorkOrderDTO workOrder) {
        Button edit = new Button(new Icon(VaadinIcon.EDIT), e -> openForm(workOrder));
        edit.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        Button changeStatus = new Button(new Icon(VaadinIcon.REFRESH), e -> openStatusDialog(workOrder));
        changeStatus.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        Button delete = new Button(new Icon(VaadinIcon.TRASH), e -> confirmDelete(workOrder));
        delete.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ERROR);
        return new HorizontalLayout(edit, changeStatus, delete);
    }

    private void openForm(WorkOrderDTO existing) {
        new WorkOrderFormDialog(client, projectClient, contractorClient, fundAllocationClient, existing, this::refresh).open();
    }

    private void openStatusDialog(WorkOrderDTO workOrder) {
        new WorkOrderStatusChangeDialog(client, workOrder, this::refresh).open();
    }

    private void confirmDelete(WorkOrderDTO workOrder) {
        ConfirmDialog confirm = new ConfirmDialog();
        confirm.setHeader("Delete Work Order");
        confirm.setText("Delete \"" + workOrder.getWorkOrderNumber() + "\"? This cannot be undone.");
        confirm.setCancelable(true);
        confirm.setConfirmText("Delete");
        confirm.setConfirmButtonTheme("error primary");
        confirm.addConfirmListener(e -> delete(workOrder));
        confirm.open();
    }

    private void delete(WorkOrderDTO workOrder) {
        try {
            client.delete(workOrder.getId());
            Notification.show("Work order deleted");
            refresh();
        } catch (GatewayApiException ex) {
            Notification errorNotification = Notification.show("Could not delete work order: " + ex.getMessage(),
                    5000, Notification.Position.MIDDLE);
            errorNotification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void previousPage() {
        currentPage--;
        refresh();
    }

    private void nextPage() {
        currentPage++;
        refresh();
    }

    private void refresh() {
        ProjectDTO selectedProject = projectFilter.getValue();
        ContractorDTO selectedContractor = contractorFilter.getValue();
        PageResponseDTO<WorkOrderDTO> result;
        if (selectedProject != null) {
            result = client.getByProject(selectedProject.getId(), currentPage, PAGE_SIZE);
        } else if (selectedContractor != null) {
            result = client.getByContractor(selectedContractor.getId(), currentPage, PAGE_SIZE);
        } else {
            result = client.getAll(currentPage, PAGE_SIZE);
        }
        grid.setItems(result.getContent());
        pagination.update(result.getNumber(), result.getTotalPages());
    }
}
