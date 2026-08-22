package com.himaloyit.buildnation.ui.view.region;

import com.himaloyit.buildnation.ui.client.region.DistrictClient;
import com.himaloyit.buildnation.ui.client.support.GatewayApiException;
import com.himaloyit.buildnation.ui.component.PaginationBar;
import com.himaloyit.buildnation.ui.dto.PageResponseDTO;
import com.himaloyit.buildnation.ui.dto.region.DistrictDTO;
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

/** List/Search/View/Add/Edit/Delete for the District region entity (top of the region hierarchy). */
@Route(value = "districts", layout = MainLayout.class)
@PageTitle("Districts | BuildNation")
public class DistrictListView extends VerticalLayout {

    private static final int PAGE_SIZE = 20;

    private final DistrictClient client;
    private final Grid<DistrictDTO> grid = new Grid<>(DistrictDTO.class, false);
    private final PaginationBar pagination = new PaginationBar(this::previousPage, this::nextPage);
    private int currentPage = 0;

    public DistrictListView(DistrictClient client) {
        this.client = client;
        setSizeFull();

        grid.addColumn(DistrictDTO::getName).setHeader("Name");
        grid.addColumn(DistrictDTO::getCode).setHeader("Code");
        grid.addComponentColumn(this::actions).setHeader("Actions").setAutoWidth(true).setFlexGrow(0);
        grid.setSizeFull();

        Button add = new Button("Add District", e -> openForm(null));
        add.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        HorizontalLayout toolbar = new HorizontalLayout(add);
        toolbar.setWidthFull();
        toolbar.setJustifyContentMode(JustifyContentMode.END);

        add(new H2("Districts"), toolbar, grid, pagination);
        setFlexGrow(1, grid);

        refresh();
    }

    private HorizontalLayout actions(DistrictDTO district) {
        Button edit = new Button(new Icon(VaadinIcon.EDIT), e -> openForm(district));
        edit.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        Button delete = new Button(new Icon(VaadinIcon.TRASH), e -> confirmDelete(district));
        delete.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ERROR);
        return new HorizontalLayout(edit, delete);
    }

    private void openForm(DistrictDTO existing) {
        new DistrictFormDialog(client, existing, this::refresh).open();
    }

    private void confirmDelete(DistrictDTO district) {
        ConfirmDialog confirm = new ConfirmDialog();
        confirm.setHeader("Delete District");
        confirm.setText("Delete \"" + district.getName() + "\"? This cannot be undone.");
        confirm.setCancelable(true);
        confirm.setConfirmText("Delete");
        confirm.setConfirmButtonTheme("error primary");
        confirm.addConfirmListener(e -> delete(district));
        confirm.open();
    }

    private void delete(DistrictDTO district) {
        try {
            client.delete(district.getId());
            Notification.show("District deleted");
            refresh();
        } catch (GatewayApiException ex) {
            Notification errorNotification = Notification.show("Could not delete district: " + ex.getMessage(),
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
        PageResponseDTO<DistrictDTO> result = client.getAll(currentPage, PAGE_SIZE);
        grid.setItems(result.getContent());
        pagination.update(result.getNumber(), result.getTotalPages());
    }
}
