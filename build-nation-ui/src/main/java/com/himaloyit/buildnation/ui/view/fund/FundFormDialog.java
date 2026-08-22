package com.himaloyit.buildnation.ui.view.fund;

import com.himaloyit.buildnation.ui.client.fund.FundClient;
import com.himaloyit.buildnation.ui.client.prj.CategoryClient;
import com.himaloyit.buildnation.ui.client.prj.SubCategoryClient;
import com.himaloyit.buildnation.ui.client.support.GatewayApiException;
import com.himaloyit.buildnation.ui.dto.fund.CreateFundRequest;
import com.himaloyit.buildnation.ui.dto.fund.FundDTO;
import com.himaloyit.buildnation.ui.dto.fund.UpdateFundRequest;
import com.himaloyit.buildnation.ui.dto.prj.CategoryDTO;
import com.himaloyit.buildnation.ui.dto.prj.SubCategoryDTO;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;

import java.util.List;
import java.util.UUID;

/**
 * Add/Edit dialog for a Fund. Category/SubCategory are optional on the backend (nullable FKs), and
 * allocated/spent/remaining amounts are transactionally maintained by the backend service — only
 * month, fundType, category, subCategory and receivedAmount are ever submitted from here.
 */
public class FundFormDialog extends Dialog {

    private static final int REFERENCE_DATA_PAGE_SIZE = 500;

    private final Binder<FundDTO> binder = new Binder<>(FundDTO.class);
    private final List<CategoryDTO> categories;
    private final SubCategoryClient subCategoryClient;
    private final ComboBox<SubCategoryDTO> subCategory = new ComboBox<>("SubCategory");
    private List<SubCategoryDTO> subCategoriesForSelectedCategory = List.of();

    public FundFormDialog(FundClient client, CategoryClient categoryClient, SubCategoryClient subCategoryClient,
                           FundDTO existing, Runnable onSaved) {
        setHeaderTitle(existing == null ? "Add Fund" : "Edit Fund");
        this.subCategoryClient = subCategoryClient;
        this.categories = categoryClient.getAll(0, REFERENCE_DATA_PAGE_SIZE).getContent();

        DatePicker month = new DatePicker("Month");
        TextField fundType = new TextField("Fund Type");
        ComboBox<CategoryDTO> category = new ComboBox<>("Category");
        category.setItemLabelGenerator(CategoryDTO::getName);
        category.setItems(categories);
        category.setClearButtonVisible(true);
        subCategory.setItemLabelGenerator(SubCategoryDTO::getName);
        subCategory.setClearButtonVisible(true);
        BigDecimalField receivedAmount = new BigDecimalField("Received Amount");

        binder.forField(month).asRequired("Month is mandatory").bind(FundDTO::getMonth, FundDTO::setMonth);
        binder.forField(fundType).asRequired("Fund type is mandatory").bind(FundDTO::getFundType, FundDTO::setFundType);
        binder.forField(category)
                .bind(this::findCategoryOf, (dto, selected) -> dto.setCategoryId(selected == null ? null : selected.getId()));
        binder.forField(subCategory)
                .bind(this::findSubCategoryOf, (dto, selected) -> dto.setSubCategoryId(selected == null ? null : selected.getId()));
        binder.forField(receivedAmount).asRequired("Received amount is mandatory")
                .bind(FundDTO::getReceivedAmount, FundDTO::setReceivedAmount);

        FundDTO working = new FundDTO();
        if (existing != null) {
            working.setId(existing.getId());
            working.setMonth(existing.getMonth());
            working.setFundType(existing.getFundType());
            working.setCategoryId(existing.getCategoryId());
            working.setSubCategoryId(existing.getSubCategoryId());
            working.setReceivedAmount(existing.getReceivedAmount());

            CategoryDTO existingCategory = findCategoryOf(working);
            onCategoryChanged(existingCategory, existing.getSubCategoryId());
        }
        binder.readBean(working);
        // Registered after the initial readBean so pre-filling the form in edit mode doesn't
        // immediately wipe out the subcategory selection it just set up (see ProjectFormDialog).
        category.addValueChangeListener(e -> onCategoryChanged(e.getValue(), null));

        FormLayout form = new FormLayout(month, fundType, category, subCategory, receivedAmount);
        add(form);

        Button cancel = new Button("Cancel", e -> close());
        Button save = new Button("Save", e -> save(client, existing, working, onSaved));
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        getFooter().add(cancel, save);
    }

    private void onCategoryChanged(CategoryDTO selectedCategory, UUID preselectSubCategoryId) {
        if (selectedCategory == null) {
            subCategoriesForSelectedCategory = List.of();
        } else {
            subCategoriesForSelectedCategory = subCategoryClient.getByCategory(selectedCategory.getId(), 0, REFERENCE_DATA_PAGE_SIZE)
                    .getContent();
        }
        subCategory.setItems(subCategoriesForSelectedCategory);
        if (preselectSubCategoryId != null) {
            subCategoriesForSelectedCategory.stream()
                    .filter(sc -> sc.getId().equals(preselectSubCategoryId))
                    .findFirst()
                    .ifPresent(subCategory::setValue);
        } else {
            subCategory.clear();
        }
    }

    private CategoryDTO findCategoryOf(FundDTO dto) {
        if (dto.getCategoryId() == null) {
            return null;
        }
        return categories.stream().filter(c -> c.getId().equals(dto.getCategoryId())).findFirst().orElse(null);
    }

    private SubCategoryDTO findSubCategoryOf(FundDTO dto) {
        if (dto.getSubCategoryId() == null) {
            return null;
        }
        return subCategoriesForSelectedCategory.stream().filter(s -> s.getId().equals(dto.getSubCategoryId())).findFirst().orElse(null);
    }

    private void save(FundClient client, FundDTO existing, FundDTO working, Runnable onSaved) {
        if (!binder.writeBeanIfValid(working)) {
            return;
        }
        try {
            if (existing == null) {
                client.create(new CreateFundRequest(working.getMonth(), working.getFundType(), working.getCategoryId(),
                        working.getSubCategoryId(), working.getReceivedAmount()));
            } else {
                client.update(existing.getId(), new UpdateFundRequest(working.getMonth(), working.getFundType(),
                        working.getCategoryId(), working.getSubCategoryId(), working.getReceivedAmount()));
            }
            Notification.show("Fund saved");
            onSaved.run();
            close();
        } catch (GatewayApiException ex) {
            Notification errorNotification = Notification.show("Could not save fund: " + ex.getMessage(),
                    5000, Notification.Position.MIDDLE);
            errorNotification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }
}
