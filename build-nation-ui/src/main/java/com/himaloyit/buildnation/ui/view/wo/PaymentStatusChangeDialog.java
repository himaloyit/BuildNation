package com.himaloyit.buildnation.ui.view.wo;

import com.himaloyit.buildnation.ui.client.support.GatewayApiException;
import com.himaloyit.buildnation.ui.client.wo.PaymentClient;
import com.himaloyit.buildnation.ui.dto.wo.PaymentDTO;
import com.himaloyit.buildnation.ui.dto.wo.PaymentStatus;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;

/**
 * Small dialog driving the Payment status lifecycle via PATCH /{id}/status
 * (REQUESTED -&gt; APPROVED/REJECTED, APPROVED -&gt; PAID — the backend enforces the actual state
 * machine rules, e.g. a FINAL milestone needs an APPROVED Inspection first; invalid transitions
 * surface here as a {@link GatewayApiException} with the backend's own message).
 */
public class PaymentStatusChangeDialog extends Dialog {

    public PaymentStatusChangeDialog(PaymentClient client, PaymentDTO payment, Runnable onSaved) {
        setHeaderTitle("Change Payment Status");

        ComboBox<PaymentStatus> status = new ComboBox<>("New Status");
        status.setItems(PaymentStatus.values());
        status.setValue(payment.getStatus());
        status.setWidthFull();
        add(status);

        Button cancel = new Button("Cancel", e -> close());
        Button save = new Button("Save", e -> {
            PaymentStatus selected = status.getValue();
            if (selected == null) {
                return;
            }
            try {
                client.updateStatus(payment.getId(), selected);
                Notification.show("Payment status updated");
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
