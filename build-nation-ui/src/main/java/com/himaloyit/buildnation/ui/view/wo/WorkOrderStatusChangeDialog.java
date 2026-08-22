package com.himaloyit.buildnation.ui.view.wo;

import com.himaloyit.buildnation.ui.client.support.GatewayApiException;
import com.himaloyit.buildnation.ui.client.wo.WorkOrderClient;
import com.himaloyit.buildnation.ui.dto.wo.WorkOrderDTO;
import com.himaloyit.buildnation.ui.dto.wo.WorkOrderStatus;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;

/** Small dialog driving the WorkOrder status lifecycle via PATCH /{id}/status. */
public class WorkOrderStatusChangeDialog extends Dialog {

    public WorkOrderStatusChangeDialog(WorkOrderClient client, WorkOrderDTO workOrder, Runnable onSaved) {
        setHeaderTitle("Change Status: " + workOrder.getWorkOrderNumber());

        ComboBox<WorkOrderStatus> status = new ComboBox<>("New Status");
        status.setItems(WorkOrderStatus.values());
        status.setValue(workOrder.getStatus());
        status.setWidthFull();
        add(status);

        Button cancel = new Button("Cancel", e -> close());
        Button save = new Button("Save", e -> {
            WorkOrderStatus selected = status.getValue();
            if (selected == null) {
                return;
            }
            try {
                client.updateStatus(workOrder.getId(), selected);
                Notification.show("Work order status updated");
                onSaved.run();
                close();
            } catch (GatewayApiException ex) {
                Notification errorNotification = Notification.show("Could not update status: " + ex.getMessage(),
                        5000, Notification.Position.MIDDLE);
                errorNotification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        getFooter().add(cancel, save);
    }
}
