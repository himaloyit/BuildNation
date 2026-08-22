package com.himaloyit.buildnation.ui.view.prj;

import com.himaloyit.buildnation.ui.client.prj.CategoryClient;
import com.himaloyit.buildnation.ui.client.prj.SubCategoryClient;
import com.himaloyit.buildnation.ui.client.support.GatewayApiException;
import com.himaloyit.buildnation.ui.dto.prj.CategoryDTO;
import com.himaloyit.buildnation.ui.dto.prj.CreateSubCategoryRequest;
import com.himaloyit.buildnation.ui.dto.prj.SubCategoryDTO;
import com.himaloyit.buildnation.ui.dto.prj.UpdateSubCategoryRequest;
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

/** Add/Edit dialog for a SubCategory. Reference-data assumption: all categories fit in one combo box page. */
public class SubCategoryFormDialog extends Dialog {

    private static final int REFERENCE_DATA_PAGE_SIZE = 500;

    private final Binder<SubCategoryDTO> binder = new Binder<>(SubCategoryDTO.class);
    private final List<CategoryDTO> categories;

    public SubCategoryFormDialog(SubCategoryClient client, CategoryClient categoryClient, SubCategoryDTO existing,
                                  Runnable onSaved) {
        setHeaderTitle(existing == null ? "Add SubCategory" : "Edit SubCategory");
        this.categories = categoryClient.getAll(0, REFERENCE_DATA_PAGE_SIZE).getContent();

        TextField name = new TextField("Name");
        TextField code = new TextField("Code");
        ComboBox<CategoryDTO> category = new ComboBox<>("Category");
        category.setItemLabelGenerator(CategoryDTO::getName);
        category.setItems(categories);

        binder.forField(name).asRequired("Name is mandatory").bind(SubCategoryDTO::getName, SubCategoryDTO::setName);
        binder.forField(code).asRequired("Code is mandatory").bind(SubCategoryDTO::getCode, SubCategoryDTO::setCode);
        binder.forField(category).asRequired("Category is mandatory")
                .bind(this::findCategoryOf, (dto, selected) -> dto.setCategoryId(selected == null ? null : selected.getId()));

        SubCategoryDTO working = new SubCategoryDTO();
        if (existing != null) {
            working.setId(existing.getId());
            working.setName(existing.getName());
            working.setCode(existing.getCode());
            working.setCategoryId(existing.getCategoryId());
        }
        binder.readBean(working);

        FormLayout form = new FormLayout(name, code, category);
        add(form);

        Button cancel = new Button("Cancel", e -> close());
        Button save = new Button("Save", e -> save(client, existing, working, onSaved));
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        getFooter().add(cancel, save);
    }

    private CategoryDTO findCategoryOf(SubCategoryDTO dto) {
        if (dto.getCategoryId() == null) {
            return null;
        }
        return categories.stream().filter(c -> c.getId().equals(dto.getCategoryId())).findFirst().orElse(null);
    }

    private void save(SubCategoryClient client, SubCategoryDTO existing, SubCategoryDTO working, Runnable onSaved) {
        if (!binder.writeBeanIfValid(working)) {
            return;
        }
        try {
            if (existing == null) {
                client.create(new CreateSubCategoryRequest(working.getName(), working.getCode(), working.getCategoryId()));
            } else {
                client.update(existing.getId(),
                        new UpdateSubCategoryRequest(working.getName(), working.getCode(), working.getCategoryId()));
            }
            Notification.show("SubCategory saved");
            onSaved.run();
            close();
        } catch (GatewayApiException ex) {
            Notification errorNotification = Notification.show("Could not save subcategory: " + ex.getMessage(),
                    5000, Notification.Position.MIDDLE);
            errorNotification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }
}
