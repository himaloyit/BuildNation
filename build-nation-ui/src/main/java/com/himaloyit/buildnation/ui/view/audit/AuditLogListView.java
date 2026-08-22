package com.himaloyit.buildnation.ui.view.audit;

import com.himaloyit.buildnation.ui.client.audit.AuditLogClient;
import com.himaloyit.buildnation.ui.client.support.GatewayApiException;
import com.himaloyit.buildnation.ui.component.PaginationBar;
import com.himaloyit.buildnation.ui.dto.PageResponseDTO;
import com.himaloyit.buildnation.ui.dto.audit.AuditLogDTO;
import com.himaloyit.buildnation.ui.view.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import jakarta.annotation.security.PermitAll;

import java.util.UUID;

/**
 * Read-only List/Search/View for AuditLog — no Add/Edit/Delete, matching the backend's own
 * append-only design (only create/get/list endpoints exist there; see [[cdm_service]]). Search
 * covers the three real backend query capabilities: by entity type (+ optional entity id), and by
 * performer — nothing beyond what the backend actually supports is offered.
 */
@Route(value = "audit-logs", layout = MainLayout.class)
@PageTitle("Audit Trail | BuildNation")
@PermitAll
public class AuditLogListView extends VerticalLayout {

    private static final int PAGE_SIZE = 20;

    private final AuditLogClient client;
    private final Grid<AuditLogDTO> grid = new Grid<>(AuditLogDTO.class, false);
    private final PaginationBar pagination = new PaginationBar(this::previousPage, this::nextPage);
    private final TextField entityTypeFilter = new TextField("Entity Type");
    private final TextField entityIdFilter = new TextField("Entity ID (optional, needs Entity Type)");
    private final TextField performedByFilter = new TextField("Performed By");
    private int currentPage = 0;

    public AuditLogListView(AuditLogClient client) {
        this.client = client;
        setSizeFull();

        entityTypeFilter.setClearButtonVisible(true);
        entityIdFilter.setClearButtonVisible(true);
        performedByFilter.setClearButtonVisible(true);

        Button search = new Button("Search", e -> {
            currentPage = 0;
            refresh();
        });
        search.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button clear = new Button("Clear", e -> {
            entityTypeFilter.clear();
            entityIdFilter.clear();
            performedByFilter.clear();
            currentPage = 0;
            refresh();
        });

        HorizontalLayout toolbar = new HorizontalLayout(entityTypeFilter, entityIdFilter, performedByFilter, search, clear);
        toolbar.setAlignItems(Alignment.END);
        toolbar.setWidthFull();

        grid.addColumn(AuditLogDTO::getEntityType).setHeader("Entity Type");
        grid.addColumn(AuditLogDTO::getEntityId).setHeader("Entity ID");
        grid.addColumn(a -> a.getAction() == null ? "-" : a.getAction().name()).setHeader("Action");
        grid.addColumn(AuditLogDTO::getPerformedBy).setHeader("Performed By");
        grid.addColumn(AuditLogDTO::getPerformedAt).setHeader("Performed At");
        grid.addComponentColumn(this::actions).setHeader("Actions").setAutoWidth(true).setFlexGrow(0);
        grid.setSizeFull();

        add(new H2("Audit Trail"),
                new Paragraph("Read-only history of create/update/delete actions recorded by the backend. "
                        + "Not every action in the system is captured yet — automatic logging is only wired "
                        + "in for the entities that call it explicitly."),
                toolbar, grid, pagination);
        setFlexGrow(1, grid);

        refresh();
    }

    private HorizontalLayout actions(AuditLogDTO auditLog) {
        Button view = new Button(new Icon(VaadinIcon.EYE), e -> new AuditLogDetailDialog(auditLog).open());
        view.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        return new HorizontalLayout(view);
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
        String entityType = blankToNull(entityTypeFilter.getValue());
        String entityIdText = blankToNull(entityIdFilter.getValue());
        String performedBy = blankToNull(performedByFilter.getValue());

        try {
            PageResponseDTO<AuditLogDTO> result;
            if (entityType != null && entityIdText != null) {
                result = client.getByEntity(entityType, UUID.fromString(entityIdText), currentPage, PAGE_SIZE);
            } else if (entityType != null) {
                result = client.getByEntityType(entityType, currentPage, PAGE_SIZE);
            } else if (performedBy != null) {
                result = client.getByPerformedBy(performedBy, currentPage, PAGE_SIZE);
            } else {
                result = client.getAll(currentPage, PAGE_SIZE);
            }
            grid.setItems(result.getContent());
            pagination.update(result.getNumber(), result.getTotalPages());
        } catch (IllegalArgumentException ex) {
            Notification errorNotification = Notification.show("Entity ID must be a valid UUID",
                    4000, Notification.Position.MIDDLE);
            errorNotification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        } catch (GatewayApiException ex) {
            Notification errorNotification = Notification.show("Could not load audit logs: " + ex.getMessage(),
                    5000, Notification.Position.MIDDLE);
            errorNotification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private String blankToNull(String value) {
        return (value == null || value.isBlank()) ? null : value.trim();
    }
}
