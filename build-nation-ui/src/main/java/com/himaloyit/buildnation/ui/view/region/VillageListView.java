package com.himaloyit.buildnation.ui.view.region;

import com.himaloyit.buildnation.ui.client.region.VillageClient;
import com.himaloyit.buildnation.ui.client.region.WardClient;
import com.himaloyit.buildnation.ui.client.support.GatewayApiException;
import com.himaloyit.buildnation.ui.component.PaginationBar;
import com.himaloyit.buildnation.ui.dto.PageResponseDTO;
import com.himaloyit.buildnation.ui.dto.region.VillageDTO;
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

/** List/Search/View/Add/Edit/Delete for Village, filterable by its parent Ward (bottom of the region hierarchy). */
@Route(value = "villages", layout = MainLayout.class)
@PageTitle("Villages | BuildNation")
public class VillageListView extends VerticalLayout {

    private static final int PAGE_SIZE = 20;
    private static final int REFERENCE_DATA_PAGE_SIZE = 500;

    private final VillageClient client;
    private final WardClient wardClient;
    private final Grid<VillageDTO> grid = new Grid<>(VillageDTO.class, false);
    private final PaginationBar pagination = new PaginationBar(this::previousPage, this::nextPage);
    private final ComboBox<WardDTO> wardFilter = new ComboBox<>("Filter by Ward");
    private final Map<UUID, String> wardNamesById = new HashMap<>();
    private int currentPage = 0;

    public VillageListView(VillageClient client, WardClient wardClient) {
        this.client = client;
        this.wardClient = wardClient;
        setSizeFull();

        List<WardDTO> wards = wardClient.getAll(0, REFERENCE_DATA_PAGE_SIZE).getContent();
        wards.forEach(w -> wardNamesById.put(w.getId(), w.getName()));
        wardFilter.setItemLabelGenerator(WardDTO::getName);
        wardFilter.setItems(wards);
        wardFilter.setClearButtonVisible(true);
        wardFilter.addValueChangeListener(e -> {
            currentPage = 0;
            refresh();
        });

        grid.addColumn(VillageDTO::getName).setHeader("Name");
        grid.addColumn(VillageDTO::getCode).setHeader("Code");
        grid.addColumn(v -> wardNamesById.getOrDefault(v.getWardId(), "-")).setHeader("Ward");
        grid.addComponentColumn(this::actions).setHeader("Actions").setAutoWidth(true).setFlexGrow(0);
        grid.setSizeFull();

        Button add = new Button("Add Village", e -> openForm(null));
        add.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        HorizontalLayout toolbar = new HorizontalLayout(wardFilter, add);
        toolbar.setWidthFull();
        toolbar.setAlignItems(Alignment.END);
        toolbar.setJustifyContentMode(JustifyContentMode.BETWEEN);

        add(new H2("Villages"), toolbar, grid, pagination);
        setFlexGrow(1, grid);

        refresh();
    }

    private HorizontalLayout actions(VillageDTO village) {
        Button edit = new Button(new Icon(VaadinIcon.EDIT), e -> openForm(village));
        edit.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        Button delete = new Button(new Icon(VaadinIcon.TRASH), e -> confirmDelete(village));
        delete.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ERROR);
        return new HorizontalLayout(edit, delete);
    }

    private void openForm(VillageDTO existing) {
        new VillageFormDialog(client, wardClient, existing, this::refresh).open();
    }

    private void confirmDelete(VillageDTO village) {
        ConfirmDialog confirm = new ConfirmDialog();
        confirm.setHeader("Delete Village");
        confirm.setText("Delete \"" + village.getName() + "\"? This cannot be undone.");
        confirm.setCancelable(true);
        confirm.setConfirmText("Delete");
        confirm.setConfirmButtonTheme("error primary");
        confirm.addConfirmListener(e -> delete(village));
        confirm.open();
    }

    private void delete(VillageDTO village) {
        try {
            client.delete(village.getId());
            Notification.show("Village deleted");
            refresh();
        } catch (GatewayApiException ex) {
            Notification errorNotification = Notification.show("Could not delete village: " + ex.getMessage(),
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
        WardDTO selectedWard = wardFilter.getValue();
        PageResponseDTO<VillageDTO> result = selectedWard == null
                ? client.getAll(currentPage, PAGE_SIZE)
                : client.getByWard(selectedWard.getId(), currentPage, PAGE_SIZE);
        grid.setItems(result.getContent());
        pagination.update(result.getNumber(), result.getTotalPages());
    }
}
