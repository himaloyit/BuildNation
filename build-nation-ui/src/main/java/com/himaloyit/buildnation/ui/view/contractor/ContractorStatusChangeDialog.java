package com.himaloyit.buildnation.ui.view.contractor;

import com.himaloyit.buildnation.ui.client.contractor.ContractorClient;
import com.himaloyit.buildnation.ui.client.support.GatewayApiException;
import com.himaloyit.buildnation.ui.dto.contractor.ContractorDTO;
import com.himaloyit.buildnation.ui.dto.contractor.ContractorStatus;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;

/** Small dialog driving the Contractor status lifecycle via PATCH /{id}/status. */
public class ContractorStatusChangeDialog extends Dialog {

    public ContractorStatusChangeDialog(ContractorClient client, ContractorDTO contractor, Runnable onSaved) {
        setHeaderTitle("Change Status: " + contractor.getName());

        ComboBox<ContractorStatus> status = new ComboBox<>("New Status");
        status.setItems(ContractorStatus.values());
        status.setValue(contractor.getStatus());
        status.setWidthFull();
        add(status);

        Button cancel = new Button("Cancel", e -> close());
        Button save = new Button("Save", e -> {
            ContractorStatus selected = status.getValue();
            if (selected == null) {
                return;
            }
            try {
                client.updateStatus(contractor.getId(), selected);
                Notification.show("Contractor status updated");
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
