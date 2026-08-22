package com.himaloyit.buildnation.ui.view.region;

import com.himaloyit.buildnation.ui.client.region.UnionClient;
import com.himaloyit.buildnation.ui.client.region.UpazilaClient;
import com.himaloyit.buildnation.ui.client.support.GatewayApiException;
import com.himaloyit.buildnation.ui.dto.region.CreateUnionRequest;
import com.himaloyit.buildnation.ui.dto.region.UnionDTO;
import com.himaloyit.buildnation.ui.dto.region.UpazilaDTO;
import com.himaloyit.buildnation.ui.dto.region.UpdateUnionRequest;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;

import java.util.List;

/** Add/Edit dialog for a Union. Reference-data assumption: all upazilas fit in one combo box page. */
public class UnionFormDialog extends Dialog {

    private static final int REFERENCE_DATA_PAGE_SIZE = 500;

    private final Binder<UnionDTO> binder = new Binder<>(UnionDTO.class);
    private final List<UpazilaDTO> upazilas;

    public UnionFormDialog(UnionClient client, UpazilaClient upazilaClient, UnionDTO existing, Runnable onSaved) {
        setHeaderTitle(existing == null ? "Add Union" : "Edit Union");
        this.upazilas = upazilaClient.getAll(0, REFERENCE_DATA_PAGE_SIZE).getContent();

        TextField name = new TextField("Name");
        TextField code = new TextField("Code");
        ComboBox<UpazilaDTO> upazila = new ComboBox<>("Upazila");
        upazila.setItemLabelGenerator(UpazilaDTO::getName);
        upazila.setItems(upazilas);

        binder.forField(name).asRequired("Name is mandatory").bind(UnionDTO::getName, UnionDTO::setName);
        binder.forField(code).asRequired("Code is mandatory").bind(UnionDTO::getCode, UnionDTO::setCode);
        binder.forField(upazila).asRequired("Upazila is mandatory")
                .bind(this::findUpazilaOf, (dto, selected) -> dto.setUpazilaId(selected == null ? null : selected.getId()));

        UnionDTO working = new UnionDTO();
        if (existing != null) {
            working.setId(existing.getId());
            working.setName(existing.getName());
            working.setCode(existing.getCode());
            working.setUpazilaId(existing.getUpazilaId());
        }
        binder.readBean(working);

        FormLayout form = new FormLayout(name, code, upazila);
        add(form);

        Button cancel = new Button("Cancel", e -> close());
        Button save = new Button("Save", e -> save(client, existing, working, onSaved));
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        getFooter().add(cancel, save);
    }

    private UpazilaDTO findUpazilaOf(UnionDTO dto) {
        if (dto.getUpazilaId() == null) {
            return null;
        }
        return upazilas.stream().filter(u -> u.getId().equals(dto.getUpazilaId())).findFirst().orElse(null);
    }

    private void save(UnionClient client, UnionDTO existing, UnionDTO working, Runnable onSaved) {
        if (!binder.writeBeanIfValid(working)) {
            return;
        }
        try {
            if (existing == null) {
                client.create(new CreateUnionRequest(working.getName(), working.getCode(), working.getUpazilaId()));
            } else {
                client.update(existing.getId(),
                        new UpdateUnionRequest(working.getName(), working.getCode(), working.getUpazilaId()));
            }
            Notification.show("Union saved");
            onSaved.run();
            close();
        } catch (GatewayApiException ex) {
            Notification errorNotification = Notification.show("Could not save union: " + ex.getMessage(),
                    5000, Notification.Position.MIDDLE);
            errorNotification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }
}
