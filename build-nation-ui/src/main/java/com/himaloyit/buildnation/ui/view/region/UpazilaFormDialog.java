package com.himaloyit.buildnation.ui.view.region;

import com.himaloyit.buildnation.ui.client.region.DistrictClient;
import com.himaloyit.buildnation.ui.client.region.UpazilaClient;
import com.himaloyit.buildnation.ui.client.support.GatewayApiException;
import com.himaloyit.buildnation.ui.dto.region.CreateUpazilaRequest;
import com.himaloyit.buildnation.ui.dto.region.DistrictDTO;
import com.himaloyit.buildnation.ui.dto.region.UpazilaDTO;
import com.himaloyit.buildnation.ui.dto.region.UpdateUpazilaRequest;
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

/** Add/Edit dialog for an Upazila. Reference-data assumption: all districts fit in one combo box page. */
public class UpazilaFormDialog extends Dialog {

    private static final int REFERENCE_DATA_PAGE_SIZE = 500;

    private final Binder<UpazilaDTO> binder = new Binder<>(UpazilaDTO.class);
    private final List<DistrictDTO> districts;

    public UpazilaFormDialog(UpazilaClient client, DistrictClient districtClient, UpazilaDTO existing, Runnable onSaved) {
        setHeaderTitle(existing == null ? "Add Upazila" : "Edit Upazila");
        this.districts = districtClient.getAll(0, REFERENCE_DATA_PAGE_SIZE).getContent();

        TextField name = new TextField("Name");
        TextField code = new TextField("Code");
        ComboBox<DistrictDTO> district = new ComboBox<>("District");
        district.setItemLabelGenerator(DistrictDTO::getName);
        district.setItems(districts);

        binder.forField(name).asRequired("Name is mandatory").bind(UpazilaDTO::getName, UpazilaDTO::setName);
        binder.forField(code).asRequired("Code is mandatory").bind(UpazilaDTO::getCode, UpazilaDTO::setCode);
        binder.forField(district).asRequired("District is mandatory")
                .bind(this::findDistrictOf, (dto, selected) -> dto.setDistrictId(selected == null ? null : selected.getId()));

        UpazilaDTO working = new UpazilaDTO();
        if (existing != null) {
            working.setId(existing.getId());
            working.setName(existing.getName());
            working.setCode(existing.getCode());
            working.setDistrictId(existing.getDistrictId());
        }
        binder.readBean(working);

        FormLayout form = new FormLayout(name, code, district);
        add(form);

        Button cancel = new Button("Cancel", e -> close());
        Button save = new Button("Save", e -> save(client, existing, working, onSaved));
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        getFooter().add(cancel, save);
    }

    private DistrictDTO findDistrictOf(UpazilaDTO dto) {
        if (dto.getDistrictId() == null) {
            return null;
        }
        return districts.stream().filter(d -> d.getId().equals(dto.getDistrictId())).findFirst().orElse(null);
    }

    private void save(UpazilaClient client, UpazilaDTO existing, UpazilaDTO working, Runnable onSaved) {
        if (!binder.writeBeanIfValid(working)) {
            return;
        }
        try {
            if (existing == null) {
                client.create(new CreateUpazilaRequest(working.getName(), working.getCode(), working.getDistrictId()));
            } else {
                client.update(existing.getId(),
                        new UpdateUpazilaRequest(working.getName(), working.getCode(), working.getDistrictId()));
            }
            Notification.show("Upazila saved");
            onSaved.run();
            close();
        } catch (GatewayApiException ex) {
            Notification errorNotification = Notification.show("Could not save upazila: " + ex.getMessage(),
                    5000, Notification.Position.MIDDLE);
            errorNotification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }
}
