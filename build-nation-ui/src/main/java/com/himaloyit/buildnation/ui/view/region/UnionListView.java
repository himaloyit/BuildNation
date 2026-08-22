package com.himaloyit.buildnation.ui.view.region;

import com.himaloyit.buildnation.ui.client.region.UnionClient;
import com.himaloyit.buildnation.ui.client.region.UpazilaClient;
import com.himaloyit.buildnation.ui.client.support.GatewayApiException;
import com.himaloyit.buildnation.ui.component.PaginationBar;
import com.himaloyit.buildnation.ui.dto.PageResponseDTO;
import com.himaloyit.buildnation.ui.dto.region.UnionDTO;
import com.himaloyit.buildnation.ui.dto.region.UpazilaDTO;
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

/** List/Search/View/Add/Edit/Delete for Union, filterable by its parent Upazila. */
@Route(value = "unions", layout = MainLayout.class)
@PageTitle("Unions | BuildNation")
public class UnionListView extends VerticalLayout {

    private static final int PAGE_SIZE = 20;
    private static final int REFERENCE_DATA_PAGE_SIZE = 500;

    private final UnionClient client;
    private final UpazilaClient upazilaClient;
    private final Grid<UnionDTO> grid = new Grid<>(UnionDTO.class, false);
    private final PaginationBar pagination = new PaginationBar(this::previousPage, this::nextPage);
    private final ComboBox<UpazilaDTO> upazilaFilter = new ComboBox<>("Filter by Upazila");
    private final Map<UUID, String> upazilaNamesById = new HashMap<>();
    private int currentPage = 0;

    public UnionListView(UnionClient client, UpazilaClient upazilaClient) {
        this.client = client;
        this.upazilaClient = upazilaClient;
        setSizeFull();

        List<UpazilaDTO> upazilas = upazilaClient.getAll(0, REFERENCE_DATA_PAGE_SIZE).getContent();
        upazilas.forEach(u -> upazilaNamesById.put(u.getId(), u.getName()));
        upazilaFilter.setItemLabelGenerator(UpazilaDTO::getName);
        upazilaFilter.setItems(upazilas);
        upazilaFilter.setClearButtonVisible(true);
        upazilaFilter.addValueChangeListener(e -> {
            currentPage = 0;
            refresh();
        });

        grid.addColumn(UnionDTO::getName).setHeader("Name");
        grid.addColumn(UnionDTO::getCode).setHeader("Code");
        grid.addColumn(u -> upazilaNamesById.getOrDefault(u.getUpazilaId(), "-")).setHeader("Upazila");
        grid.addComponentColumn(this::actions).setHeader("Actions").setAutoWidth(true).setFlexGrow(0);
        grid.setSizeFull();

        Button add = new Button("Add Union", e -> openForm(null));
        add.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        HorizontalLayout toolbar = new HorizontalLayout(upazilaFilter, add);
        toolbar.setWidthFull();
        toolbar.setAlignItems(Alignment.END);
        toolbar.setJustifyContentMode(JustifyContentMode.BETWEEN);

        add(new H2("Unions"), toolbar, grid, pagination);
        setFlexGrow(1, grid);

        refresh();
    }

    private HorizontalLayout actions(UnionDTO union) {
        Button edit = new Button(new Icon(VaadinIcon.EDIT), e -> openForm(union));
        edit.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        Button delete = new Button(new Icon(VaadinIcon.TRASH), e -> confirmDelete(union));
        delete.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ERROR);
        return new HorizontalLayout(edit, delete);
    }

    private void openForm(UnionDTO existing) {
        new UnionFormDialog(client, upazilaClient, existing, this::refresh).open();
    }

    private void confirmDelete(UnionDTO union) {
        ConfirmDialog confirm = new ConfirmDialog();
        confirm.setHeader("Delete Union");
        confirm.setText("Delete \"" + union.getName() + "\"? This cannot be undone.");
        confirm.setCancelable(true);
        confirm.setConfirmText("Delete");
        confirm.setConfirmButtonTheme("error primary");
        confirm.addConfirmListener(e -> delete(union));
        confirm.open();
    }

    private void delete(UnionDTO union) {
        try {
            client.delete(union.getId());
            Notification.show("Union deleted");
            refresh();
        } catch (GatewayApiException ex) {
            Notification errorNotification = Notification.show("Could not delete union: " + ex.getMessage(),
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
        UpazilaDTO selectedUpazila = upazilaFilter.getValue();
        PageResponseDTO<UnionDTO> result = selectedUpazila == null
                ? client.getAll(currentPage, PAGE_SIZE)
                : client.getByUpazila(selectedUpazila.getId(), currentPage, PAGE_SIZE);
        grid.setItems(result.getContent());
        pagination.update(result.getNumber(), result.getTotalPages());
    }
}
