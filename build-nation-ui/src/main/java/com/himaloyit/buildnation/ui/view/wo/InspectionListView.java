package com.himaloyit.buildnation.ui.view.wo;

import com.himaloyit.buildnation.ui.client.support.GatewayApiException;
import com.himaloyit.buildnation.ui.client.wo.InspectionClient;
import com.himaloyit.buildnation.ui.client.wo.WorkOrderClient;
import com.himaloyit.buildnation.ui.component.PaginationBar;
import com.himaloyit.buildnation.ui.dto.PageResponseDTO;
import com.himaloyit.buildnation.ui.dto.wo.InspectionDTO;
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

/** List/Search/View/Add/Edit/Delete for Inspection, filterable by Work Order. */
@Route(value = "inspections", layout = MainLayout.class)
@PageTitle("Inspections | BuildNation")
@PermitAll
public class InspectionListView extends VerticalLayout {

    private static final int PAGE_SIZE = 20;
    private static final int REFERENCE_DATA_PAGE_SIZE = 500;

    private final InspectionClient client;
    private final WorkOrderClient workOrderClient;
    private final Grid<InspectionDTO> grid = new Grid<>(InspectionDTO.class, false);
    private final PaginationBar pagination = new PaginationBar(this::previousPage, this::nextPage);
    private final ComboBox<WorkOrderDTO> workOrderFilter = new ComboBox<>("Filter by Work Order");
    private final Map<UUID, String> workOrderNumbersById = new HashMap<>();
    private int currentPage = 0;

    public InspectionListView(InspectionClient client, WorkOrderClient workOrderClient) {
        this.client = client;
        this.workOrderClient = workOrderClient;
        setSizeFull();

        List<WorkOrderDTO> workOrders = workOrderClient.getAll(0, REFERENCE_DATA_PAGE_SIZE).getContent();
        workOrders.forEach(w -> workOrderNumbersById.put(w.getId(), w.getWorkOrderNumber()));

        workOrderFilter.setItemLabelGenerator(WorkOrderDTO::getWorkOrderNumber);
        workOrderFilter.setItems(workOrders);
        workOrderFilter.setClearButtonVisible(true);
        workOrderFilter.addValueChangeListener(e -> {
            currentPage = 0;
            refresh();
        });

        grid.addColumn(i -> workOrderNumbersById.getOrDefault(i.getWorkOrderId(), "-")).setHeader("Work Order");
        grid.addColumn(InspectionDTO::getInspectorName).setHeader("Inspector");
        grid.addColumn(InspectionDTO::getProgressPercentage).setHeader("Progress %");
        grid.addColumn(InspectionDTO::getQuality).setHeader("Quality");
        grid.addColumn(i -> i.getStatus() == null ? "-" : i.getStatus().name()).setHeader("Status");
        grid.addColumn(InspectionDTO::getInspectionDate).setHeader("Inspection Date");
        grid.addComponentColumn(this::actions).setHeader("Actions").setAutoWidth(true).setFlexGrow(0);
        grid.setSizeFull();

        Button add = new Button("Add Inspection", e -> openForm(null));
        add.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        HorizontalLayout toolbar = new HorizontalLayout(workOrderFilter, add);
        toolbar.setWidthFull();
        toolbar.setAlignItems(Alignment.END);
        toolbar.setJustifyContentMode(JustifyContentMode.BETWEEN);

        add(new H2("Inspections"), toolbar, grid, pagination);
        setFlexGrow(1, grid);

        refresh();
    }

    private HorizontalLayout actions(InspectionDTO inspection) {
        Button edit = new Button(new Icon(VaadinIcon.EDIT), e -> openForm(inspection));
        edit.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        Button changeStatus = new Button(new Icon(VaadinIcon.REFRESH), e -> openStatusDialog(inspection));
        changeStatus.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        Button delete = new Button(new Icon(VaadinIcon.TRASH), e -> confirmDelete(inspection));
        delete.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ERROR);
        return new HorizontalLayout(edit, changeStatus, delete);
    }

    private void openForm(InspectionDTO existing) {
        new InspectionFormDialog(client, workOrderClient, existing, this::refresh).open();
    }

    private void openStatusDialog(InspectionDTO inspection) {
        new InspectionStatusChangeDialog(client, inspection, this::refresh).open();
    }

    private void confirmDelete(InspectionDTO inspection) {
        ConfirmDialog confirm = new ConfirmDialog();
        confirm.setHeader("Delete Inspection");
        confirm.setText("Delete this inspection? This cannot be undone.");
        confirm.setCancelable(true);
        confirm.setConfirmText("Delete");
        confirm.setConfirmButtonTheme("error primary");
        confirm.addConfirmListener(e -> delete(inspection));
        confirm.open();
    }

    private void delete(InspectionDTO inspection) {
        try {
            client.delete(inspection.getId());
            Notification.show("Inspection deleted");
            refresh();
        } catch (GatewayApiException ex) {
            Notification errorNotification = Notification.show("Could not delete inspection: " + ex.getMessage(),
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
        WorkOrderDTO selectedWorkOrder = workOrderFilter.getValue();
        PageResponseDTO<InspectionDTO> result = selectedWorkOrder == null
                ? client.getAll(currentPage, PAGE_SIZE)
                : client.getByWorkOrder(selectedWorkOrder.getId(), currentPage, PAGE_SIZE);
        grid.setItems(result.getContent());
        pagination.update(result.getNumber(), result.getTotalPages());
    }
}
