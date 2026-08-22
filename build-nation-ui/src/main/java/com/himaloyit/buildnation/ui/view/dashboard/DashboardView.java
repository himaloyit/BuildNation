package com.himaloyit.buildnation.ui.view.dashboard;

import com.himaloyit.buildnation.ui.client.contractor.ContractorClient;
import com.himaloyit.buildnation.ui.client.fund.FundClient;
import com.himaloyit.buildnation.ui.client.member.MemberClient;
import com.himaloyit.buildnation.ui.client.prj.ProjectClient;
import com.himaloyit.buildnation.ui.client.region.DistrictClient;
import com.himaloyit.buildnation.ui.client.region.UnionClient;
import com.himaloyit.buildnation.ui.client.region.UpazilaClient;
import com.himaloyit.buildnation.ui.client.region.VillageClient;
import com.himaloyit.buildnation.ui.client.region.WardClient;
import com.himaloyit.buildnation.ui.client.wo.WorkOrderClient;
import com.himaloyit.buildnation.ui.component.StatTile;
import com.himaloyit.buildnation.ui.dto.fund.FundDTO;
import com.himaloyit.buildnation.ui.dto.prj.ProjectStatus;
import com.himaloyit.buildnation.ui.view.MainLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;

import jakarta.annotation.security.PermitAll;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.function.LongSupplier;

/**
 * Landing page summary widgets. Every number here comes from a real, live API response fetched
 * through the same Gateway-routed clients the rest of the UI uses — nothing is fabricated (per
 * Doc/Prompt/Build_Nation_Vaadin_UI_Prompt.docx §9). If a backend call fails, that tile shows "—"
 * rather than guessing, and the rest of the dashboard still renders.
 */
@Route(value = "", layout = MainLayout.class)
@RouteAlias(value = "dashboard", layout = MainLayout.class)
@PageTitle("Dashboard | BuildNation")
@PermitAll
public class DashboardView extends VerticalLayout {

    private static final int REFERENCE_DATA_PAGE_SIZE = 500;

    public DashboardView(MemberClient memberClient, ProjectClient projectClient, ContractorClient contractorClient,
                          WorkOrderClient workOrderClient, FundClient fundClient, DistrictClient districtClient,
                          UpazilaClient upazilaClient, UnionClient unionClient, WardClient wardClient,
                          VillageClient villageClient) {
        add(new H2("Dashboard"));

        FlexLayout overview = tileRow();
        overview.add(
                countTile("Members", () -> memberClient.getAll(0, 1).getTotalElements()),
                countTile("Projects", () -> projectClient.getAll(0, 1).getTotalElements()),
                countTile("Contractors", () -> contractorClient.getAll(0, 1).getTotalElements()),
                countTile("Work Orders", () -> workOrderClient.getAll(0, 1).getTotalElements()));
        add(new H3("Overview"), overview);

        FlexLayout region = tileRow();
        region.add(
                countTile("Districts", () -> districtClient.getAll(0, 1).getTotalElements()),
                countTile("Upazilas", () -> upazilaClient.getAll(0, 1).getTotalElements()),
                countTile("Unions", () -> unionClient.getAll(0, 1).getTotalElements()),
                countTile("Wards", () -> wardClient.getAll(0, 1).getTotalElements()),
                countTile("Villages", () -> villageClient.getAll(0, 1).getTotalElements()));
        add(new H3("Region Coverage"), region);

        add(new H3("Fund Summary"), fundSummary(fundClient));

        add(new H3("Projects by Status"), projectsByStatus(projectClient));
    }

    private FlexLayout tileRow() {
        FlexLayout row = new FlexLayout();
        row.setWidthFull();
        row.getStyle().set("gap", "1em").set("flex-wrap", "wrap");
        return row;
    }

    private StatTile countTile(String label, LongSupplier totalSupplier) {
        StatTile tile = new StatTile(label, "…");
        try {
            tile.setValue(String.valueOf(totalSupplier.getAsLong()));
        } catch (Exception ex) {
            tile.setValue("—");
        }
        return tile;
    }

    private FlexLayout fundSummary(FundClient fundClient) {
        FlexLayout row = tileRow();
        StatTile received = new StatTile("Total Received", "…");
        StatTile allocated = new StatTile("Total Allocated", "…");
        StatTile spent = new StatTile("Total Spent", "…");
        StatTile remaining = new StatTile("Total Remaining", "…");
        row.add(received, allocated, spent, remaining);

        try {
            List<FundDTO> funds = fundClient.getAll(0, REFERENCE_DATA_PAGE_SIZE).getContent();
            BigDecimal totalReceived = BigDecimal.ZERO;
            BigDecimal totalAllocated = BigDecimal.ZERO;
            BigDecimal totalSpent = BigDecimal.ZERO;
            BigDecimal totalRemaining = BigDecimal.ZERO;
            for (FundDTO fund : funds) {
                totalReceived = totalReceived.add(nullToZero(fund.getReceivedAmount()));
                totalAllocated = totalAllocated.add(nullToZero(fund.getAllocatedAmount()));
                totalSpent = totalSpent.add(nullToZero(fund.getSpentAmount()));
                totalRemaining = totalRemaining.add(nullToZero(fund.getRemainingAmount()));
            }
            received.setValue(totalReceived.toPlainString());
            allocated.setValue(totalAllocated.toPlainString());
            spent.setValue(totalSpent.toPlainString());
            remaining.setValue(totalRemaining.toPlainString());
        } catch (Exception ex) {
            received.setValue("—");
            allocated.setValue("—");
            spent.setValue("—");
            remaining.setValue("—");
        }
        return row;
    }

    private BigDecimal nullToZero(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private Grid<ProjectStatusCount> projectsByStatus(ProjectClient projectClient) {
        Grid<ProjectStatusCount> grid = new Grid<>(ProjectStatusCount.class, false);
        grid.addColumn(ProjectStatusCount::status).setHeader("Status");
        grid.addColumn(ProjectStatusCount::count).setHeader("Projects");
        grid.setAllRowsVisible(true);
        grid.setWidth("30em");

        List<ProjectStatusCount> counts = new ArrayList<>();
        for (ProjectStatus status : ProjectStatus.values()) {
            long count;
            try {
                count = projectClient.getByStatus(status, 0, 1).getTotalElements();
            } catch (Exception ex) {
                count = -1;
            }
            counts.add(new ProjectStatusCount(status.name(), count < 0 ? "—" : String.valueOf(count)));
        }
        grid.setItems(counts);
        return grid;
    }

    private record ProjectStatusCount(String status, String count) {
    }
}
