package com.himaloyit.buildnation.ui.view.fund;

import com.himaloyit.buildnation.ui.client.fund.FundAllocationClient;
import com.himaloyit.buildnation.ui.client.fund.FundClient;
import com.himaloyit.buildnation.ui.client.prj.ProjectClient;
import com.himaloyit.buildnation.ui.client.support.GatewayApiException;
import com.himaloyit.buildnation.ui.component.PaginationBar;
import com.himaloyit.buildnation.ui.dto.PageResponseDTO;
import com.himaloyit.buildnation.ui.dto.fund.FundAllocationDTO;
import com.himaloyit.buildnation.ui.dto.fund.FundDTO;
import com.himaloyit.buildnation.ui.dto.prj.ProjectDTO;
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

/**
 * List/Search/View/Add/Delete for FundAllocation, filterable by Fund or Project. No Edit — the
 * backend has no update endpoint (immutable: delete and recreate to change an amount).
 */
@Route(value = "fund-allocations", layout = MainLayout.class)
@PageTitle("Fund Allocations | BuildNation")
public class FundAllocationListView extends VerticalLayout {

    private static final int PAGE_SIZE = 20;
    private static final int REFERENCE_DATA_PAGE_SIZE = 500;

    private final FundAllocationClient client;
    private final FundClient fundClient;
    private final ProjectClient projectClient;
    private final Grid<FundAllocationDTO> grid = new Grid<>(FundAllocationDTO.class, false);
    private final PaginationBar pagination = new PaginationBar(this::previousPage, this::nextPage);
    private final ComboBox<FundDTO> fundFilter = new ComboBox<>("Filter by Fund");
    private final ComboBox<ProjectDTO> projectFilter = new ComboBox<>("Filter by Project");
    private final Map<UUID, String> fundLabelsById = new HashMap<>();
    private final Map<UUID, String> projectNamesById = new HashMap<>();
    private int currentPage = 0;

    public FundAllocationListView(FundAllocationClient client, FundClient fundClient, ProjectClient projectClient) {
        this.client = client;
        this.fundClient = fundClient;
        this.projectClient = projectClient;
        setSizeFull();

        List<FundDTO> funds = fundClient.getAll(0, REFERENCE_DATA_PAGE_SIZE).getContent();
        funds.forEach(f -> fundLabelsById.put(f.getId(), f.getMonth() + " - " + f.getFundType()));
        List<ProjectDTO> projects = projectClient.getAll(0, REFERENCE_DATA_PAGE_SIZE).getContent();
        projects.forEach(p -> projectNamesById.put(p.getId(), p.getName()));

        fundFilter.setItemLabelGenerator(f -> f.getMonth() + " - " + f.getFundType());
        fundFilter.setItems(funds);
        fundFilter.setClearButtonVisible(true);
        fundFilter.addValueChangeListener(e -> {
            if (e.getValue() != null) {
                projectFilter.clear();
            }
            currentPage = 0;
            refresh();
        });

        projectFilter.setItemLabelGenerator(ProjectDTO::getName);
        projectFilter.setItems(projects);
        projectFilter.setClearButtonVisible(true);
        projectFilter.addValueChangeListener(e -> {
            if (e.getValue() != null) {
                fundFilter.clear();
            }
            currentPage = 0;
            refresh();
        });

        grid.addColumn(a -> fundLabelsById.getOrDefault(a.getFundId(), "-")).setHeader("Fund");
        grid.addColumn(a -> projectNamesById.getOrDefault(a.getProjectId(), "-")).setHeader("Project");
        grid.addColumn(FundAllocationDTO::getAmount).setHeader("Amount");
        grid.addComponentColumn(this::actions).setHeader("Actions").setAutoWidth(true).setFlexGrow(0);
        grid.setSizeFull();

        Button add = new Button("Add Allocation", e -> openForm());
        add.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        HorizontalLayout toolbar = new HorizontalLayout(fundFilter, projectFilter, add);
        toolbar.setWidthFull();
        toolbar.setAlignItems(Alignment.END);
        toolbar.setJustifyContentMode(JustifyContentMode.BETWEEN);

        add(new H2("Fund Allocations"), toolbar, grid, pagination);
        setFlexGrow(1, grid);

        refresh();
    }

    private HorizontalLayout actions(FundAllocationDTO allocation) {
        Button delete = new Button(new Icon(VaadinIcon.TRASH), e -> confirmDelete(allocation));
        delete.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ERROR);
        return new HorizontalLayout(delete);
    }

    private void openForm() {
        new FundAllocationFormDialog(client, fundClient, projectClient, this::refresh).open();
    }

    private void confirmDelete(FundAllocationDTO allocation) {
        ConfirmDialog confirm = new ConfirmDialog();
        confirm.setHeader("Delete Fund Allocation");
        confirm.setText("Delete this allocation of " + allocation.getAmount() + "? This reverses the fund's "
                + "allocated balance but does not revert the project's status.");
        confirm.setCancelable(true);
        confirm.setConfirmText("Delete");
        confirm.setConfirmButtonTheme("error primary");
        confirm.addConfirmListener(e -> delete(allocation));
        confirm.open();
    }

    private void delete(FundAllocationDTO allocation) {
        try {
            client.delete(allocation.getId());
            Notification.show("Fund allocation deleted");
            refresh();
        } catch (GatewayApiException ex) {
            Notification errorNotification = Notification.show("Could not delete fund allocation: " + ex.getMessage(),
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
        FundDTO selectedFund = fundFilter.getValue();
        ProjectDTO selectedProject = projectFilter.getValue();
        PageResponseDTO<FundAllocationDTO> result;
        if (selectedFund != null) {
            result = client.getByFund(selectedFund.getId(), currentPage, PAGE_SIZE);
        } else if (selectedProject != null) {
            result = client.getByProject(selectedProject.getId(), currentPage, PAGE_SIZE);
        } else {
            result = client.getAll(currentPage, PAGE_SIZE);
        }
        grid.setItems(result.getContent());
        pagination.update(result.getNumber(), result.getTotalPages());
    }
}
