package com.himaloyit.buildnation.ui.view.wo;

import com.himaloyit.buildnation.ui.client.support.GatewayApiException;
import com.himaloyit.buildnation.ui.client.wo.PaymentClient;
import com.himaloyit.buildnation.ui.client.wo.WorkOrderClient;
import com.himaloyit.buildnation.ui.dto.wo.CreatePaymentRequest;
import com.himaloyit.buildnation.ui.dto.wo.MilestoneType;
import com.himaloyit.buildnation.ui.dto.wo.PaymentDTO;
import com.himaloyit.buildnation.ui.dto.wo.WorkOrderDTO;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.data.binder.Binder;

import java.util.List;
import java.util.UUID;

/**
 * Add-only dialog for a Payment — the backend has no update endpoint (amount is computed from
 * {@code workOrder.amount * percentage / 100} and never submitted directly), so there is no Edit
 * dialog, only this create form plus {@link PaymentStatusChangeDialog} for the status lifecycle.
 */
public class PaymentFormDialog extends Dialog {

    private static final int REFERENCE_DATA_PAGE_SIZE = 500;

    private final Binder<PaymentDTO> binder = new Binder<>(PaymentDTO.class);
    private final List<WorkOrderDTO> workOrders;

    public PaymentFormDialog(PaymentClient client, WorkOrderClient workOrderClient, Runnable onSaved) {
        setHeaderTitle("Add Payment");
        this.workOrders = workOrderClient.getAll(0, REFERENCE_DATA_PAGE_SIZE).getContent();

        ComboBox<WorkOrderDTO> workOrder = new ComboBox<>("Work Order");
        workOrder.setItemLabelGenerator(WorkOrderDTO::getWorkOrderNumber);
        workOrder.setItems(workOrders);

        ComboBox<MilestoneType> milestoneType = new ComboBox<>("Milestone Type");
        milestoneType.setItems(MilestoneType.values());

        IntegerField percentage = new IntegerField("Percentage");
        percentage.setMin(1);
        percentage.setMax(100);

        PaymentDTO working = new PaymentDTO();

        binder.forField(workOrder).asRequired("Work order is mandatory")
                .bind(dto -> findWorkOrderOf(dto.getWorkOrderId()), (dto, selected) -> dto.setWorkOrderId(selected == null ? null : selected.getId()));
        binder.forField(milestoneType).asRequired("Milestone type is mandatory")
                .bind(PaymentDTO::getMilestoneType, PaymentDTO::setMilestoneType);
        binder.forField(percentage).asRequired("Percentage is mandatory")
                .bind(PaymentDTO::getPercentage, PaymentDTO::setPercentage);

        binder.readBean(working);

        FormLayout form = new FormLayout(workOrder, milestoneType, percentage);
        add(form);

        Button cancel = new Button("Cancel", e -> close());
        Button save = new Button("Save", e -> save(client, working, onSaved));
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        getFooter().add(cancel, save);
    }

    private WorkOrderDTO findWorkOrderOf(UUID workOrderId) {
        if (workOrderId == null) {
            return null;
        }
        return workOrders.stream().filter(w -> w.getId().equals(workOrderId)).findFirst().orElse(null);
    }

    private void save(PaymentClient client, PaymentDTO working, Runnable onSaved) {
        if (!binder.writeBeanIfValid(working)) {
            return;
        }
        try {
            client.create(new CreatePaymentRequest(working.getWorkOrderId(), working.getMilestoneType(), working.getPercentage()));
            Notification.show("Payment saved");
            onSaved.run();
            close();
        } catch (GatewayApiException ex) {
            Notification errorNotification = Notification.show("Could not save payment: " + ex.getMessage(),
                    5000, Notification.Position.MIDDLE);
            errorNotification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }
}
