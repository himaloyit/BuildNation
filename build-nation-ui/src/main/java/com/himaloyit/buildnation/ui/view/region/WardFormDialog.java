package com.himaloyit.buildnation.ui.view.region;

import com.himaloyit.buildnation.ui.client.region.UnionClient;
import com.himaloyit.buildnation.ui.client.region.WardClient;
import com.himaloyit.buildnation.ui.client.support.GatewayApiException;
import com.himaloyit.buildnation.ui.dto.region.CreateWardRequest;
import com.himaloyit.buildnation.ui.dto.region.UnionDTO;
import com.himaloyit.buildnation.ui.dto.region.UpdateWardRequest;
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

/** Add/Edit dialog for a Ward. Reference-data assumption: all unions fit in one combo box page. */
public class WardFormDialog extends Dialog {

    private static final int REFERENCE_DATA_PAGE_SIZE = 500;

    private final Binder<WardDTO> binder = new Binder<>(WardDTO.class);
    private final List<UnionDTO> unions;

    public WardFormDialog(WardClient client, UnionClient unionClient, WardDTO existing, Runnable onSaved) {
        setHeaderTitle(existing == null ? "Add Ward" : "Edit Ward");
        this.unions = unionClient.getAll(0, REFERENCE_DATA_PAGE_SIZE).getContent();

        TextField name = new TextField("Name");
        TextField code = new TextField("Code");
        ComboBox<UnionDTO> union = new ComboBox<>("Union");
        union.setItemLabelGenerator(UnionDTO::getName);
        union.setItems(unions);

        binder.forField(name).asRequired("Name is mandatory").bind(WardDTO::getName, WardDTO::setName);
        binder.forField(code).asRequired("Code is mandatory").bind(WardDTO::getCode, WardDTO::setCode);
        binder.forField(union).asRequired("Union is mandatory")
                .bind(this::findUnionOf, (dto, selected) -> dto.setUnionId(selected == null ? null : selected.getId()));

        WardDTO working = new WardDTO();
        if (existing != null) {
            working.setId(existing.getId());
            working.setName(existing.getName());
            working.setCode(existing.getCode());
            working.setUnionId(existing.getUnionId());
        }
        binder.readBean(working);

        FormLayout form = new FormLayout(name, code, union);
        add(form);

        Button cancel = new Button("Cancel", e -> close());
        Button save = new Button("Save", e -> save(client, existing, working, onSaved));
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        getFooter().add(cancel, save);
    }

    private UnionDTO findUnionOf(WardDTO dto) {
        if (dto.getUnionId() == null) {
            return null;
        }
        return unions.stream().filter(u -> u.getId().equals(dto.getUnionId())).findFirst().orElse(null);
    }

    private void save(WardClient client, WardDTO existing, WardDTO working, Runnable onSaved) {
        if (!binder.writeBeanIfValid(working)) {
            return;
        }
        try {
            if (existing == null) {
                client.create(new CreateWardRequest(working.getName(), working.getCode(), working.getUnionId()));
            } else {
                client.update(existing.getId(),
                        new UpdateWardRequest(working.getName(), working.getCode(), working.getUnionId()));
            }
            Notification.show("Ward saved");
            onSaved.run();
            close();
        } catch (GatewayApiException ex) {
            Notification errorNotification = Notification.show("Could not save ward: " + ex.getMessage(),
                    5000, Notification.Position.MIDDLE);
            errorNotification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }
}
