package com.himaloyit.buildnation.ui.view.fund;

import com.himaloyit.buildnation.ui.client.fund.FundAllocationClient;
import com.himaloyit.buildnation.ui.client.fund.FundClient;
import com.himaloyit.buildnation.ui.client.prj.ProjectClient;
import com.himaloyit.buildnation.ui.client.support.GatewayApiException;
import com.himaloyit.buildnation.ui.dto.fund.CreateFundAllocationRequest;
import com.himaloyit.buildnation.ui.dto.fund.FundAllocationDTO;
import com.himaloyit.buildnation.ui.dto.fund.FundDTO;
import com.himaloyit.buildnation.ui.dto.prj.ProjectDTO;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.data.binder.Binder;

import java.util.List;

/**
 * Add-only dialog for a FundAllocation — the backend has no update endpoint (immutable by design:
 * delete and recreate to change an amount), so there is no corresponding Edit dialog.
 */
public class FundAllocationFormDialog extends Dialog {

    private static final int REFERENCE_DATA_PAGE_SIZE = 500;

    private final Binder<FundAllocationDTO> binder = new Binder<>(FundAllocationDTO.class);
    private final List<FundDTO> funds;
    private final List<ProjectDTO> projects;

    public FundAllocationFormDialog(FundAllocationClient client, FundClient fundClient, ProjectClient projectClient,
                                     Runnable onSaved) {
        setHeaderTitle("Add Fund Allocation");
        this.funds = fundClient.getAll(0, REFERENCE_DATA_PAGE_SIZE).getContent();
        this.projects = projectClient.getAll(0, REFERENCE_DATA_PAGE_SIZE).getContent();

        ComboBox<FundDTO> fund = new ComboBox<>("Fund");
        fund.setItemLabelGenerator(f -> f.getMonth() + " - " + f.getFundType());
        fund.setItems(funds);

        ComboBox<ProjectDTO> project = new ComboBox<>("Project");
        project.setItemLabelGenerator(ProjectDTO::getName);
        project.setItems(projects);

        BigDecimalField amount = new BigDecimalField("Amount");

        FundAllocationDTO working = new FundAllocationDTO();

        binder.forField(fund).asRequired("Fund is mandatory")
                .bind(dto -> findFundOf(dto.getFundId()), (dto, selected) -> dto.setFundId(selected == null ? null : selected.getId()));
        binder.forField(project).asRequired("Project is mandatory")
                .bind(dto -> findProjectOf(dto.getProjectId()), (dto, selected) -> dto.setProjectId(selected == null ? null : selected.getId()));
        binder.forField(amount).asRequired("Amount is mandatory")
                .bind(FundAllocationDTO::getAmount, FundAllocationDTO::setAmount);

        binder.readBean(working);

        FormLayout form = new FormLayout(fund, project, amount);
        add(form);

        Button cancel = new Button("Cancel", e -> close());
        Button save = new Button("Save", e -> save(client, working, onSaved));
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        getFooter().add(cancel, save);
    }

    private FundDTO findFundOf(java.util.UUID fundId) {
        if (fundId == null) {
            return null;
        }
        return funds.stream().filter(f -> f.getId().equals(fundId)).findFirst().orElse(null);
    }

    private ProjectDTO findProjectOf(java.util.UUID projectId) {
        if (projectId == null) {
            return null;
        }
        return projects.stream().filter(p -> p.getId().equals(projectId)).findFirst().orElse(null);
    }

    private void save(FundAllocationClient client, FundAllocationDTO working, Runnable onSaved) {
        if (!binder.writeBeanIfValid(working)) {
            return;
        }
        try {
            client.create(new CreateFundAllocationRequest(working.getFundId(), working.getProjectId(), working.getAmount()));
            Notification.show("Fund allocation saved");
            onSaved.run();
            close();
        } catch (GatewayApiException ex) {
            Notification errorNotification = Notification.show("Could not save fund allocation: " + ex.getMessage(),
                    5000, Notification.Position.MIDDLE);
            errorNotification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }
}
