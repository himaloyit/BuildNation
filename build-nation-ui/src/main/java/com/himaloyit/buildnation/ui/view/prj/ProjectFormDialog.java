package com.himaloyit.buildnation.ui.view.prj;

import com.himaloyit.buildnation.ui.client.prj.CategoryClient;
import com.himaloyit.buildnation.ui.client.prj.ProjectClient;
import com.himaloyit.buildnation.ui.client.prj.SubCategoryClient;
import com.himaloyit.buildnation.ui.client.region.VillageClient;
import com.himaloyit.buildnation.ui.client.support.GatewayApiException;
import com.himaloyit.buildnation.ui.dto.prj.CategoryDTO;
import com.himaloyit.buildnation.ui.dto.prj.CreateProjectRequest;
import com.himaloyit.buildnation.ui.dto.prj.ProjectDTO;
import com.himaloyit.buildnation.ui.dto.prj.SubCategoryDTO;
import com.himaloyit.buildnation.ui.dto.prj.UpdateProjectRequest;
import com.himaloyit.buildnation.ui.dto.region.VillageDTO;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;

import java.util.List;

/**
 * Add/Edit dialog for a Project. Priority score is only settable at creation (the backend has no
 * update-priority-via-PUT path, only a separate PATCH /{id}/priority — deferred along with the
 * priority-queue screen, see build_nation_ui memory), so it's shown read-only when editing.
 */
public class ProjectFormDialog extends Dialog {

    private static final int REFERENCE_DATA_PAGE_SIZE = 500;

    private final Binder<ProjectDTO> binder = new Binder<>(ProjectDTO.class);
    private final List<CategoryDTO> categories;
    private final List<VillageDTO> villages;
    private final SubCategoryClient subCategoryClient;
    private final ComboBox<SubCategoryDTO> subCategory = new ComboBox<>("SubCategory");
    private List<SubCategoryDTO> subCategoriesForSelectedCategory = List.of();

    public ProjectFormDialog(ProjectClient client, CategoryClient categoryClient, SubCategoryClient subCategoryClient,
                              VillageClient villageClient, ProjectDTO existing, Runnable onSaved) {
        setHeaderTitle(existing == null ? "Add Project" : "Edit Project");
        this.subCategoryClient = subCategoryClient;
        this.categories = categoryClient.getAll(0, REFERENCE_DATA_PAGE_SIZE).getContent();
        this.villages = villageClient.getAll(0, REFERENCE_DATA_PAGE_SIZE).getContent();

        TextField name = new TextField("Name");
        TextArea description = new TextArea("Description");
        TextField currentCondition = new TextField("Current Condition");
        ComboBox<CategoryDTO> category = new ComboBox<>("Category");
        category.setItemLabelGenerator(CategoryDTO::getName);
        category.setItems(categories);
        subCategory.setItemLabelGenerator(SubCategoryDTO::getName);
        ComboBox<VillageDTO> village = new ComboBox<>("Village");
        village.setItemLabelGenerator(VillageDTO::getName);
        village.setItems(villages);
        BigDecimalField estimatedCost = new BigDecimalField("Estimated Cost");
        IntegerField priorityScore = new IntegerField("Priority Score");
        priorityScore.setMin(0);
        TextField submittedBy = new TextField("Submitted By");

        binder.forField(name).asRequired("Name is mandatory").bind(ProjectDTO::getName, ProjectDTO::setName);
        binder.forField(description).bind(ProjectDTO::getDescription, ProjectDTO::setDescription);
        binder.forField(currentCondition).bind(ProjectDTO::getCurrentCondition, ProjectDTO::setCurrentCondition);
        binder.forField(category).asRequired("Category is mandatory")
                .bind(this::findCategoryOf, (dto, selected) -> dto.setCategoryId(selected == null ? null : selected.getId()));
        binder.forField(subCategory).asRequired("SubCategory is mandatory")
                .bind(this::findSubCategoryOf, (dto, selected) -> dto.setSubCategoryId(selected == null ? null : selected.getId()));
        binder.forField(village).asRequired("Village is mandatory")
                .bind(this::findVillageOf, (dto, selected) -> dto.setVillageId(selected == null ? null : selected.getId()));
        binder.forField(estimatedCost).asRequired("Estimated cost is mandatory")
                .bind(ProjectDTO::getEstimatedCost, ProjectDTO::setEstimatedCost);
        binder.forField(priorityScore).bind(ProjectDTO::getPriorityScore, ProjectDTO::setPriorityScore);
        binder.forField(submittedBy).bind(ProjectDTO::getSubmittedBy, ProjectDTO::setSubmittedBy);

        ProjectDTO working = new ProjectDTO();
        if (existing != null) {
            working.setId(existing.getId());
            working.setName(existing.getName());
            working.setDescription(existing.getDescription());
            working.setCurrentCondition(existing.getCurrentCondition());
            working.setCategoryId(existing.getCategoryId());
            working.setSubCategoryId(existing.getSubCategoryId());
            working.setVillageId(existing.getVillageId());
            working.setEstimatedCost(existing.getEstimatedCost());
            working.setPriorityScore(existing.getPriorityScore());
            working.setSubmittedBy(existing.getSubmittedBy());
            working.setStatus(existing.getStatus());

            CategoryDTO existingCategory = findCategoryOf(working);
            onCategoryChanged(existingCategory, existing.getSubCategoryId());
            priorityScore.setReadOnly(true);
        }
        binder.readBean(working);
        // Registered after the initial readBean so pre-filling the form in edit mode doesn't
        // immediately wipe out the subcategory selection it just set up.
        category.addValueChangeListener(e -> onCategoryChanged(e.getValue(), null));

        FormLayout form = new FormLayout(name, description, currentCondition, category, subCategory, village,
                estimatedCost, priorityScore, submittedBy);
        form.setColspan(description, 2);
        add(form);

        Button cancel = new Button("Cancel", e -> close());
        Button save = new Button("Save", e -> save(client, existing, working, onSaved));
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        getFooter().add(cancel, save);
    }

