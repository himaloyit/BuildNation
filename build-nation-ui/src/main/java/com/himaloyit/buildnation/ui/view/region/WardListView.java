package com.himaloyit.buildnation.ui.view.region;

import com.himaloyit.buildnation.ui.client.region.UnionClient;
import com.himaloyit.buildnation.ui.client.region.WardClient;
import com.himaloyit.buildnation.ui.client.support.GatewayApiException;
import com.himaloyit.buildnation.ui.component.PaginationBar;
import com.himaloyit.buildnation.ui.dto.PageResponseDTO;
import com.himaloyit.buildnation.ui.dto.region.UnionDTO;
import com.himaloyit.buildnation.ui.dto.region.WardDTO;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/** List/Search/View/Add/Edit/Delete for Ward, filterable by its parent Union. */
@Route(value = "wards", layout = MainLayout.class)
@PageTitle("Wards | BuildNation")
public class WardListView extends VerticalLayout {

    private static final int PAGE_SIZE = 20;
    private static final int REFERENCE_DATA_PAGE_SIZE = 500;

    private final WardClient client;
    private final UnionClient unionClient;
    private final Grid<WardDTO> grid = new Grid<>(WardDTO.class, false);
    private final PaginationBar pagination = new PaginationBar(this::previousPage, this::nextPage);
    private final ComboBox<UnionDTO> unionFilter = new ComboBox<>("Filter by Union");
    private final Map<UUID, String> unionNamesById = new HashMap<>();
    private int currentPage = 0;

    public WardListView(WardClient client, UnionClient unionClient) {
        this.client = client;
        this.unionClient = unionClient;
        setSizeFull();

        List<UnionDTO> unions = unionClient.getAll(0, REFERENCE_DATA_PAGE_SIZE).getContent();
        unions.forEach(u -> unionNamesById.put(u.getId(), u.getName()));
        unionFilter.setItemLabelGenerator(UnionDTO::getName);
        unionFilter.setItems(unions);
        unionFilter.setClearButtonVisible(true);
        unionFilter.addValueChangeListener(e -> {
            currentPage = 0;
            refresh();
        });

        grid.addColumn(WardDTO::getName).setHeader("Name");
        grid.addColumn(WardDTO::getCode).setHeader("Code");
        grid.addColumn(w -> unionNamesById.getOrDefault(w.getUnionId(), "-")).setHeader("Union");
        grid.addComponentColumn(this::actions).setHeader("Actions").setAutoWidth(true).setFlexGrow(0);
        grid.setSizeFull();

        Button add = new Button("Add Ward", e -> openForm(null));
        add.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        HorizontalLayout toolbar = new HorizontalLayout(unionFilter, add);
        toolbar.setWidthFull();
        toolbar.setAlignItems(Alignment.END);
        toolbar.setJustifyContentMode(JustifyContentMode.BETWEEN);

        add(new H2("Wards"), toolbar, grid, pagination);
        setFlexGrow(1, grid);

        refresh();
    }

    private HorizontalLayout actions(WardDTO ward) {
        Button edit = new Button(new Icon(VaadinIcon.EDIT), e -> openForm(ward));
        edit.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        Button delete = new Button(new Icon(VaadinIcon.TRASH), e -> confirmDelete(ward));
        delete.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ERROR);
        return new HorizontalLayout(edit, delete);
    }

    private void openForm(WardDTO existing) {
        new WardFormDialog(client, unionClient, existing, this::refresh).open();
    }

    private void confirmDelete(WardDTO ward) {
        ConfirmDialog confirm = new ConfirmDialog();
        confirm.setHeader("Delete Ward");
        confirm.setText("Delete \"" + ward.getName() + "\"? This cannot be undone.");
        confirm.setCancelable(true);
        confirm.setConfirmText("Delete");
        confirm.setConfirmButtonTheme("error primary");
        confirm.addConfirmListener(e -> delete(ward));
        confirm.open();
    }

    private void delete(WardDTO ward) {
        try {
            client.delete(ward.getId());
            Notification.show("Ward deleted");
            refresh();
        } catch (GatewayApiException ex) {
            Notification errorNotification = Notification.show("Could not delete ward: " + ex.getMessage(),
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
        UnionDTO selectedUnion = unionFilter.getValue();
        PageResponseDTO<WardDTO> result = selectedUnion == null
                ? client.getAll(currentPage, PAGE_SIZE)
                : client.getByUnion(selectedUnion.getId(), currentPage, PAGE_SIZE);
        grid.setItems(result.getContent());
        pagination.update(result.getNumber(), result.getTotalPages());
    }
}
