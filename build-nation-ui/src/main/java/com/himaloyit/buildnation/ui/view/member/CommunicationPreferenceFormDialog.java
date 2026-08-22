package com.himaloyit.buildnation.ui.view.member;

import com.himaloyit.buildnation.ui.client.member.CommunicationPreferenceClient;
import com.himaloyit.buildnation.ui.client.support.GatewayApiException;
import com.himaloyit.buildnation.ui.dto.member.CommunicationPreferenceDTO;
import com.himaloyit.buildnation.ui.dto.member.MemberDTO;
import com.himaloyit.buildnation.ui.dto.member.UpdateCommunicationPreferenceRequest;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.Binder;

/**
 * Edit dialog for a Member's CommunicationPreference sub-resource. Update-only, same reasoning as
 * {@link MemberProfileFormDialog} — every Member always has one, created alongside it server-side.
 */
public class CommunicationPreferenceFormDialog extends Dialog {

    private final Binder<CommunicationPreferenceDTO> binder = new Binder<>(CommunicationPreferenceDTO.class);

    public CommunicationPreferenceFormDialog(CommunicationPreferenceClient client, MemberDTO member, Runnable onSaved) {
        setHeaderTitle("Communication Preferences: " + member.getFullName());

        CommunicationPreferenceDTO working = client.getByMemberId(member.getId());

        Checkbox preferEmail = new Checkbox("Prefer Email");
        Checkbox preferSms = new Checkbox("Prefer SMS");
        Checkbox preferWhatsApp = new Checkbox("Prefer WhatsApp");
        Checkbox preferPhone = new Checkbox("Prefer Phone");

        binder.forField(preferEmail).bind(CommunicationPreferenceDTO::isPreferEmail, CommunicationPreferenceDTO::setPreferEmail);
        binder.forField(preferSms).bind(CommunicationPreferenceDTO::isPreferSms, CommunicationPreferenceDTO::setPreferSms);
        binder.forField(preferWhatsApp).bind(CommunicationPreferenceDTO::isPreferWhatsApp, CommunicationPreferenceDTO::setPreferWhatsApp);
        binder.forField(preferPhone).bind(CommunicationPreferenceDTO::isPreferPhone, CommunicationPreferenceDTO::setPreferPhone);
        binder.readBean(working);

        add(new VerticalLayout(preferEmail, preferSms, preferWhatsApp, preferPhone));

        Button cancel = new Button("Cancel", e -> close());
        Button save = new Button("Save", e -> save(client, member, working, onSaved));
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        getFooter().add(cancel, save);
    }

    private void save(CommunicationPreferenceClient client, MemberDTO member, CommunicationPreferenceDTO working,
                       Runnable onSaved) {
        if (!binder.writeBeanIfValid(working)) {
            return;
        }
        try {
            client.update(member.getId(), new UpdateCommunicationPreferenceRequest(working.isPreferEmail(),
                    working.isPreferSms(), working.isPreferWhatsApp(), working.isPreferPhone()));
            Notification.show("Communication preferences saved");
            onSaved.run();
            close();
        } catch (GatewayApiException ex) {
            Notification errorNotification = Notification.show("Could not save preferences: " + ex.getMessage(),
                    5000, Notification.Position.MIDDLE);
            errorNotification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }
}
