package com.himaloyit.buildnation.ui.view.prj;

import com.himaloyit.buildnation.ui.client.prj.CategoryClient;
import com.himaloyit.buildnation.ui.client.support.GatewayApiException;
import com.himaloyit.buildnation.ui.dto.prj.CategoryDTO;
import com.himaloyit.buildnation.ui.dto.prj.CreateCategoryRequest;
import com.himaloyit.buildnation.ui.dto.prj.UpdateCategoryRequest;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;

/** Add/Edit dialog for a Project Management Category (top of Category > SubCategory > Project). */
public class CategoryFormDialog extends Dialog {

    private final Binder<CategoryDTO> binder = new Binder<>(CategoryDTO.class);

    public CategoryFormDialog(CategoryClient client, CategoryDTO existing, Runnable onSaved) {
        setHeaderTitle(existing == null ? "Add Category" : "Edit Category");

        TextField name = new TextField("Name");
        TextField code = new TextField("Code");

        binder.forField(name).asRequired("Name is mandatory").bind(CategoryDTO::getName, CategoryDTO::setName);
        binder.forField(code).asRequired("Code is mandatory").bind(CategoryDTO::getCode, CategoryDTO::setCode);

        CategoryDTO working = new CategoryDTO();
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

    private void save(CategoryClient client, CategoryDTO existing, CategoryDTO working, Runnable onSaved) {
        if (!binder.writeBeanIfValid(working)) {
            return;
        }
        try {
            if (existing == null) {
                client.create(new CreateCategoryRequest(working.getName(), working.getCode()));
            } else {
                client.update(existing.getId(), new UpdateCategoryRequest(working.getName(), working.getCode()));
            }
            Notification.show("Category saved");
            onSaved.run();
            close();
        } catch (GatewayApiException ex) {
            Notification errorNotification = Notification.show("Could not save category: " + ex.getMessage(),
                    5000, Notification.Position.MIDDLE);
            errorNotification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }
}
