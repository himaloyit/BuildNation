package com.himaloyit.buildnation.ui.view.member;

import com.himaloyit.buildnation.ui.client.member.MemberClient;
import com.himaloyit.buildnation.ui.client.support.GatewayApiException;
import com.himaloyit.buildnation.ui.dto.member.MemberDTO;
import com.himaloyit.buildnation.ui.dto.member.MemberRole;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;

/** Small dialog driving Member role changes via PATCH /{id}/role. */
public class MemberRoleChangeDialog extends Dialog {

    public MemberRoleChangeDialog(MemberClient client, MemberDTO member, Runnable onSaved) {
        setHeaderTitle("Change Role: " + member.getFullName());

        ComboBox<MemberRole> role = new ComboBox<>("New Role");
        role.setItems(MemberRole.values());
        role.setValue(member.getRole());
        role.setWidthFull();
        add(role);

        Button cancel = new Button("Cancel", e -> close());
        Button save = new Button("Save", e -> {
            MemberRole selected = role.getValue();
            if (selected == null) {
                return;
            }
            try {
                client.updateRole(member.getId(), selected);
                Notification.show("Member role updated");
                onSaved.run();
                close();
            } catch (GatewayApiException ex) {
                Notification errorNotification = Notification.show("Could not update role: " + ex.getMessage(),
                        5000, Notification.Position.MIDDLE);
                errorNotification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        getFooter().add(cancel, save);
    }
}
