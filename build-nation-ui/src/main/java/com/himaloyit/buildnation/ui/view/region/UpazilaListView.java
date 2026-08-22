package com.himaloyit.buildnation.ui.view.region;

import com.himaloyit.buildnation.ui.client.region.DistrictClient;
import com.himaloyit.buildnation.ui.client.region.UpazilaClient;
import com.himaloyit.buildnation.ui.client.support.GatewayApiException;
import com.himaloyit.buildnation.ui.component.PaginationBar;
import com.himaloyit.buildnation.ui.dto.PageResponseDTO;
import com.himaloyit.buildnation.ui.dto.region.DistrictDTO;
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

/** List/Search/View/Add/Edit/Delete for Upazila, filterable by its parent District. */
@Route(value = "upazilas", layout = MainLayout.class)
@PageTitle("Upazilas | BuildNation")
public class UpazilaListView extends VerticalLayout {

    private static final int PAGE_SIZE = 20;
    private static final int REFERENCE_DATA_PAGE_SIZE = 500;

    private final UpazilaClient client;
    private final DistrictClient districtClient;
    private final Grid<UpazilaDTO> grid = new Grid<>(UpazilaDTO.class, false);
    private final PaginationBar pagination = new PaginationBar(this::previousPage, this::nextPage);
    private final ComboBox<DistrictDTO> districtFilter = new ComboBox<>("Filter by District");
    private final Map<UUID, String> districtNamesById = new HashMap<>();
    private int currentPage = 0;

    public UpazilaListView(UpazilaClient client, DistrictClient districtClient) {
        this.client = client;
        this.districtClient = districtClient;
        setSizeFull();

        List<DistrictDTO> districts = districtClient.getAll(0, REFERENCE_DATA_PAGE_SIZE).getContent();
        districts.forEach(d -> districtNamesById.put(d.getId(), d.getName()));
        districtFilter.setItemLabelGenerator(DistrictDTO::getName);
        districtFilter.setItems(districts);
        districtFilter.setClearButtonVisible(true);
        districtFilter.addValueChangeListener(e -> {
            currentPage = 0;
            refresh();
        });

        grid.addColumn(UpazilaDTO::getName).setHeader("Name");
        grid.addColumn(UpazilaDTO::getCode).setHeader("Code");
        grid.addColumn(u -> districtNamesById.getOrDefault(u.getDistrictId(), "-")).setHeader("District");
        grid.addComponentColumn(this::actions).setHeader("Actions").setAutoWidth(true).setFlexGrow(0);
        grid.setSizeFull();

        Button add = new Button("Add Upazila", e -> openForm(null));
        add.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        HorizontalLayout toolbar = new HorizontalLayout(districtFilter, add);
        toolbar.setWidthFull();
        toolbar.setAlignItems(Alignment.END);
        toolbar.setJustifyContentMode(JustifyContentMode.BETWEEN);

        add(new H2("Upazilas"), toolbar, grid, pagination);
        setFlexGrow(1, grid);

        refresh();
    }

    private HorizontalLayout actions(UpazilaDTO upazila) {
        Button edit = new Button(new Icon(VaadinIcon.EDIT), e -> openForm(upazila));
        edit.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        Button delete = new Button(new Icon(VaadinIcon.TRASH), e -> confirmDelete(upazila));
        delete.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ERROR);
        return new HorizontalLayout(edit, delete);
    }

    private void openForm(UpazilaDTO existing) {
        new UpazilaFormDialog(client, districtClient, existing, this::refresh).open();
    }

    private void confirmDelete(UpazilaDTO upazila) {
        ConfirmDialog confirm = new ConfirmDialog();
        confirm.setHeader("Delete Upazila");
        confirm.setText("Delete \"" + upazila.getName() + "\"? This cannot be undone.");
        confirm.setCancelable(true);
        confirm.setConfirmText("Delete");
        confirm.setConfirmButtonTheme("error primary");
        confirm.addConfirmListener(e -> delete(upazila));
        confirm.open();
    }

    private void delete(UpazilaDTO upazila) {
        try {
            client.delete(upazila.getId());
            Notification.show("Upazila deleted");
            refresh();
        } catch (GatewayApiException ex) {
            Notification errorNotification = Notification.show("Could not delete upazila: " + ex.getMessage(),
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
        DistrictDTO selectedDistrict = districtFilter.getValue();
        PageResponseDTO<UpazilaDTO> result = selectedDistrict == null
                ? client.getAll(currentPage, PAGE_SIZE)
                : client.getByDistrict(selectedDistrict.getId(), currentPage, PAGE_SIZE);
        grid.setItems(result.getContent());
        pagination.update(result.getNumber(), result.getTotalPages());
    }
}