    private void onCategoryChanged(CategoryDTO selectedCategory, java.util.UUID preselectSubCategoryId) {
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

    private CategoryDTO findCategoryOf(ProjectDTO dto) {
        if (dto.getCategoryId() == null) {
            return null;
        }
        return categories.stream().filter(c -> c.getId().equals(dto.getCategoryId())).findFirst().orElse(null);
    }

    private SubCategoryDTO findSubCategoryOf(ProjectDTO dto) {
        if (dto.getSubCategoryId() == null) {
            return null;
        }
        return subCategoriesForSelectedCategory.stream().filter(s -> s.getId().equals(dto.getSubCategoryId())).findFirst().orElse(null);
    }

    private VillageDTO findVillageOf(ProjectDTO dto) {
        if (dto.getVillageId() == null) {
            return null;
        }
        return villages.stream().filter(v -> v.getId().equals(dto.getVillageId())).findFirst().orElse(null);
    }

    private void save(ProjectClient client, ProjectDTO existing, ProjectDTO working, Runnable onSaved) {
        if (!binder.writeBeanIfValid(working)) {
            return;
        }
        try {
            if (existing == null) {
                client.create(new CreateProjectRequest(working.getName(), working.getDescription(),
                        working.getCurrentCondition(), working.getCategoryId(), working.getSubCategoryId(),
                        working.getVillageId(), working.getEstimatedCost(), working.getPriorityScore(),
                        working.getSubmittedBy()));
            } else {
                client.update(existing.getId(), new UpdateProjectRequest(working.getName(), working.getDescription(),
                        working.getCurrentCondition(), working.getCategoryId(), working.getSubCategoryId(),
                        working.getVillageId(), working.getEstimatedCost(), working.getSubmittedBy()));
            }
            Notification.show("Project saved");
            onSaved.run();
            close();
        } catch (GatewayApiException ex) {
            Notification errorNotification = Notification.show("Could not save project: " + ex.getMessage(),
                    5000, Notification.Position.MIDDLE);
            errorNotification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }
}
