package com.himaloyit.buildnation.ui.view;

import com.himaloyit.buildnation.ui.security.AuthenticatedPrincipal;
import com.himaloyit.buildnation.ui.view.dashboard.DashboardView;
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
        return nav;
    }
}
