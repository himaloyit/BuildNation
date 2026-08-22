package com.himaloyit.buildnation.ui.view.member;

import com.himaloyit.buildnation.ui.client.member.CommunicationPreferenceClient;
import com.himaloyit.buildnation.ui.client.member.MemberClient;
import com.himaloyit.buildnation.ui.client.member.MemberProfileClient;
import com.himaloyit.buildnation.ui.client.support.GatewayApiException;
import com.himaloyit.buildnation.ui.component.PaginationBar;
import com.himaloyit.buildnation.ui.dto.PageResponseDTO;
import com.himaloyit.buildnation.ui.dto.member.MemberDTO;
import com.himaloyit.buildnation.ui.dto.member.MemberRole;
import com.himaloyit.buildnation.ui.dto.member.MemberStatus;
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

/** List/Search/View/Add/Edit/Delete for Member, filterable by Role or Status. */
@Route(value = "members", layout = MainLayout.class)
@PageTitle("Members | BuildNation")
@PermitAll
public class MemberListView extends VerticalLayout {

    private static final int PAGE_SIZE = 20;

    private final MemberClient client;
    private final MemberProfileClient profileClient;
    private final CommunicationPreferenceClient preferenceClient;
    private final Grid<MemberDTO> grid = new Grid<>(MemberDTO.class, false);
    private final PaginationBar pagination = new PaginationBar(this::previousPage, this::nextPage);
    private final ComboBox<MemberRole> roleFilter = new ComboBox<>("Filter by Role");
    private final ComboBox<MemberStatus> statusFilter = new ComboBox<>("Filter by Status");
    private int currentPage = 0;

    public MemberListView(MemberClient client, MemberProfileClient profileClient,
                           CommunicationPreferenceClient preferenceClient) {
        this.client = client;
        this.profileClient = profileClient;
        this.preferenceClient = preferenceClient;
        setSizeFull();

        roleFilter.setItems(MemberRole.values());
        roleFilter.setClearButtonVisible(true);
        roleFilter.addValueChangeListener(e -> {
            if (e.getValue() != null) {
                statusFilter.clear();
            }
            currentPage = 0;
            refresh();
        });

        statusFilter.setItems(MemberStatus.values());
        statusFilter.setClearButtonVisible(true);
        statusFilter.addValueChangeListener(e -> {
            if (e.getValue() != null) {
                roleFilter.clear();
            }
            currentPage = 0;
            refresh();
        });

        grid.addColumn(MemberDTO::getFullName).setHeader("Full Name");
        grid.addColumn(MemberDTO::getEmail).setHeader("Email");
        grid.addColumn(MemberDTO::getPhone).setHeader("Phone");
        grid.addColumn(MemberDTO::getPosition).setHeader("Position");
        grid.addColumn(m -> m.getRole() == null ? "-" : m.getRole().name()).setHeader("Role");
        grid.addColumn(m -> m.getStatus() == null ? "-" : m.getStatus().name()).setHeader("Status");
        grid.addComponentColumn(this::actions).setHeader("Actions").setAutoWidth(true).setFlexGrow(0);
        grid.setSizeFull();

        Button add = new Button("Add Member", e -> openForm(null));
        add.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        HorizontalLayout toolbar = new HorizontalLayout(roleFilter, statusFilter, add);
        toolbar.setWidthFull();
        toolbar.setAlignItems(Alignment.END);
        toolbar.setJustifyContentMode(JustifyContentMode.BETWEEN);

        add(new H2("Members"), toolbar, grid, pagination);
        setFlexGrow(1, grid);

        refresh();
    }

    private HorizontalLayout actions(MemberDTO member) {
        Button edit = new Button(new Icon(VaadinIcon.EDIT), e -> openForm(member));
        edit.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        edit.getElement().setAttribute("title", "Edit");

        Button role = new Button(new Icon(VaadinIcon.USER_STAR), e -> new MemberRoleChangeDialog(client, member, this::refresh).open());
        role.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        role.getElement().setAttribute("title", "Change Role");

        Button status = new Button(new Icon(VaadinIcon.REFRESH), e -> new MemberStatusChangeDialog(client, member, this::refresh).open());
        status.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        status.getElement().setAttribute("title", "Change Status");

        Button profile = new Button(new Icon(VaadinIcon.USER_CARD), e -> new MemberProfileFormDialog(profileClient, member, this::refresh).open());
        profile.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        profile.getElement().setAttribute("title", "Profile");

        Button preferences = new Button(new Icon(VaadinIcon.ENVELOPE), e -> new CommunicationPreferenceFormDialog(preferenceClient, member, this::refresh).open());
        preferences.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        preferences.getElement().setAttribute("title", "Communication Preferences");

        Button delete = new Button(new Icon(VaadinIcon.TRASH), e -> confirmDelete(member));
        delete.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ERROR);
        delete.getElement().setAttribute("title", "Delete");

        return new HorizontalLayout(edit, role, status, profile, preferences, delete);
    }

    private void openForm(MemberDTO existing) {
        new MemberFormDialog(client, existing, this::refresh).open();
    }

    private void confirmDelete(MemberDTO member) {
        ConfirmDialog confirm = new ConfirmDialog();
        confirm.setHeader("Delete Member");
        confirm.setText("Delete \"" + member.getFullName() + "\"? This cannot be undone.");
        confirm.setCancelable(true);
        confirm.setConfirmText("Delete");
        confirm.setConfirmButtonTheme("error primary");
        confirm.addConfirmListener(e -> delete(member));
        confirm.open();
    }

    private void delete(MemberDTO member) {
        try {
            client.delete(member.getId());
            Notification.show("Member deleted");
            refresh();
        } catch (GatewayApiException ex) {
            Notification errorNotification = Notification.show("Could not delete member: " + ex.getMessage(),
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
        MemberRole selectedRole = roleFilter.getValue();
        MemberStatus selectedStatus = statusFilter.getValue();
        PageResponseDTO<MemberDTO> result;
        if (selectedRole != null) {
            result = client.getByRole(selectedRole, currentPage, PAGE_SIZE);
        } else if (selectedStatus != null) {
            result = client.getByStatus(selectedStatus, currentPage, PAGE_SIZE);
        } else {
            result = client.getAll(currentPage, PAGE_SIZE);
        }
        grid.setItems(result.getContent());
        pagination.update(result.getNumber(), result.getTotalPages());
    }
}
