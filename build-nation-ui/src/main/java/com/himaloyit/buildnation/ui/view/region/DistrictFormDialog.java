package com.himaloyit.buildnation.ui.view.region;

import com.himaloyit.buildnation.ui.client.region.DistrictClient;
import com.himaloyit.buildnation.ui.client.support.GatewayApiException;
import com.himaloyit.buildnation.ui.dto.region.CreateDistrictRequest;
import com.himaloyit.buildnation.ui.dto.region.DistrictDTO;
import com.himaloyit.buildnation.ui.dto.region.UpdateDistrictRequest;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;

/** Add/Edit dialog for a District. Also serves as the "View" — fields are simply read-only-by-inspection. */
public class DistrictFormDialog extends Dialog {

    private final Binder<DistrictDTO> binder = new Binder<>(DistrictDTO.class);

    public DistrictFormDialog(DistrictClient client, DistrictDTO existing, Runnable onSaved) {
        setHeaderTitle(existing == null ? "Add District" : "Edit District");

        TextField name = new TextField("Name");
        TextField code = new TextField("Code");

        binder.forField(name).asRequired("Name is mandatory").bind(DistrictDTO::getName, DistrictDTO::setName);
        binder.forField(code).asRequired("Code is mandatory").bind(DistrictDTO::getCode, DistrictDTO::setCode);

        DistrictDTO working = new DistrictDTO();
        if (existing != null) {
            working.setId(existing.getId());
            working.setName(existing.getName());
            working.setCode(existing.getCode());
        }
        binder.readBean(working);

        FormLayout form = new FormLayout(name, code);
        add(form);

        Button cancel = new Button("Cancel", e -> close());
        Button save = new Button("Save", e -> save(client, existing, working, onSaved));
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        getFooter().add(cancel, save);
    }

    private void save(DistrictClient client, DistrictDTO existing, DistrictDTO working, Runnable onSaved) {
        if (!binder.writeBeanIfValid(working)) {
            return;
        }
        try {
            if (existing == null) {
                client.create(new CreateDistrictRequest(working.getName(), working.getCode()));
            } else {
                client.update(existing.getId(), new UpdateDistrictRequest(working.getName(), working.getCode()));
            }
            Notification.show("District saved");
            onSaved.run();
            close();
        } catch (GatewayApiException ex) {
            Notification errorNotification = Notification.show("Could not save district: " + ex.getMessage(),
                    5000, Notification.Position.MIDDLE);
            errorNotification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }
}
