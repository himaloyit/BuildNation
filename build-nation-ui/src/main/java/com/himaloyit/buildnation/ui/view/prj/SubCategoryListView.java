package com.himaloyit.buildnation.ui.view.prj;

import com.himaloyit.buildnation.ui.client.prj.CategoryClient;
import com.himaloyit.buildnation.ui.client.prj.SubCategoryClient;
import com.himaloyit.buildnation.ui.client.support.GatewayApiException;
import com.himaloyit.buildnation.ui.component.PaginationBar;
import com.himaloyit.buildnation.ui.dto.PageResponseDTO;
import com.himaloyit.buildnation.ui.dto.prj.CategoryDTO;
import com.himaloyit.buildnation.ui.dto.prj.SubCategoryDTO;
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

/** List/Search/View/Add/Edit/Delete for SubCategory, filterable by its parent Category. */
@Route(value = "subcategories", layout = MainLayout.class)
@PageTitle("SubCategories | BuildNation")
@PermitAll
public class SubCategoryListView extends VerticalLayout {

    private static final int PAGE_SIZE = 20;
    private static final int REFERENCE_DATA_PAGE_SIZE = 500;

    private final SubCategoryClient client;
    private final CategoryClient categoryClient;
    private final Grid<SubCategoryDTO> grid = new Grid<>(SubCategoryDTO.class, false);
    private final PaginationBar pagination = new PaginationBar(this::previousPage, this::nextPage);
    private final ComboBox<CategoryDTO> categoryFilter = new ComboBox<>("Filter by Category");
    private final Map<UUID, String> categoryNamesById = new HashMap<>();
    private int currentPage = 0;

    public SubCategoryListView(SubCategoryClient client, CategoryClient categoryClient) {
        this.client = client;
        this.categoryClient = categoryClient;
        setSizeFull();

        List<CategoryDTO> categories = categoryClient.getAll(0, REFERENCE_DATA_PAGE_SIZE).getContent();
        categories.forEach(c -> categoryNamesById.put(c.getId(), c.getName()));
        categoryFilter.setItemLabelGenerator(CategoryDTO::getName);
        categoryFilter.setItems(categories);
        categoryFilter.setClearButtonVisible(true);
        categoryFilter.addValueChangeListener(e -> {
            currentPage = 0;
            refresh();
        });

        grid.addColumn(SubCategoryDTO::getName).setHeader("Name");
        grid.addColumn(SubCategoryDTO::getCode).setHeader("Code");
        grid.addColumn(s -> categoryNamesById.getOrDefault(s.getCategoryId(), "-")).setHeader("Category");
        grid.addComponentColumn(this::actions).setHeader("Actions").setAutoWidth(true).setFlexGrow(0);
        grid.setSizeFull();

        Button add = new Button("Add SubCategory", e -> openForm(null));
        add.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        HorizontalLayout toolbar = new HorizontalLayout(categoryFilter, add);
        toolbar.setWidthFull();
        toolbar.setAlignItems(Alignment.END);
        toolbar.setJustifyContentMode(JustifyContentMode.BETWEEN);

        add(new H2("SubCategories"), toolbar, grid, pagination);
        setFlexGrow(1, grid);

        refresh();
    }

    private HorizontalLayout actions(SubCategoryDTO subCategory) {
        Button edit = new Button(new Icon(VaadinIcon.EDIT), e -> openForm(subCategory));
        edit.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        Button delete = new Button(new Icon(VaadinIcon.TRASH), e -> confirmDelete(subCategory));
        delete.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ERROR);
        return new HorizontalLayout(edit, delete);
    }

    private void openForm(SubCategoryDTO existing) {
        new SubCategoryFormDialog(client, categoryClient, existing, this::refresh).open();
    }

    private void confirmDelete(SubCategoryDTO subCategory) {
        ConfirmDialog confirm = new ConfirmDialog();
        confirm.setHeader("Delete SubCategory");
        confirm.setText("Delete \"" + subCategory.getName() + "\"? This cannot be undone.");
        confirm.setCancelable(true);
        confirm.setConfirmText("Delete");
        confirm.setConfirmButtonTheme("error primary");
        confirm.addConfirmListener(e -> delete(subCategory));
        confirm.open();
    }

    private void delete(SubCategoryDTO subCategory) {
        try {
            client.delete(subCategory.getId());
            Notification.show("SubCategory deleted");
            refresh();
        } catch (GatewayApiException ex) {
            Notification errorNotification = Notification.show("Could not delete subcategory: " + ex.getMessage(),
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
        PageResponseDTO<SubCategoryDTO> result = selectedCategory == null
                ? client.getAll(currentPage, PAGE_SIZE)
                : client.getByCategory(selectedCategory.getId(), currentPage, PAGE_SIZE);
        grid.setItems(result.getContent());
        pagination.update(result.getNumber(), result.getTotalPages());
    }
}
