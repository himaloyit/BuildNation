package com.himaloyit.buildnation.ui.view.dashboard;

import com.himaloyit.buildnation.ui.view.MainLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;

/**
 * Landing page. Summary widgets (project/fund statistics, recent projects, etc.
 * per §9 of the UI prompt) are added once authentication (Phase 4) and the
 * constituency-development-management client (Phase 5) exist to source real data —
 * no fabricated numbers are shown before then.
 */
@Route(value = "", layout = MainLayout.class)
@RouteAlias(value = "dashboard", layout = MainLayout.class)
@PageTitle("Dashboard | BuildNation")
public class DashboardView extends VerticalLayout {

    public DashboardView() {
        add(new H2("Dashboard"));
        add(new Paragraph("Summary widgets will appear here once authentication and the "
                + "backend API clients are wired in."));
    }
}
