package com.himaloyit.buildnation.ui.view.region;

import com.himaloyit.buildnation.ui.client.region.VillageClient;
import com.himaloyit.buildnation.ui.client.region.WardClient;
import com.himaloyit.buildnation.ui.client.support.GatewayApiException;
import com.himaloyit.buildnation.ui.dto.region.CreateVillageRequest;
import com.himaloyit.buildnation.ui.dto.region.UpdateVillageRequest;
import com.himaloyit.buildnation.ui.dto.region.VillageDTO;
import com.himaloyit.buildnation.ui.dto.region.WardDTO;
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

/** Add/Edit dialog for a Village. Reference-data assumption: all wards fit in one combo box page. */
public class VillageFormDialog extends Dialog {

    private static final int REFERENCE_DATA_PAGE_SIZE = 500;

    private final Binder<VillageDTO> binder = new Binder<>(VillageDTO.class);
    private final List<WardDTO> wards;

    public VillageFormDialog(VillageClient client, WardClient wardClient, VillageDTO existing, Runnable onSaved) {
        setHeaderTitle(existing == null ? "Add Village" : "Edit Village");
        this.wards = wardClient.getAll(0, REFERENCE_DATA_PAGE_SIZE).getContent();

        TextField name = new TextField("Name");
        TextField code = new TextField("Code");
        ComboBox<WardDTO> ward = new ComboBox<>("Ward");
        ward.setItemLabelGenerator(WardDTO::getName);
        ward.setItems(wards);

        binder.forField(name).asRequired("Name is mandatory").bind(VillageDTO::getName, VillageDTO::setName);
        binder.forField(code).asRequired("Code is mandatory").bind(VillageDTO::getCode, VillageDTO::setCode);
        binder.forField(ward).asRequired("Ward is mandatory")
                .bind(this::findWardOf, (dto, selected) -> dto.setWardId(selected == null ? null : selected.getId()));

        VillageDTO working = new VillageDTO();
        if (existing != null) {
            working.setId(existing.getId());
            working.setName(existing.getName());
            working.setCode(existing.getCode());
            working.setWardId(existing.getWardId());
        }
        binder.readBean(working);

        FormLayout form = new FormLayout(name, code, ward);
        add(form);

        Button cancel = new Button("Cancel", e -> close());
        Button save = new Button("Save", e -> save(client, existing, working, onSaved));
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        getFooter().add(cancel, save);
    }

    private WardDTO findWardOf(VillageDTO dto) {
        if (dto.getWardId() == null) {
            return null;
        }
        return wards.stream().filter(w -> w.getId().equals(dto.getWardId())).findFirst().orElse(null);
    }

    private void save(VillageClient client, VillageDTO existing, VillageDTO working, Runnable onSaved) {
        if (!binder.writeBeanIfValid(working)) {
            return;
        }
        try {
            if (existing == null) {
                client.create(new CreateVillageRequest(working.getName(), working.getCode(), working.getWardId()));
            } else {
                client.update(existing.getId(),
                        new UpdateVillageRequest(working.getName(), working.getCode(), working.getWardId()));
            }
            Notification.show("Village saved");
            onSaved.run();
            close();
        } catch (GatewayApiException ex) {
            Notification errorNotification = Notification.show("Could not save village: " + ex.getMessage(),
                    5000, Notification.Position.MIDDLE);
            errorNotification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }
}
