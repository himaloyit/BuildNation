package com.himaloyit.buildnation.ui.view.member;

import com.himaloyit.buildnation.ui.client.member.MemberClient;
import com.himaloyit.buildnation.ui.client.support.GatewayApiException;
import com.himaloyit.buildnation.ui.dto.member.CreateMemberRequest;
import com.himaloyit.buildnation.ui.dto.member.MemberDTO;
import com.himaloyit.buildnation.ui.dto.member.MemberRole;
import com.himaloyit.buildnation.ui.dto.member.UpdateMemberRequest;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;

import java.util.UUID;

/**
 * Add/Edit dialog for a Member. The backend's create and update contracts genuinely diverge — only
 * {@code CreateMemberRequest} takes dob/gender/address/role (dob/gender/address seed the auto-created
 * MemberProfile; role otherwise defaults to GENERAL_MEMBER) and only {@code UpdateMemberRequest} takes
 * position — so this dialog shows a different field set per mode rather than forcing one shape.
 * Editing dob/gender/address afterward happens through {@link MemberProfileFormDialog}; role/status
 * changes happen through their own PATCH-backed dialogs.
 */
public class MemberFormDialog extends Dialog {

    private final Binder<MemberDTO> binder = new Binder<>(MemberDTO.class);

    public MemberFormDialog(MemberClient client, MemberDTO existing, Runnable onSaved) {
        setHeaderTitle(existing == null ? "Add Member" : "Edit Member");

        MemberDTO working = new MemberDTO();
        FormLayout form;

        if (existing == null) {
            TextField fullName = new TextField("Full Name");
            TextField email = new TextField("Email");
            TextField phone = new TextField("Phone");
            DatePicker dob = new DatePicker("Date of Birth");
            TextField gender = new TextField("Gender");
            TextField address = new TextField("Address");
            TextField constituencyId = new TextField("Constituency ID (optional)");
            ComboBox<MemberRole> role = new ComboBox<>("Role");
            role.setItems(MemberRole.values());

            binder.forField(fullName).asRequired("Full name is mandatory").bind(MemberDTO::getFullName, MemberDTO::setFullName);
            binder.forField(email).asRequired("Email is mandatory").bind(MemberDTO::getEmail, MemberDTO::setEmail);
            binder.forField(phone).bind(MemberDTO::getPhone, MemberDTO::setPhone);
            binder.forField(role).bind(MemberDTO::getRole, MemberDTO::setRole);
            binder.readBean(working);

            form = new FormLayout(fullName, email, phone, dob, gender, address, constituencyId, role);
            add(form);

            Button cancel = new Button("Cancel", e -> close());
            Button save = new Button("Save", e -> createMember(client, working, dob.getValue(), gender.getValue(),
                    address.getValue(), constituencyId.getValue(), onSaved));
            save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            getFooter().add(cancel, save);
        } else {
            working.setId(existing.getId());
            working.setFullName(existing.getFullName());
            working.setEmail(existing.getEmail());
            working.setPhone(existing.getPhone());
            working.setPosition(existing.getPosition());
            working.setRole(existing.getRole());
            working.setStatus(existing.getStatus());
            working.setConstituencyId(existing.getConstituencyId());

            TextField email = new TextField("Email");
            email.setValue(existing.getEmail() == null ? "" : existing.getEmail());
            email.setReadOnly(true);
            TextField fullName = new TextField("Full Name");
            TextField phone = new TextField("Phone");
            TextField position = new TextField("Position");
            TextField constituencyId = new TextField("Constituency ID (optional)");

            binder.forField(fullName).asRequired("Full name is mandatory").bind(MemberDTO::getFullName, MemberDTO::setFullName);
            binder.forField(phone).bind(MemberDTO::getPhone, MemberDTO::setPhone);
            binder.forField(position).bind(MemberDTO::getPosition, MemberDTO::setPosition);
            binder.forField(constituencyId)
                    .withConverter(this::parseUuid, id -> id == null ? "" : id.toString())
                    .bind(MemberDTO::getConstituencyId, MemberDTO::setConstituencyId);
            binder.readBean(working);

            form = new FormLayout(email, fullName, phone, position, constituencyId);
            add(form);

            Button cancel = new Button("Cancel", e -> close());
            Button save = new Button("Save", e -> updateMember(client, existing.getId(), working, onSaved));
            save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            getFooter().add(cancel, save);
        }
    }

    private UUID parseUuid(String text) {
        if (text == null || text.isBlank()) {
            return null;
        }
        try {
            return UUID.fromString(text.trim());
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    private void createMember(MemberClient client, MemberDTO working, java.time.LocalDate dob, String gender,
                               String address, String constituencyIdText, Runnable onSaved) {
        if (!binder.writeBeanIfValid(working)) {
            return;
        }
        UUID constituencyId = parseUuid(constituencyIdText);
        String role = working.getRole() == null ? null : working.getRole().name();
        try {
            client.create(new CreateMemberRequest(working.getFullName(), working.getEmail(), working.getPhone(),
                    dob, gender, address, constituencyId, role));
            Notification.show("Member saved");
            onSaved.run();
            close();
        } catch (GatewayApiException ex) {
            showError(ex);
        }
    }

    private void updateMember(MemberClient client, UUID id, MemberDTO working, Runnable onSaved) {
        if (!binder.writeBeanIfValid(working)) {
            return;
        }
        try {
            client.update(id, new UpdateMemberRequest(working.getFullName(), working.getPhone(), working.getPosition(),
                    working.getConstituencyId()));
            Notification.show("Member saved");
            onSaved.run();
            close();
        } catch (GatewayApiException ex) {
            showError(ex);
        }
    }

    private void showError(GatewayApiException ex) {
        Notification errorNotification = Notification.show("Could not save member: " + ex.getMessage(),
                5000, Notification.Position.MIDDLE);
        errorNotification.addThemeVariants(NotificationVariant.LUMO_ERROR);
    }
}
