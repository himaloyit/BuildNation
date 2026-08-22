package com.himaloyit.buildnation.ui.view.contractor;

import com.himaloyit.buildnation.ui.client.contractor.ContractorClient;
import com.himaloyit.buildnation.ui.client.support.GatewayApiException;
import com.himaloyit.buildnation.ui.component.PaginationBar;
import com.himaloyit.buildnation.ui.dto.PageResponseDTO;
import com.himaloyit.buildnation.ui.dto.contractor.ContractorDTO;
import com.himaloyit.buildnation.ui.dto.contractor.ContractorStatus;
import com.himaloyit.buildnation.ui.dto.contractor.ContractorType;
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

/** List/Search/View/Add/Edit/Delete for Contractor, filterable by Type or Status. */
@Route(value = "contractors", layout = MainLayout.class)
@PageTitle("Contractors | BuildNation")
@PermitAll
public class ContractorListView extends VerticalLayout {

    private static final int PAGE_SIZE = 20;

    private final ContractorClient client;
    private final Grid<ContractorDTO> grid = new Grid<>(ContractorDTO.class, false);
    private final PaginationBar pagination = new PaginationBar(this::previousPage, this::nextPage);
    private final ComboBox<ContractorType> typeFilter = new ComboBox<>("Filter by Type");
    private final ComboBox<ContractorStatus> statusFilter = new ComboBox<>("Filter by Status");
    private int currentPage = 0;

    public ContractorListView(ContractorClient client) {
        this.client = client;
        setSizeFull();

        typeFilter.setItems(ContractorType.values());
        typeFilter.setClearButtonVisible(true);
        typeFilter.addValueChangeListener(e -> {
            if (e.getValue() != null) {
                statusFilter.clear();
            }
            currentPage = 0;
            refresh();
        });

        statusFilter.setItems(ContractorStatus.values());
        statusFilter.setClearButtonVisible(true);
        statusFilter.addValueChangeListener(e -> {
            if (e.getValue() != null) {
                typeFilter.clear();
            }
            currentPage = 0;
            refresh();
        });

        grid.addColumn(ContractorDTO::getName).setHeader("Name");
        grid.addColumn(c -> c.getType() == null ? "-" : c.getType().name()).setHeader("Type");
        grid.addColumn(c -> c.getStatus() == null ? "-" : c.getStatus().name()).setHeader("Status");
        grid.addColumn(ContractorDTO::getContactNumber).setHeader("Contact Number");
        grid.addColumn(ContractorDTO::getKeyPersonName).setHeader("Key Person");
        grid.addComponentColumn(this::actions).setHeader("Actions").setAutoWidth(true).setFlexGrow(0);
        grid.setSizeFull();

        Button add = new Button("Add Contractor", e -> openForm(null));
        add.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        HorizontalLayout toolbar = new HorizontalLayout(typeFilter, statusFilter, add);
        toolbar.setWidthFull();
        toolbar.setAlignItems(Alignment.END);
        toolbar.setJustifyContentMode(JustifyContentMode.BETWEEN);

        add(new H2("Contractors"), toolbar, grid, pagination);
        setFlexGrow(1, grid);

        refresh();
    }

    private HorizontalLayout actions(ContractorDTO contractor) {
        Button edit = new Button(new Icon(VaadinIcon.EDIT), e -> openForm(contractor));
        edit.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        Button changeStatus = new Button(new Icon(VaadinIcon.REFRESH), e -> openStatusDialog(contractor));
        changeStatus.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        Button delete = new Button(new Icon(VaadinIcon.TRASH), e -> confirmDelete(contractor));
        delete.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ERROR);
        return new HorizontalLayout(edit, changeStatus, delete);
    }

    private void openForm(ContractorDTO existing) {
        new ContractorFormDialog(client, existing, this::refresh).open();
    }

    private void openStatusDialog(ContractorDTO contractor) {
        new ContractorStatusChangeDialog(client, contractor, this::refresh).open();
    }

    private void confirmDelete(ContractorDTO contractor) {
        ConfirmDialog confirm = new ConfirmDialog();
        confirm.setHeader("Delete Contractor");
        confirm.setText("Delete \"" + contractor.getName() + "\"? This cannot be undone.");
        confirm.setCancelable(true);
        confirm.setConfirmText("Delete");
        confirm.setConfirmButtonTheme("error primary");
        confirm.addConfirmListener(e -> delete(contractor));
        confirm.open();
    }

    private void delete(ContractorDTO contractor) {
        try {
            client.delete(contractor.getId());
            Notification.show("Contractor deleted");
            refresh();
        } catch (GatewayApiException ex) {
            Notification errorNotification = Notification.show("Could not delete contractor: " + ex.getMessage(),
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
        ContractorType selectedType = typeFilter.getValue();
        ContractorStatus selectedStatus = statusFilter.getValue();
        PageResponseDTO<ContractorDTO> result;
        if (selectedType != null) {
            result = client.getByType(selectedType, currentPage, PAGE_SIZE);
        } else if (selectedStatus != null) {
            result = client.getByStatus(selectedStatus, currentPage, PAGE_SIZE);
        } else {
            result = client.getAll(currentPage, PAGE_SIZE);
        }
        grid.setItems(result.getContent());
        pagination.update(result.getNumber(), result.getTotalPages());
    }
}
