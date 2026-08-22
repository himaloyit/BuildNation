package com.himaloyit.buildnation.ui.view.prj;

import com.himaloyit.buildnation.ui.client.prj.CategoryClient;
import com.himaloyit.buildnation.ui.client.prj.ProjectClient;
import com.himaloyit.buildnation.ui.client.support.GatewayApiException;
import com.himaloyit.buildnation.ui.component.PaginationBar;
import com.himaloyit.buildnation.ui.dto.PageResponseDTO;
import com.himaloyit.buildnation.ui.dto.prj.CategoryDTO;
import com.himaloyit.buildnation.ui.dto.prj.ProjectDTO;
import com.himaloyit.buildnation.ui.view.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.UUID;

/**
 * The Project priority queue: {@code GET /priority-queue} (ranked by {@code priorityRank}, scoped
 * to NEW/PENDING_APPROVAL/APPROVED projects server-side) plus {@code POST
 * /priority-queue/recalculate}, which the backend uses to (re)assign {@code priorityRank} 1..N by
 * descending {@code priorityScore} — this UI only triggers it and shows the result, all ranking
 * logic lives server-side. Deferred from the initial Project Management pass, built now on request.
 */
@Route(value = "priority-queue", layout = MainLayout.class)
@PageTitle("Priority Queue | BuildNation")
public class PriorityQueueView extends VerticalLayout {

    private static final int PAGE_SIZE = 20;
    private static final int REFERENCE_DATA_PAGE_SIZE = 500;

    private final ProjectClient client;
    private final Grid<ProjectDTO> grid = new Grid<>(ProjectDTO.class, false);
    private final PaginationBar pagination = new PaginationBar(this::previousPage, this::nextPage);
    private final ComboBox<CategoryDTO> categoryFilter = new ComboBox<>("Filter by Category");
    private int currentPage = 0;

    public PriorityQueueView(ProjectClient client, CategoryClient categoryClient) {
        this.client = client;
        setSizeFull();

        categoryFilter.setItemLabelGenerator(CategoryDTO::getName);
        categoryFilter.setItems(categoryClient.getAll(0, REFERENCE_DATA_PAGE_SIZE).getContent());
        categoryFilter.setClearButtonVisible(true);
        categoryFilter.addValueChangeListener(e -> {
            currentPage = 0;
            refresh();
        });

        grid.addColumn(ProjectDTO::getPriorityRank).setHeader("Rank");
        grid.addColumn(ProjectDTO::getName).setHeader("Name");
        grid.addColumn(ProjectDTO::getEstimatedCost).setHeader("Estimated Cost");
        grid.addColumn(ProjectDTO::getPriorityScore).setHeader("Priority Score");
        grid.addColumn(p -> p.getStatus() == null ? "-" : p.getStatus().name()).setHeader("Status");
        grid.addComponentColumn(this::actions).setHeader("Actions").setAutoWidth(true).setFlexGrow(0);
        grid.setSizeFull();

        Button recalculate = new Button("Recalculate Queue", e -> confirmRecalculate());
        recalculate.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        HorizontalLayout toolbar = new HorizontalLayout(categoryFilter, recalculate);
        toolbar.setWidthFull();
        toolbar.setAlignItems(Alignment.END);
        toolbar.setJustifyContentMode(JustifyContentMode.BETWEEN);

        add(new H2("Priority Queue"),
                new Paragraph("Ranked by priority score, scoped to NEW/PENDING_APPROVAL/APPROVED projects. "
                        + "Ranks only update when you recalculate — editing a score alone doesn't reorder the queue."),
                toolbar, grid, pagination);
        setFlexGrow(1, grid);

        refresh();
    }

    private HorizontalLayout actions(ProjectDTO project) {
        Button changeScore = new Button(new Icon(VaadinIcon.EDIT), e -> new ProjectPriorityChangeDialog(client, project, this::refresh).open());
        changeScore.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        changeScore.getElement().setAttribute("title", "Change Priority Score");
        return new HorizontalLayout(changeScore);
    }

    private void confirmRecalculate() {
        ConfirmDialog confirm = new ConfirmDialog();
        confirm.setHeader("Recalculate Priority Queue");
        UUID selectedCategoryId = categoryFilter.getValue() == null ? null : categoryFilter.getValue().getId();
        confirm.setText(selectedCategoryId == null
                ? "Recalculate ranks for all eligible projects across every category?"
                : "Recalculate ranks for eligible projects in \"" + categoryFilter.getValue().getName() + "\" only?");
        confirm.setCancelable(true);
        confirm.setConfirmText("Recalculate");
        confirm.addConfirmListener(e -> recalculate(selectedCategoryId));
        confirm.open();
    }

    private void recalculate(UUID categoryId) {
        try {
            int ranked = client.recalculatePriorityQueue(categoryId);
            Notification.show(ranked + " project(s) ranked");
            currentPage = 0;
            refresh();
        } catch (GatewayApiException ex) {
            Notification errorNotification = Notification.show("Could not recalculate queue: " + ex.getMessage(),
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
        UUID categoryId = selectedCategory == null ? null : selectedCategory.getId();
        PageResponseDTO<ProjectDTO> result = client.getPriorityQueue(categoryId, currentPage, PAGE_SIZE);
        grid.setItems(result.getContent());
        pagination.update(result.getNumber(), result.getTotalPages());
    }
}
