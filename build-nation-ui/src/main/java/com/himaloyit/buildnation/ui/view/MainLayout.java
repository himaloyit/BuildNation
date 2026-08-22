package com.himaloyit.buildnation.ui.view;

import com.himaloyit.buildnation.ui.security.AuthenticatedPrincipal;
import com.himaloyit.buildnation.ui.view.dashboard.DashboardView;
import com.himaloyit.buildnation.ui.view.region.DistrictListView;
import com.himaloyit.buildnation.ui.view.region.UnionListView;
import com.himaloyit.buildnation.ui.view.region.UpazilaListView;
import com.himaloyit.buildnation.ui.view.region.VillageListView;
import com.himaloyit.buildnation.ui.view.region.WardListView;
import com.himaloyit.buildnation.ui.view.prj.CategoryListView;
import com.himaloyit.buildnation.ui.view.prj.PriorityQueueView;
import com.himaloyit.buildnation.ui.view.prj.ProjectListView;
import com.himaloyit.buildnation.ui.view.prj.SubCategoryListView;
import com.himaloyit.buildnation.ui.view.fund.FundAllocationListView;
import com.himaloyit.buildnation.ui.view.fund.FundListView;
import com.himaloyit.buildnation.ui.view.contractor.ContractorListView;
import com.himaloyit.buildnation.ui.view.wo.InspectionListView;
import com.himaloyit.buildnation.ui.view.wo.PaymentListView;
import com.himaloyit.buildnation.ui.view.wo.WorkOrderListView;
import com.himaloyit.buildnation.ui.view.audit.AuditLogListView;
import com.himaloyit.buildnation.ui.view.member.MemberListView;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.spring.security.AuthenticationContext;
import com.vaadin.flow.theme.lumo.LumoUtility;

/**
 * Application shell: header (title + user info + logout) and a side navigation drawer.
 * Menu items appear here only once the module they link to actually exists — see
 * Doc/Prompt/Build_Nation_Vaadin_UI_Prompt.docx §8.
 */
public class MainLayout extends AppLayout implements RouterLayout {

    public MainLayout(AuthenticationContext authenticationContext) {
        addToNavbar(header(authenticationContext));
        addToDrawer(navigation());
    }

    private HorizontalLayout header(AuthenticationContext authenticationContext) {
        DrawerToggle toggle = new DrawerToggle();

        H1 title = new H1("BuildNation");
        title.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);

        HorizontalLayout titleGroup = new HorizontalLayout(toggle, title);
        titleGroup.setAlignItems(HorizontalLayout.Alignment.CENTER);

        String displayName = authenticationContext.getAuthenticatedUser(AuthenticatedPrincipal.class)
                .map(user -> user.getPrincipal().getPrincipalName())
                .orElse("Guest");

        Avatar avatar = new Avatar(displayName);
        Span userName = new Span(displayName);

        Button logout = new Button("Logout", event -> authenticationContext.logout());
        logout.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        HorizontalLayout userInfo = new HorizontalLayout(avatar, userName, logout);
        userInfo.setAlignItems(HorizontalLayout.Alignment.CENTER);
        userInfo.addClassNames(LumoUtility.Margin.Right.MEDIUM);

        HorizontalLayout header = new HorizontalLayout(titleGroup, userInfo);
        header.setWidthFull();
        header.setAlignItems(HorizontalLayout.Alignment.CENTER);
        header.setJustifyContentMode(HorizontalLayout.JustifyContentMode.BETWEEN);
        header.addClassNames(LumoUtility.Padding.Horizontal.MEDIUM);
        return header;
    }

    private SideNav navigation() {
        SideNav nav = new SideNav();
        nav.addItem(new SideNavItem("Dashboard", DashboardView.class, VaadinIcon.DASHBOARD.create()));

        nav.addItem(new SideNavItem("Members", MemberListView.class, VaadinIcon.GROUP.create()));

        SideNavItem region = new SideNavItem("Region");
        region.setPrefixComponent(VaadinIcon.MAP_MARKER.create());
        region.addItem(new SideNavItem("Districts", DistrictListView.class));
        region.addItem(new SideNavItem("Upazilas", UpazilaListView.class));
        region.addItem(new SideNavItem("Unions", UnionListView.class));
        region.addItem(new SideNavItem("Wards", WardListView.class));
        region.addItem(new SideNavItem("Villages", VillageListView.class));
        nav.addItem(region);

        SideNavItem projects = new SideNavItem("Projects");
        projects.setPrefixComponent(VaadinIcon.CLIPBOARD_TEXT.create());
        projects.addItem(new SideNavItem("Categories", CategoryListView.class));
        projects.addItem(new SideNavItem("SubCategories", SubCategoryListView.class));
        projects.addItem(new SideNavItem("Projects", ProjectListView.class));
        projects.addItem(new SideNavItem("Priority Queue", PriorityQueueView.class));
        nav.addItem(projects);

        SideNavItem funds = new SideNavItem("Funds");
        funds.setPrefixComponent(VaadinIcon.MONEY_DEPOSIT.create());
        funds.addItem(new SideNavItem("Funds", FundListView.class));
        funds.addItem(new SideNavItem("Fund Allocations", FundAllocationListView.class));
        nav.addItem(funds);

        nav.addItem(new SideNavItem("Contractors", ContractorListView.class, VaadinIcon.USERS.create()));

        SideNavItem workOrders = new SideNavItem("Work Orders");
        workOrders.setPrefixComponent(VaadinIcon.CLIPBOARD_CHECK.create());
        workOrders.addItem(new SideNavItem("Work Orders", WorkOrderListView.class));
        workOrders.addItem(new SideNavItem("Payments", PaymentListView.class));
        workOrders.addItem(new SideNavItem("Inspections", InspectionListView.class));
        nav.addItem(workOrders);

        nav.addItem(new SideNavItem("Audit Trail", AuditLogListView.class, VaadinIcon.RECORDS.create()));

        return nav;
    }
}
