package com.himaloyit.buildnation.ui.view.prj;

import com.himaloyit.buildnation.ui.client.prj.CategoryClient;
import com.himaloyit.buildnation.ui.client.support.GatewayApiException;
import com.himaloyit.buildnation.ui.component.PaginationBar;
import com.himaloyit.buildnation.ui.dto.PageResponseDTO;
import com.himaloyit.buildnation.ui.dto.prj.CategoryDTO;
import com.himaloyit.buildnation.ui.view.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
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

/** List/Search/View/Add/Edit/Delete for Project Management Category. */
@Route(value = "categories", layout = MainLayout.class)
@PageTitle("Categories | BuildNation")
@PermitAll
public class CategoryListView extends VerticalLayout {

    private static final int PAGE_SIZE = 20;

    private final CategoryClient client;
    private final Grid<CategoryDTO> grid = new Grid<>(CategoryDTO.class, false);
    private final PaginationBar pagination = new PaginationBar(this::previousPage, this::nextPage);
    private int currentPage = 0;

    public CategoryListView(CategoryClient client) {
        this.client = client;
        setSizeFull();

        grid.addColumn(CategoryDTO::getName).setHeader("Name");
        grid.addColumn(CategoryDTO::getCode).setHeader("Code");
        grid.addComponentColumn(this::actions).setHeader("Actions").setAutoWidth(true).setFlexGrow(0);
        grid.setSizeFull();

        Button add = new Button("Add Category", e -> openForm(null));
        add.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        HorizontalLayout toolbar = new HorizontalLayout(add);
        toolbar.setWidthFull();
        toolbar.setJustifyContentMode(JustifyContentMode.END);

        add(new H2("Categories"), toolbar, grid, pagination);
        setFlexGrow(1, grid);

        refresh();
    }

    private HorizontalLayout actions(CategoryDTO category) {
        Button edit = new Button(new Icon(VaadinIcon.EDIT), e -> openForm(category));
        edit.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        Button delete = new Button(new Icon(VaadinIcon.TRASH), e -> confirmDelete(category));
        delete.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ERROR);
        return new HorizontalLayout(edit, delete);
    }

    private void openForm(CategoryDTO existing) {
        new CategoryFormDialog(client, existing, this::refresh).open();
    }

    private void confirmDelete(CategoryDTO category) {
        ConfirmDialog confirm = new ConfirmDialog();
        confirm.setHeader("Delete Category");
        confirm.setText("Delete \"" + category.getName() + "\"? This cannot be undone.");
        confirm.setCancelable(true);
        confirm.setConfirmText("Delete");
        confirm.setConfirmButtonTheme("error primary");
        confirm.addConfirmListener(e -> delete(category));
        confirm.open();
    }

    private void delete(CategoryDTO category) {
        try {
            client.delete(category.getId());
            Notification.show("Category deleted");
            refresh();
        } catch (GatewayApiException ex) {
            Notification errorNotification = Notification.show("Could not delete category: " + ex.getMessage(),
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
        PageResponseDTO<CategoryDTO> result = client.getAll(currentPage, PAGE_SIZE);
        grid.setItems(result.getContent());
        pagination.update(result.getNumber(), result.getTotalPages());
    }
}
