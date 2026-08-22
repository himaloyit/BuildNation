package com.himaloyit.buildnation.ui.view.prj;

import com.himaloyit.buildnation.ui.client.prj.CategoryClient;
import com.himaloyit.buildnation.ui.client.prj.ProjectClient;
import com.himaloyit.buildnation.ui.client.prj.SubCategoryClient;
import com.himaloyit.buildnation.ui.client.region.VillageClient;
import com.himaloyit.buildnation.ui.client.support.GatewayApiException;
import com.himaloyit.buildnation.ui.component.PaginationBar;
import com.himaloyit.buildnation.ui.dto.PageResponseDTO;
import com.himaloyit.buildnation.ui.dto.prj.CategoryDTO;
import com.himaloyit.buildnation.ui.dto.prj.ProjectDTO;
import com.himaloyit.buildnation.ui.dto.prj.ProjectStatus;
import com.himaloyit.buildnation.ui.dto.prj.SubCategoryDTO;
import com.himaloyit.buildnation.ui.dto.region.VillageDTO;
import com.himaloyit.buildnation.ui.view.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import jakarta.annotation.security.PermitAll;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * List/Search/View/Add/Edit/Delete for Project, filterable by Category or Status (at most one
 * filter at a time — the backend doesn't support combining them). Priority-queue management is a
 * separate, not-yet-built screen (see build_nation_ui memory); status changes here are a plain
 * lifecycle PATCH via {@link ProjectStatusChangeDialog}.
 */
@Route(value = "projects", layout = MainLayout.class)
@PageTitle("Projects | BuildNation")
@PermitAll
public class ProjectListView extends VerticalLayout {

    private static final int PAGE_SIZE = 20;
    private static final int REFERENCE_DATA_PAGE_SIZE = 500;

    private final ProjectClient client;
    private final CategoryClient categoryClient;
    private final SubCategoryClient subCategoryClient;
    private final VillageClient villageClient;
    private final Grid<ProjectDTO> grid = new Grid<>(ProjectDTO.class, false);
    private final PaginationBar pagination = new PaginationBar(this::previousPage, this::nextPage);
    private final ComboBox<CategoryDTO> categoryFilter = new ComboBox<>("Filter by Category");
    private final ComboBox<ProjectStatus> statusFilter = new ComboBox<>("Filter by Status");
    private final Map<UUID, String> categoryNamesById = new HashMap<>();
    private final Map<UUID, String> subCategoryNamesById = new HashMap<>();
    private final Map<UUID, String> villageNamesById = new HashMap<>();
    private int currentPage = 0;

    public ProjectListView(ProjectClient client, CategoryClient categoryClient, SubCategoryClient subCategoryClient,
                            VillageClient villageClient) {
        this.client = client;
        this.categoryClient = categoryClient;
        this.subCategoryClient = subCategoryClient;
        this.villageClient = villageClient;
        setSizeFull();

        List<CategoryDTO> categories = categoryClient.getAll(0, REFERENCE_DATA_PAGE_SIZE).getContent();
        categories.forEach(c -> categoryNamesById.put(c.getId(), c.getName()));
        List<SubCategoryDTO> subCategories = subCategoryClient.getAll(0, REFERENCE_DATA_PAGE_SIZE).getContent();
        subCategories.forEach(s -> subCategoryNamesById.put(s.getId(), s.getName()));
        List<VillageDTO> villages = villageClient.getAll(0, REFERENCE_DATA_PAGE_SIZE).getContent();
        villages.forEach(v -> villageNamesById.put(v.getId(), v.getName()));

        categoryFilter.setItemLabelGenerator(CategoryDTO::getName);
        categoryFilter.setItems(categories);
        categoryFilter.setClearButtonVisible(true);
        categoryFilter.addValueChangeListener(e -> {
            if (e.getValue() != null) {
                statusFilter.clear();
            }
            currentPage = 0;
            refresh();
        });

        statusFilter.setItems(ProjectStatus.values());
        statusFilter.setClearButtonVisible(true);
        statusFilter.addValueChangeListener(e -> {
            if (e.getValue() != null) {
                categoryFilter.clear();
            }
            currentPage = 0;
            refresh();
        });

        grid.addColumn(ProjectDTO::getName).setHeader("Name");
        grid.addColumn(p -> categoryNamesById.getOrDefault(p.getCategoryId(), "-")).setHeader("Category");
        grid.addColumn(p -> subCategoryNamesById.getOrDefault(p.getSubCategoryId(), "-")).setHeader("SubCategory");
        grid.addColumn(p -> villageNamesById.getOrDefault(p.getVillageId(), "-")).setHeader("Village");
        grid.addColumn(ProjectDTO::getEstimatedCost).setHeader("Estimated Cost");
        grid.addColumn(ProjectDTO::getPriorityScore).setHeader("Priority");
        grid.addColumn(p -> p.getStatus() == null ? "-" : p.getStatus().name()).setHeader("Status");
        grid.addComponentColumn(this::actions).setHeader("Actions").setAutoWidth(true).setFlexGrow(0);
        grid.setSizeFull();

        Button add = new Button("Add Project", e -> openForm(null));
        add.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        HorizontalLayout toolbar = new HorizontalLayout(categoryFilter, statusFilter, add);
        toolbar.setWidthFull();
        toolbar.setAlignItems(Alignment.END);
        toolbar.setJustifyContentMode(JustifyContentMode.BETWEEN);

        add(new H2("Projects"), toolbar, grid, pagination);
        setFlexGrow(1, grid);

        refresh();
    }

    private HorizontalLayout actions(ProjectDTO project) {
        Button edit = new Button(new Icon(VaadinIcon.EDIT), e -> openForm(project));
        edit.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        Button changeStatus = new Button(new Icon(VaadinIcon.REFRESH), e -> openStatusDialog(project));
        changeStatus.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        Button delete = new Button(new Icon(VaadinIcon.TRASH), e -> confirmDelete(project));
        delete.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ERROR);
        return new HorizontalLayout(edit, changeStatus, delete);
    }

    private void openForm(ProjectDTO existing) {
        new ProjectFormDialog(client, categoryClient, subCategoryClient, villageClient, existing, this::refresh).open();
    }

    private void openStatusDialog(ProjectDTO project) {
        new ProjectStatusChangeDialog(client, project, this::refresh).open();
    }

    private void confirmDelete(ProjectDTO project) {
        ConfirmDialog confirm = new ConfirmDialog();
        confirm.setHeader("Delete Project");
        confirm.setText("Delete \"" + project.getName() + "\"? This cannot be undone.");
        confirm.setCancelable(true);
        confirm.setConfirmText("Delete");
        confirm.setConfirmButtonTheme("error primary");
        confirm.addConfirmListener(e -> delete(project));
        confirm.open();
    }

    private void delete(ProjectDTO project) {
        try {
            client.delete(project.getId());
            Notification.show("Project deleted");
            refresh();
        } catch (GatewayApiException ex) {
            Notification errorNotification = Notification.show("Could not delete project: " + ex.getMessage(),
                    5000, Notification.Position.MIDDLE);
            errorNotification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void previousPage() {
        currentPage--;
        refresh();
    }

    private void nextPage() {
        currentPage++;
        refresh();
    }

    private void refresh() {
        CategoryDTO selectedCategory = categoryFilter.getValue();
        ProjectStatus selectedStatus = statusFilter.getValue();
        PageResponseDTO<ProjectDTO> result;
        if (selectedCategory != null) {
            result = client.getByCategory(selectedCategory.getId(), currentPage, PAGE_SIZE);
        } else if (selectedStatus != null) {
            result = client.getByStatus(selectedStatus, currentPage, PAGE_SIZE);
        } else {
            result = client.getAll(currentPage, PAGE_SIZE);
        }
        grid.setItems(result.getContent());
        pagination.update(result.getNumber(), result.getTotalPages());
    }
}
