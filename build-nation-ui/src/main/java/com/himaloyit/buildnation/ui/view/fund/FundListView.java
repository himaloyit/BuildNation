package com.himaloyit.buildnation.ui.view.fund;

import com.himaloyit.buildnation.ui.client.fund.FundClient;
import com.himaloyit.buildnation.ui.client.prj.CategoryClient;
import com.himaloyit.buildnation.ui.client.prj.SubCategoryClient;
import com.himaloyit.buildnation.ui.client.support.GatewayApiException;
import com.himaloyit.buildnation.ui.component.PaginationBar;
import com.himaloyit.buildnation.ui.dto.PageResponseDTO;
import com.himaloyit.buildnation.ui.dto.fund.FundDTO;
import com.himaloyit.buildnation.ui.dto.prj.CategoryDTO;
import com.himaloyit.buildnation.ui.dto.prj.SubCategoryDTO;
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

/** List/Search/View/Add/Edit/Delete for Fund, filterable by Category. */
@Route(value = "funds", layout = MainLayout.class)
@PageTitle("Funds | BuildNation")
public class FundListView extends VerticalLayout {

    private static final int PAGE_SIZE = 20;
    private static final int REFERENCE_DATA_PAGE_SIZE = 500;

    private final FundClient client;
    private final CategoryClient categoryClient;
    private final SubCategoryClient subCategoryClient;
    private final Grid<FundDTO> grid = new Grid<>(FundDTO.class, false);
    private final PaginationBar pagination = new PaginationBar(this::previousPage, this::nextPage);
    private final ComboBox<CategoryDTO> categoryFilter = new ComboBox<>("Filter by Category");
    private final Map<UUID, String> categoryNamesById = new HashMap<>();
    private final Map<UUID, String> subCategoryNamesById = new HashMap<>();
    private int currentPage = 0;

    public FundListView(FundClient client, CategoryClient categoryClient, SubCategoryClient subCategoryClient) {
        this.client = client;
        this.categoryClient = categoryClient;
        this.subCategoryClient = subCategoryClient;
        setSizeFull();

        List<CategoryDTO> categories = categoryClient.getAll(0, REFERENCE_DATA_PAGE_SIZE).getContent();
        categories.forEach(c -> categoryNamesById.put(c.getId(), c.getName()));
        List<SubCategoryDTO> subCategories = subCategoryClient.getAll(0, REFERENCE_DATA_PAGE_SIZE).getContent();
        subCategories.forEach(s -> subCategoryNamesById.put(s.getId(), s.getName()));

        categoryFilter.setItemLabelGenerator(CategoryDTO::getName);
        categoryFilter.setItems(categories);
        categoryFilter.setClearButtonVisible(true);
        categoryFilter.addValueChangeListener(e -> {
            currentPage = 0;
            refresh();
        });

        grid.addColumn(FundDTO::getMonth).setHeader("Month");
        grid.addColumn(FundDTO::getFundType).setHeader("Fund Type");
        grid.addColumn(f -> categoryNamesById.getOrDefault(f.getCategoryId(), "-")).setHeader("Category");
        grid.addColumn(f -> subCategoryNamesById.getOrDefault(f.getSubCategoryId(), "-")).setHeader("SubCategory");
        grid.addColumn(FundDTO::getReceivedAmount).setHeader("Received");
        grid.addColumn(FundDTO::getAllocatedAmount).setHeader("Allocated");
        grid.addColumn(FundDTO::getSpentAmount).setHeader("Spent");
        grid.addColumn(FundDTO::getRemainingAmount).setHeader("Remaining");
        grid.addComponentColumn(this::actions).setHeader("Actions").setAutoWidth(true).setFlexGrow(0);
        grid.setSizeFull();

        Button add = new Button("Add Fund", e -> openForm(null));
        add.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        HorizontalLayout toolbar = new HorizontalLayout(categoryFilter, add);
        toolbar.setWidthFull();
        toolbar.setAlignItems(Alignment.END);
        toolbar.setJustifyContentMode(JustifyContentMode.BETWEEN);

        add(new H2("Funds"), toolbar, grid, pagination);
        setFlexGrow(1, grid);

        refresh();
    }

    private HorizontalLayout actions(FundDTO fund) {
        Button edit = new Button(new Icon(VaadinIcon.EDIT), e -> openForm(fund));
        edit.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        Button delete = new Button(new Icon(VaadinIcon.TRASH), e -> confirmDelete(fund));
        delete.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ERROR);
        return new HorizontalLayout(edit, delete);
    }

    private void openForm(FundDTO existing) {
        new FundFormDialog(client, categoryClient, subCategoryClient, existing, this::refresh).open();
    }

    private void confirmDelete(FundDTO fund) {
        ConfirmDialog confirm = new ConfirmDialog();
        confirm.setHeader("Delete Fund");
        confirm.setText("Delete the " + fund.getFundType() + " fund for " + fund.getMonth() + "? This cannot be undone.");
        confirm.setCancelable(true);
        confirm.setConfirmText("Delete");
        confirm.setConfirmButtonTheme("error primary");
        confirm.addConfirmListener(e -> delete(fund));
        confirm.open();
    }

    private void delete(FundDTO fund) {
        try {
            client.delete(fund.getId());
            Notification.show("Fund deleted");
            refresh();
        } catch (GatewayApiException ex) {
            Notification errorNotification = Notification.show("Could not delete fund: " + ex.getMessage(),
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
        PageResponseDTO<FundDTO> result = selectedCategory == null
                ? client.getAll(currentPage, PAGE_SIZE)
                : client.getByCategory(selectedCategory.getId(), currentPage, PAGE_SIZE);
        grid.setItems(result.getContent());
        pagination.update(result.getNumber(), result.getTotalPages());
    }
}
