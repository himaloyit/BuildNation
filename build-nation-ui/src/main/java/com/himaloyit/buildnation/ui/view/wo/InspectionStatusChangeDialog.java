package com.himaloyit.buildnation.ui.view.wo;

import com.himaloyit.buildnation.ui.client.support.GatewayApiException;
import com.himaloyit.buildnation.ui.client.wo.InspectionClient;
import com.himaloyit.buildnation.ui.dto.wo.InspectionDTO;
import com.himaloyit.buildnation.ui.dto.wo.InspectionStatus;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;

/** Small dialog driving the Inspection status lifecycle via PATCH /{id}/status. */
public class InspectionStatusChangeDialog extends Dialog {

    public InspectionStatusChangeDialog(InspectionClient client, InspectionDTO inspection, Runnable onSaved) {
        setHeaderTitle("Change Inspection Status");

        ComboBox<InspectionStatus> status = new ComboBox<>("New Status");
        status.setItems(InspectionStatus.values());
        status.setValue(inspection.getStatus());
        status.setWidthFull();
        add(status);

        Button cancel = new Button("Cancel", e -> close());
        Button save = new Button("Save", e -> {
            InspectionStatus selected = status.getValue();
            if (selected == null) {
                return;
            }
            try {
                client.updateStatus(inspection.getId(), selected);
                Notification.show("Inspection status updated");
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
