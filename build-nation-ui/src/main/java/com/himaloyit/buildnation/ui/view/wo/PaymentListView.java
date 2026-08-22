package com.himaloyit.buildnation.ui.view.wo;

import com.himaloyit.buildnation.ui.client.support.GatewayApiException;
import com.himaloyit.buildnation.ui.client.wo.PaymentClient;
import com.himaloyit.buildnation.ui.client.wo.WorkOrderClient;
import com.himaloyit.buildnation.ui.component.PaginationBar;
import com.himaloyit.buildnation.ui.dto.PageResponseDTO;
import com.himaloyit.buildnation.ui.dto.wo.PaymentDTO;
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

/**
 * List/Search/View/Add/Delete for Payment, filterable by Work Order. No Edit — the backend has no
 * update endpoint, only create/PATCH-status/delete (see {@link PaymentFormDialog}).
 */
@Route(value = "payments", layout = MainLayout.class)
@PageTitle("Payments | BuildNation")
@PermitAll
public class PaymentListView extends VerticalLayout {

    private static final int PAGE_SIZE = 20;
    private static final int REFERENCE_DATA_PAGE_SIZE = 500;

    private final PaymentClient client;
    private final WorkOrderClient workOrderClient;
    private final Grid<PaymentDTO> grid = new Grid<>(PaymentDTO.class, false);
    private final PaginationBar pagination = new PaginationBar(this::previousPage, this::nextPage);
    private final ComboBox<WorkOrderDTO> workOrderFilter = new ComboBox<>("Filter by Work Order");
    private final Map<UUID, String> workOrderNumbersById = new HashMap<>();
    private int currentPage = 0;

    public PaymentListView(PaymentClient client, WorkOrderClient workOrderClient) {
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

        grid.addColumn(p -> workOrderNumbersById.getOrDefault(p.getWorkOrderId(), "-")).setHeader("Work Order");
        grid.addColumn(p -> p.getMilestoneType() == null ? "-" : p.getMilestoneType().name()).setHeader("Milestone");
        grid.addColumn(PaymentDTO::getPercentage).setHeader("Percentage");
        grid.addColumn(PaymentDTO::getAmount).setHeader("Amount");
        grid.addColumn(p -> p.getStatus() == null ? "-" : p.getStatus().name()).setHeader("Status");
        grid.addColumn(PaymentDTO::getVoucherNumber).setHeader("Voucher #");
        grid.addColumn(PaymentDTO::getRequestedDate).setHeader("Requested");
        grid.addColumn(PaymentDTO::getPaymentDate).setHeader("Paid");
        grid.addComponentColumn(this::actions).setHeader("Actions").setAutoWidth(true).setFlexGrow(0);
        grid.setSizeFull();

        Button add = new Button("Add Payment", e -> openForm());
        add.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        HorizontalLayout toolbar = new HorizontalLayout(workOrderFilter, add);
        toolbar.setWidthFull();
        toolbar.setAlignItems(Alignment.END);
        toolbar.setJustifyContentMode(JustifyContentMode.BETWEEN);

        add(new H2("Payments"), toolbar, grid, pagination);
        setFlexGrow(1, grid);

        refresh();
    }

    private HorizontalLayout actions(PaymentDTO payment) {
        Button changeStatus = new Button(new Icon(VaadinIcon.REFRESH), e -> openStatusDialog(payment));
        changeStatus.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        Button delete = new Button(new Icon(VaadinIcon.TRASH), e -> confirmDelete(payment));
        delete.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ERROR);
        return new HorizontalLayout(changeStatus, delete);
    }

    private void openForm() {
        new PaymentFormDialog(client, workOrderClient, this::refresh).open();
    }

    private void openStatusDialog(PaymentDTO payment) {
        new PaymentStatusChangeDialog(client, payment, this::refresh).open();
    }

    private void confirmDelete(PaymentDTO payment) {
        ConfirmDialog confirm = new ConfirmDialog();
        confirm.setHeader("Delete Payment");
        confirm.setText("Delete this payment? This cannot be undone (and the backend rejects it once "
                + "the payment is APPROVED or PAID).");
        confirm.setCancelable(true);
        confirm.setConfirmText("Delete");
        confirm.setConfirmButtonTheme("error primary");
        confirm.addConfirmListener(e -> delete(payment));
        confirm.open();
    }

    private void delete(PaymentDTO payment) {
        try {
            client.delete(payment.getId());
            Notification.show("Payment deleted");
            refresh();
        } catch (GatewayApiException ex) {
            Notification errorNotification = Notification.show("Could not delete payment: " + ex.getMessage(),
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
        PageResponseDTO<PaymentDTO> result = selectedWorkOrder == null
                ? client.getAll(currentPage, PAGE_SIZE)
                : client.getByWorkOrder(selectedWorkOrder.getId(), currentPage, PAGE_SIZE);
        grid.setItems(result.getContent());
        pagination.update(result.getNumber(), result.getTotalPages());
    }
}
