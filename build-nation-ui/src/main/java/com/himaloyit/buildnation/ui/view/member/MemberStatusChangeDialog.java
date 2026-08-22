package com.himaloyit.buildnation.ui.view.member;

import com.himaloyit.buildnation.ui.client.member.MemberClient;
import com.himaloyit.buildnation.ui.client.support.GatewayApiException;
import com.himaloyit.buildnation.ui.dto.member.MemberDTO;
import com.himaloyit.buildnation.ui.dto.member.MemberStatus;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;

/** Small dialog driving Member status changes via PATCH /{id}/status. */
public class MemberStatusChangeDialog extends Dialog {

    public MemberStatusChangeDialog(MemberClient client, MemberDTO member, Runnable onSaved) {
        setHeaderTitle("Change Status: " + member.getFullName());

        ComboBox<MemberStatus> status = new ComboBox<>("New Status");
        status.setItems(MemberStatus.values());
        status.setValue(member.getStatus());
        status.setWidthFull();
        add(status);

        Button cancel = new Button("Cancel", e -> close());
        Button save = new Button("Save", e -> {
            MemberStatus selected = status.getValue();
            if (selected == null) {
                return;
            }
            try {
                client.updateStatus(member.getId(), selected);
                Notification.show("Member status updated");
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
